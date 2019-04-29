package sk.hudak.prco.manager.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.internal.NewProductInfo;
import sk.hudak.prco.dto.newproduct.NewProductCreateDto;
import sk.hudak.prco.manager.AddingNewProductManager;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.parser.EshopProductsParser;
import sk.hudak.prco.parser.EshopUuidParser;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.task.TaskManager;
import sk.hudak.prco.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Slf4j
@Component
public class AddingNewProductManagerImpl implements AddingNewProductManager {

    private static final String ERROR_WHILE_CREATING_NEW_PRODUCT = "error while creating new product";

    @Autowired
    private InternalTxService internalTxService;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private EshopUuidParser eshopUuidParser;

    @Autowired
    private List<EshopProductsParser> productsParsers;

    @Override
    public void addNewProductsByKeywordsForAllEshops(String... searchKeyWords) {
        Arrays.asList(searchKeyWords).forEach(this::addNewProductsByKeywordForAllEshops);
    }

    @Override
    public void addNewProductsByKeywordForAllEshops(String searchKeyWord) {
        notNullNotEmpty(searchKeyWord, "searchKeyWord");

        for (EshopUuid eshopUuid : EshopUuid.values()) {

            // ak neexistuje parser pre dany eshop, tak len zaloguj a chod na dalsi
            if (!existParserFor(eshopUuid)) {
                log.warn("for eshop {} none parser found", eshopUuid);
                continue;
            }
            // spusti stahovanie pre dalsi
            addNewProductsByKeywordForEshop(eshopUuid, searchKeyWord);

            // kazdy dalsi spusti s 3 sekundovym oneskorenim
            ThreadUtils.sleepSafe(3);
        }
    }

    @Override
    public void addNewProductsByKeywordForEshop(EshopUuid eshopUuid, String searchKeyWord) {
        notNull(eshopUuid, "eshopUuid");
        notNullNotEmpty(searchKeyWord, "searchKeyWord");

        log.debug(">> addNewProductsByKeywordForEshop eshop: {}, searchKeyWord: {}", eshopUuid, searchKeyWord);
        taskManager.submitTask(eshopUuid, () -> {

            taskManager.markTaskAsRunning(eshopUuid);
            boolean finishedWithError = false;

            try {
                // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
                List<String> urlList = htmlParser.searchProductUrls(eshopUuid, searchKeyWord);

                //TODO toto volanie dat do osobitneho try catch bloku a zalogovat ze sa nepodarilo ulozit a nie ze sa nepodarilo vyparsovat
                createNewProducts(eshopUuid, urlList);

            } catch (Exception e) {
                logErrorParsingProductUrls(eshopUuid, searchKeyWord);
                log.error(ERROR_WHILE_CREATING_NEW_PRODUCT, e);
                finishedWithError = true;

            } finally {
                taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
            }
        });
        log.debug("<< addNewProductsByKeywordForEshop eshop {}, searchKeyWord {}", eshopUuid, searchKeyWord);
    }

    @Override
    public void addNewProductsByUrl(String... productsUrl) {
        notNullNotEmpty(productsUrl, "productsUrl");

        int countOfUrls = productsUrl.length;
        log.debug(">> addNewProductsByUrl count of URLs: {}", countOfUrls);

        // roztriedim URL podla typu eshopu
        Map<EshopUuid, List<String>> eshopUrls = new EnumMap<>(EshopUuid.class);
        for (String productUrl : productsUrl) {
            eshopUrls.computeIfAbsent(eshopUuidParser.parseEshopUuid(productUrl),
                    eshopUuid -> new ArrayList<>())
                    .add(productUrl);
        }

        for (EshopUuid eshopUuid : eshopUrls.keySet()) {

            taskManager.submitTask(eshopUuid, () -> {

                taskManager.markTaskAsRunning(eshopUuid);
                boolean finishedWithError = false;

                try {
                    createNewProducts(eshopUuid, eshopUrls.get(eshopUuid));

                } catch (Exception e) {
                    log.error(ERROR_WHILE_CREATING_NEW_PRODUCT, e);
                    finishedWithError = true;

                } finally {
                    taskManager.markTaskAsFinished(eshopUuid, finishedWithError);
                }
            });
        }
        log.debug("<< addNewProductsByUrl count of URLs: {}", countOfUrls);
    }

    private void createNewProducts(EshopUuid eshopUuid, List<String> urlList) {
        int allUrlCount = urlList.size();

        for (int currentUrlIndex = 0; currentUrlIndex < allUrlCount; currentUrlIndex++) {
            if (taskManager.isTaskShouldStopped(eshopUuid)) {
                taskManager.markTaskAsStopped(eshopUuid);
                break;
            }
            log.debug("starting {} of {}", currentUrlIndex + 1, allUrlCount);
            String productUrl = urlList.get(currentUrlIndex);

            // ak uz exituje, tak vynechavam
            log.debug("checking existence of product URL {}", productUrl);
            if (internalTxService.existProductWithURL(productUrl)) {
                log.debug("already added -> skipping");
                continue;
            }

            // parsujem
            NewProductInfo newProductInfo = htmlParser.parseProductNewData(productUrl);
            //TODO pridat kontrolu na dostupnost proudku, alza nebol dostupny preto nevrati mene.... a padne toto

            // je len tmp fix
            if (newProductInfo.getName() == null) {
                log.warn("new product not contains name, skipping to next product");
                continue;
            }
            if (newProductInfo.getUnit() == null) {
                logErrorParsingUnit(eshopUuid, productUrl, newProductInfo.getName());
            }

            // preklopim a pridavam do DB
            internalTxService.createNewProduct(mapper.map(newProductInfo, NewProductCreateDto.class));

            // sleep pre dalsou iteraciou
            //TODO fix na zaklade nastavenia daneho eshopu.... dave od to delay
            ThreadUtils.sleepRandomSafe();
        }
    }

    private void logErrorParsingProductUrls(EshopUuid eshopUuid, String searchKeyWord) {
        internalTxService.createError(ErrorCreateDto.builder()
                .errorType(ErrorType.PARSING_PRODUCT_URLS)
                .eshopUuid(eshopUuid)
                .additionalInfo(searchKeyWord)
                .build());
    }

    private void logErrorParsingUnit(EshopUuid eshopUuid, String productUrl, String productName) {
        internalTxService.createError(ErrorCreateDto.builder()
                .errorType(ErrorType.PARSING_PRODUCT_INFO_ERR)
                .eshopUuid(eshopUuid)
                .url(productUrl)
                .additionalInfo(productName)
                .build());

    }

    private boolean existParserFor(EshopUuid eshopUuid) {
        //FIXME cez lamba a findFirst
        for (EshopProductsParser productsParser : productsParsers) {
            if (eshopUuid.equals(productsParser.getEshopUuid())) {
                return true;
            }
        }
        return false;
    }

}
