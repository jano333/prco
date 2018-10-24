package sk.hudak.prco.builder.impl;

import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.exception.PrcoRuntimeException;

import java.net.URI;
import java.net.URL;

import static sk.hudak.prco.api.SearchTemplateConstants.KEYWORD_TEMP;
import static sk.hudak.prco.api.SearchTemplateConstants.LIMIT_TEMP;
import static sk.hudak.prco.api.SearchTemplateConstants.OFFSET_TEMP;
import static sk.hudak.prco.api.SearchTemplateConstants.PAGE_NUMBER_TEMP;

@Component
public class SearchUrlBuilderImpl implements SearchUrlBuilder {

    /**
     * to iste ako URLUtils#buildSearchUrl
     *
     * @param eshopUuid
     * @param searchKeyword
     * @return
     */
    @Override
    public String buildSearchUrl(EshopUuid eshopUuid, String searchKeyword) {
        String searchUrl = eshopUuid.getSearchTemplateUrl()
                .replace(KEYWORD_TEMP, searchKeyword);

        // safe converzation URL space to %20 see: https://stackoverflow.com/questions/724043/http-url-address-encoding-in-java
        try {
            URL url = new URL(searchUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            return url.toString();

        } catch (Exception e) {
            throw new PrcoRuntimeException("error while converting search url", e);
        }
    }

    @Override
    public String buildSearchUrl(EshopUuid eshopUuid, String searchKeyWord, int currentPageNumber) {
        String searchTemplateUrlWithPageNumber = eshopUuid.getSearchTemplateUrlWithPageNumber();
        String searchUrl;
        if (searchTemplateUrlWithPageNumber.contains(OFFSET_TEMP)) {
            searchUrl = buildSearchUrl(eshopUuid,
                    searchKeyWord,
                    (currentPageNumber - 1) * eshopUuid.getMaxCountOfProductOnPage(),
                    eshopUuid.getMaxCountOfProductOnPage());
        } else {
            searchUrl = buildSearchUrl2(eshopUuid, searchKeyWord, currentPageNumber);
        }

        return searchUrl;
    }


    private String buildSearchUrl2(EshopUuid eshopUuid, String searchKeyword, int pageNumber) {
        // FIXME duplicita
        String searchUrl = eshopUuid.getSearchTemplateUrlWithPageNumber()
                .replace(KEYWORD_TEMP, searchKeyword)
                .replace(PAGE_NUMBER_TEMP, String.valueOf(pageNumber));

        try {
            URL url = new URL(searchUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            return url.toString();

        } catch (Exception e) {
            throw new PrcoRuntimeException("error while converting search url", e);
        }
    }

    private String buildSearchUrl(EshopUuid eshopUuid, String searchKeyword, int offset, int limit) {
        // FIXME duplicita
        String searchUrl = eshopUuid.getSearchTemplateUrlWithPageNumber()
                .replace(KEYWORD_TEMP, searchKeyword)
                .replace(OFFSET_TEMP, String.valueOf(offset))
                .replace(LIMIT_TEMP, String.valueOf(limit));

        try {
            URL url = new URL(searchUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            return url.toString();

        } catch (Exception e) {
            throw new PrcoRuntimeException("error while converting search url", e);
        }
    }
}
