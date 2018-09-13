package sk.hudak.prco.parser.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.dto.internal.NewProductInfo;
import sk.hudak.prco.dto.internal.ProductForUpdateData;
import sk.hudak.prco.exception.HttpErrorPrcoRuntimeException;
import sk.hudak.prco.exception.HttpSocketTimeoutPrcoRuntimeException;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.exception.ProductNameNotFoundException;
import sk.hudak.prco.exception.ProductPriceNotFoundException;
import sk.hudak.prco.parser.EshopProductsParser;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static sk.hudak.prco.utils.ThreadUtils.sleepRandomSafeBetween;
import static sk.hudak.prco.utils.URLUtils.buildSearchUrl;

/**
 * Created by jan.hudak on 9/29/2017.
 */
@Slf4j
public abstract class JSoupProductParser implements EshopProductsParser {

    public static final int DEFAULT_TIMOUT_IN_MILIS = 3000;

    protected UnitParser unitParser;
    protected UserAgentDataHolder userAgentDataHolder;

    public JSoupProductParser(@NonNull UnitParser unitParser, @NonNull UserAgentDataHolder userAgentDataHolder) {
        this.unitParser = unitParser;
        this.userAgentDataHolder = userAgentDataHolder;
    }

    @Override
    public List<String> parseUrlsOfProduct(@NonNull String searchKeyWord) {
        String searchUrl = buildSearchUrl(getEshopUuid(), searchKeyWord);
        Document firstPageDocument = retrieveDocument(searchUrl);

        // 1. krok - zistim paging(pocet stranok)
        int countOfAllPages = internalGetCountOfPages(firstPageDocument, searchUrl);
        log.debug("pages count: {}", countOfAllPages);

        // checking for max size
        if (countOfAllPages > getEshopUuid().getMaxCountOfNewPages()) {
            countOfAllPages = getEshopUuid().getMaxCountOfNewPages();
            log.debug("new pages count: {}", countOfAllPages);
        }

        // 2. krok - parsujem prvu stranku
        List<String> firstPageUrls = internalParsePageForProductUrls(firstPageDocument, searchUrl);
        if (firstPageUrls.isEmpty()) {
            log.warn("none products URL found for keyword {}", searchKeyWord);
            return Collections.emptyList();
        }
        List<String> resultUrls = new ArrayList<>();
        resultUrls.addAll(firstPageUrls);

        // 3. krok - ak existuju dalsie stranky parsujem aj tie
        if (countOfAllPages > 1) {
            for (int currentPageNumber = 2; currentPageNumber <= countOfAllPages; currentPageNumber++) {
                resultUrls.addAll(parseNextPage(searchKeyWord, currentPageNumber));
            }
        }
        return resultUrls;
    }

    @Override
    public NewProductInfo parseNewProductInfo(@NonNull String productUrl) {
        //FIXME prepisat tak ako je parseProductUpdateData myslim tym tie optional

        Document document = retrieveDocument(productUrl);

        NewProductInfo.NewProductInfoBuilder builder = NewProductInfo.builder()
                .url(productUrl)
                .eshopUuid(getEshopUuid());

        Optional<String> nameOpt = parseProductNameFromDetail(document);
        logWarningIfNull(nameOpt, "productName", document.location());
        if (!nameOpt.isPresent()) {
            //TODO nemala by tu byt vynimka?
            return builder.build();
        }
        builder.name(nameOpt.get());

        Optional<String> pictureUrl = internalParseProductPictureURL(document, productUrl);
        if (pictureUrl.isPresent()) {
            builder.pictureUrl(pictureUrl.get());
        }

        Optional<UnitTypeValueCount> unitTypeValueCountOpt = parseUnitValueCount(document, nameOpt.get());
        if (!unitTypeValueCountOpt.isPresent()) {
            return builder.build();
        }
        return builder
                .unit(unitTypeValueCountOpt.get().getUnit())
                .unitValue(unitTypeValueCountOpt.get().getValue())
                .unitPackageCount(unitTypeValueCountOpt.get().getPackageCount())
                .build();
    }

