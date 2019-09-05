package sk.hudak.prco.builder

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.SearchTemplateConstants
import sk.hudak.prco.exception.PrcoRuntimeException
import java.net.URI
import java.net.URL

interface SearchUrlBuilder {

    //TODo spojit do jenej metody posledny bude volitelny paramer
    fun buildSearchUrl(eshopUuid: EshopUuid, searchKeyword: String): String

    fun buildSearchUrl(eshopUuid: EshopUuid, searchKeyWord: String, currentPageNumber: Int): String
}

@Component
class SearchUrlBuilderImpl : SearchUrlBuilder {

    /**
     * to iste ako URLUtils#buildSearchUrl
     *
     * @param eshopUuid
     * @param searchKeyword
     * @return
     */
    override fun buildSearchUrl(eshopUuid: EshopUuid, searchKeyword: String): String {
        val searchUrl: String
        if (eshopUuid.config != null && eshopUuid.config.isDynamicSearchUrlByKeyWord) {
            searchUrl = eshopUuid.config.buildSearchUrlForKeyWord(searchKeyword, 1)
        } else {
            searchUrl = eshopUuid.searchTemplateUrl.replace(SearchTemplateConstants.KEYWORD_TEMP, searchKeyword)
        }

        // safe converzation URL space to %20 see: https://stackoverflow.com/questions/724043/http-url-address-encoding-in-java
        try {
            var url = URL(searchUrl)
            val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
            url = uri.toURL()
            return url.toString()

        } catch (e: Exception) {
            throw PrcoRuntimeException("error while converting search url", e)
        }

    }

    override fun buildSearchUrl(eshopUuid: EshopUuid, searchKeyWord: String, currentPageNumber: Int): String {
        val searchUrl: String
        if (eshopUuid.config != null && eshopUuid.config.isDynamicSearchUrlByKeyWord) {
            searchUrl = eshopUuid.config.buildSearchUrlForKeyWord(searchKeyWord, currentPageNumber)

        } else {
            if (eshopUuid.searchTemplateUrlWithPageNumber.contains(SearchTemplateConstants.OFFSET_TEMP)) {
                searchUrl = buildSearchUrl(eshopUuid,
                        searchKeyWord,
                        (currentPageNumber - 1) * eshopUuid.maxCountOfProductOnPage,
                        eshopUuid.maxCountOfProductOnPage)
            } else {
                searchUrl = buildSearchUrl2(eshopUuid, searchKeyWord, currentPageNumber)
            }
        }

        return searchUrl
    }


    private fun buildSearchUrl2(eshopUuid: EshopUuid, searchKeyword: String, pageNumber: Int): String {
        // FIXME duplicita
        val searchUrl = eshopUuid.searchTemplateUrlWithPageNumber
                .replace(SearchTemplateConstants.KEYWORD_TEMP, searchKeyword)
                .replace(SearchTemplateConstants.PAGE_NUMBER_TEMP, pageNumber.toString())

        try {
            var url = URL(searchUrl)
            val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
            url = uri.toURL()
            return url.toString()

        } catch (e: Exception) {
            throw PrcoRuntimeException("error while converting search url", e)
        }

    }

    private fun buildSearchUrl(eshopUuid: EshopUuid, searchKeyword: String, offset: Int, limit: Int): String {
        // FIXME duplicita
        val searchUrl = eshopUuid.searchTemplateUrlWithPageNumber
                .replace(SearchTemplateConstants.KEYWORD_TEMP, searchKeyword)
                .replace(SearchTemplateConstants.OFFSET_TEMP, offset.toString())
                .replace(SearchTemplateConstants.LIMIT_TEMP, limit.toString())

        try {
            var url = URL(searchUrl)
            val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
            url = uri.toURL()
            return url.toString()

        } catch (e: Exception) {
            throw PrcoRuntimeException("error while converting search url", e)
        }

    }
}