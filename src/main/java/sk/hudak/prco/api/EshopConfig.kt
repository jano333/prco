package sk.hudak.prco.api

import sk.hudak.prco.api.SearchTemplateConstants.KEYWORD_TEMP
import sk.hudak.prco.api.SearchTemplateConstants.PAGE_NUMBER_TEMP
import sk.hudak.prco.exception.PrcoRuntimeException

object AlzaEshopConfiguration : StaticEshopConfiguration(
        EshopCategory.NONE,
        "https://www.alza.sk",
        "https://www.alza.sk/search.htm?exps=" + KEYWORD_TEMP,
        "https://www.alza.sk/search-p" + PAGE_NUMBER_TEMP + ".htm?exps=" + KEYWORD_TEMP,
        5, 12, 24
)

object MallEshopConfiguration : DynamicEshopConfiguration(
        EshopCategory.NONE,
        "https://www.mall.sk",
        3, 12, -1) {

    override fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int): String {
        return when (keyword) {
            "pampers", "nutrilon" -> "https://www.mall.sk/znacka/$keyword?page=$pageNumber"
            else -> "https://www.mall.sk/hladaj?page=$pageNumber&s=$keyword"
        }
    }
}




abstract class EshopConfiguration(
        val category: EshopCategory,
        val productStartUrl: String,
        /**
         * true, if it is dynamic, base on keyword
         */
        val isDynamicSearchUrlByKeyWord: Boolean,
        open val searchTemplateUrl: String? = null,
        val searchTemplateUrlWithPageNumber: String? = null,
        val maxCountOfNewPages: Int,
        val olderThanInHours: Int,
        val maxCountOfProductOnPage: Int,
        val countToWaitInSecond: Int = 3) {

    open fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int = 1): String {
        return ""
    }
}

abstract class StaticEshopConfiguration(category: EshopCategory,
                                        productStartUrl: String,
                                        searchTemplateUrl: String,
                                        searchTemplateUrlWithPageNumber: String,
                                        maxCountOfNewPages: Int,
                                        olderThanInHours: Int,
                                        maxCountOfProductOnPage: Int)
    : EshopConfiguration(
        category,
        productStartUrl,
        false,
        searchTemplateUrl,
        searchTemplateUrlWithPageNumber,
        maxCountOfNewPages,
        olderThanInHours,
        maxCountOfProductOnPage) {

    override fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int): String {
        throw PrcoRuntimeException("can't be called for static search url")
    }
}

abstract class DynamicEshopConfiguration(category: EshopCategory,
                                         productStartUrl: String,
                                         maxCountOfNewPages: Int,
                                         olderThanInHours: Int,
                                         maxCountOfProductOnPage: Int)
    : EshopConfiguration(
        category,
        productStartUrl,
        true,
        null,
        null,
        maxCountOfNewPages,
        olderThanInHours,
        maxCountOfProductOnPage) {

    //TODO po preklopenie vsetkych eshop to odkomentovat
//    override val searchTemplateUrl: String?
//        get() = throw PrcoRuntimeException("cant be called for dynamic")


}
