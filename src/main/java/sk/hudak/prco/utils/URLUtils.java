package sk.hudak.prco.utils;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.exception.PrcoRuntimeException;

import java.net.URI;
import java.net.URL;

public class URLUtils {

    private URLUtils() {
    }

    /**
     * vyskladam url pre dany eshop aj s klucovym slovom
     *
     * @param eshopUuid
     * @param searchKeyword
     * @return
     */
    public static String buildSearchUrl(EshopUuid eshopUuid, String searchKeyword) {
        String searchUrl = eshopUuid.getSearchTemplateUrl()
                .replace("{keyword}", searchKeyword);

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

    public static String buildSearchUrl(EshopUuid eshopUuid, String searchKeyword, int pageNumber) {
        // FIXME duplicita
        String searchUrl = eshopUuid.getSearchTemplateUrlWithPageNumber()
                .replace("{keyword}", searchKeyword)
                .replace("{pageNumber}", String.valueOf(pageNumber));

        try {
            URL url = new URL(searchUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            return url.toString();

        } catch (Exception e) {
            throw new PrcoRuntimeException("error while converting search url", e);
        }
    }

    public static String buildSearchUrl(EshopUuid eshopUuid, String searchKeyword, int offset, int limit) {
        // FIXME duplicita
        String searchUrl = eshopUuid.getSearchTemplateUrlWithPageNumber()
                .replace("{keyword}", searchKeyword)
                .replace("{offset}", String.valueOf(offset))
                .replace("{limit}", String.valueOf(limit));

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