    @Override
    public ProductForUpdateData parseProductUpdateData(@NonNull String productUrl) {
        Document document = retrieveDocument(productUrl);

        // ak je produkt nedostupny tak nastavim len url a eshop uuid
        if (isProductUnavailable(document)) {
            log.debug("product is unavailable: {} ", productUrl);
            return ProductForUpdateData.builder()
                    .url(productUrl)
                    .eshopUuid(getEshopUuid()).build();
        }

        Optional<String> productNameOpt = parseProductNameFromDetail(document);
        logWarningIfNull(productNameOpt, "productName", document.location());
        String productName = productNameOpt.orElseThrow(() -> new ProductNameNotFoundException(productUrl));

        Optional<BigDecimal> productPriceForPackageOpt = parseProductPriceForPackage(document);
        logWarningIfNull(productPriceForPackageOpt, "priceForPackage", document.location());
        BigDecimal productPriceForPackage = productPriceForPackageOpt.orElseThrow(() -> new ProductPriceNotFoundException(productUrl));

        Optional<ProductAction> productAction = internalParseProductAction(document, productUrl);

        // platnost akcie
        Optional<Date> productActionValidity = Optional.empty();
        if (productAction.isPresent() && productAction.get().equals(ProductAction.IN_ACTION)) {
            productActionValidity = internalParseProductActionValidity(document, productUrl);
        }

        Optional<String> pictureUrl = internalParseProductPictureURL(document, productUrl);

        return ProductForUpdateData.builder()
                .url(productUrl)
                .eshopUuid(getEshopUuid())
                .name(productName)
                .priceForPackage(productPriceForPackage)

                // FIXME spojit do jedneho ohladne product action
                .productAction(productAction.isPresent() ? productAction.get() : null)
                .actionValidity(productActionValidity.isPresent() ? productActionValidity.get() : null)

                .pictureUrl(pictureUrl.isPresent() ? pictureUrl.get() : null)
                .build();
    }

    protected Document retrieveDocument(String productUrl) {
        try {
            log.debug("request URL: {}", productUrl);

            String userAgent = getUserAgent();
            log.debug("userAgent: {}", userAgent);

            Connection connection = Jsoup.connect(productUrl)
                    .userAgent(userAgent)
                    .timeout(getTimeout());

            if (getCookie() != null && !getCookie().isEmpty()) {
                connection.cookies(getCookie());
            }

            return connection.get();


        } catch (Exception e) {
            //FIXME dane spracovanie urobit tak aby sa dalo v jednotlivych impl overigovat
            String errMsg = "error creating document for url '" + productUrl + "': ";
            if (e instanceof HttpStatusException) {
                HttpStatusException se = (HttpStatusException) e;
                errMsg = errMsg + " " + se.toString();
                log.error(errMsg, e);
                throw new HttpErrorPrcoRuntimeException(se.getStatusCode(), errMsg, e);

            } else if (e instanceof SocketTimeoutException) {
                throw new HttpSocketTimeoutPrcoRuntimeException((SocketTimeoutException) e);

            } else {
                log.error(errMsg, e);
                throw new PrcoRuntimeException(errMsg, e);
            }
        }
    }

    protected List<String> parseNextPage(String searchKeyWord, int currentPageNumber) {
        //FIXME presunut tuto logiku do utils...
        String searchTemplateUrlWithPageNumber = getEshopUuid().getSearchTemplateUrlWithPageNumber();
        String searchUrl;
        if (searchTemplateUrlWithPageNumber.contains("{offset}")) {
            searchUrl = buildSearchUrl(getEshopUuid(), searchKeyWord,
                    (currentPageNumber - 1) * getEshopUuid().getMaxCountOfProductOnPage(),
                    getEshopUuid().getMaxCountOfProductOnPage());
        } else {
            searchUrl = buildSearchUrl(getEshopUuid(), searchKeyWord, currentPageNumber);
        }


        // FIXME 5 a 20 dat nech nacita od konfiguracie pre konkretny eshop
        sleepRandomSafeBetween(5, 20);
        return parsePageForProductUrls(retrieveDocument(searchUrl), currentPageNumber);
    }

    protected Optional<UnitTypeValueCount> parseUnitValueCount(Document document, String productName) {
        return unitParser.parseUnitTypeValueCount(productName);
    }

    protected String getUserAgent() {
        return userAgentDataHolder.getUserAgentForEshop(getEshopUuid());
    }

    protected int getTimeout() {
        return DEFAULT_TIMOUT_IN_MILIS;
    }

    protected Map<String, String> getCookie() {
        return Collections.emptyMap();
    }

    private List<String> internalParsePageForProductUrls(Document firstPageDocument, String searchUrl) {
        try {
            return parsePageForProductUrls(firstPageDocument, 1);
        } catch (Exception e) {
            throw new PrcoRuntimeException("error while parsing pages of products, search URL: " + searchUrl, e);
        }
    }

    private Optional<Date> internalParseProductActionValidity(Document document, String searchUrl) {
        try {
            return parseProductActionValidity(document);
        } catch (Exception e) {
            throw new PrcoRuntimeException("error while parsing product action validity, search URL: " + searchUrl, e);
        }
    }

    private Optional<String> internalParseProductPictureURL(Document document, String productUrl) {
        try {
            return parseProductPictureURL(document);
        } catch (Exception e) {
            throw new PrcoRuntimeException("error while parsing product picture, URL: " + productUrl, e);
        }
    }

    private Optional<ProductAction> internalParseProductAction(Document document, String productUrl) {
        try {
            return parseProductAction(document);
        } catch (Exception e) {
            throw new PrcoRuntimeException("error while parsing product action, URL: " + productUrl, e);
        }

    }

    private int internalGetCountOfPages(Document documentList, String searchUrl) {
        try {
            return parseCountOfPages(documentList);
        } catch (Exception e) {
            throw new PrcoRuntimeException("error while parsing count of page for products, search URL: " + searchUrl, e);
        }
    }

    /**
     * Metoda vrati pocet stranok(pagging) na kolkych sa dane vyhladavane slovo vyskytuje.
     * <br><b>Pozor:</b> Nie je to celkovy pocet produktov, ale pocet stranok, v zavislosti od strankovanie
     * daneho eshopu...
     * <br>Volana 1 v poradi.
     * <br> vynimky vyhadzovane touto metodu su odchytovane vyssie
     *
     * @param documentList
     * @return
     */
    protected abstract int parseCountOfPages(Document documentList);

    /**
     * Volana 2 v poradi.<br>
     * vynimky vyhadzovane touto metodu su odchytovane vyssie
     *
     * @param documentList aktualne parsovany dokument
     * @param pageNumber   poradove cislo stranky(z pagingu)
     * @return
     */
    protected abstract List<String> parsePageForProductUrls(Document documentList, int pageNumber);

    protected abstract boolean isProductUnavailable(Document documentDetailProduct);

    protected abstract Optional<String> parseProductNameFromDetail(Document documentDetailProduct);

    protected abstract Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct);

    //TODO nasledovne 2 metody spojit do jednej a urobit aj navratovy typ

    protected abstract Optional<ProductAction> parseProductAction(Document documentDetailProduct);

    protected abstract Optional<Date> parseProductActionValidity(Document documentDetailProduct);

    protected abstract Optional<String> parseProductPictureURL(Document documentDetailProduct);


    private void logWarningIfNull(Optional<?> propertyValue, String propertyName, String productUrl) {
        if (!propertyValue.isPresent()) {
            log.warn("property {} is null for product url {}", propertyName, productUrl);
        }
    }
}
