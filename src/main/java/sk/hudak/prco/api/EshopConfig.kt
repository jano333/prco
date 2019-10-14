package sk.hudak.prco.api

import sk.hudak.prco.api.SearchKeyWordId.LOVELA_ID
import sk.hudak.prco.api.SearchKeyWordId.NUTRILON_ID
import sk.hudak.prco.api.SearchKeyWordId.PAMPERS_ID
import sk.hudak.prco.api.SearchTemplateConstants.KEYWORD_TEMP
import sk.hudak.prco.api.SearchTemplateConstants.OFFSET_TEMP
import sk.hudak.prco.api.SearchTemplateConstants.PAGE_NUMBER_TEMP
import sk.hudak.prco.exception.PrcoRuntimeException

object SearchKeyWordId {
    const val PAMPERS_ID = 1L
    const val NUTRILON_ID = 2L
    const val LOVELA_ID = 3L
}

/****************************/
/*            A             */
/****************************/
object AlzaEshopConfiguration : StaticEshopConfiguration(EshopCategory.NONE,
        "https://www.alza.sk",
        "https://www.alza.sk/search.htm?exps=$KEYWORD_TEMP",
        "https://www.alza.sk/search-p$PAGE_NUMBER_TEMP.htm?exps=$KEYWORD_TEMP",
        5, 12, 24,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, LOVELA_ID)
)

object AmdDrogeriaEshopConfiguration : StaticEshopConfiguration(EshopCategory.DRUGSTORE,
        "https://www.amddrogeria.sk",
        "https://www.amddrogeria.sk/catalog/search/?q=$KEYWORD_TEMP",
        "https://www.amddrogeria.sk/catalog/search/?q=$KEYWORD_TEMP&offset=$OFFSET_TEMP",
        5, 12, 24,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID, LOVELA_ID)
)
//TODO ANDREA_SHOP -> neviem pagging ako...

/****************************/
/*            B             */
/****************************/
object BrendonEshopConfiguration : DynamicEshopConfiguration(EshopCategory.NONE,
        "https://www.brendon.sk",
        5, 12, 30,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)) {

    override fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int): String =
            when (keyword) {
                "pampers",
                "nutrilon" -> "https://www.brendon.sk/$keyword#/pageSize=30&orderBy=0&pageNumber=$pageNumber"
                else -> "https://www.brendon.sk/search?q=$keyword&pagenumber=$pageNumber"
            }
}

/****************************/
/*            D             */
/****************************/
object DrogerkaConfiguration : StaticEshopConfiguration(EshopCategory.DRUGSTORE,
        "https://www.drogerka.sk",
        "https://www.drogerka.sk/index.php?route=product/search&search=$KEYWORD_TEMP",
        "https://www.drogerka.sk/vyhladavanie?search=$KEYWORD_TEMP&page=$PAGE_NUMBER_TEMP",
        5, 12, 48,
        supportedSearchKeywordIds = listOf(PAMPERS_ID)
)

object DrMaxConfiguration : StaticEshopConfiguration(EshopCategory.PHARMACY,
        "https://www.drmax.sk",
        "https://www.drmax.sk/catalog/search/?q=$KEYWORD_TEMP",
        "https://www.drmax.sk/catalog/search/?q={keyword}&offset={offset}&limit={limit}",
        4, 12, 24,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)
)

object DrogeriaVmdConfiguration : StaticEshopConfiguration(EshopCategory.DRUGSTORE,
        "https://www.drogeria-vmd.sk",
        "https://www.drogeria-vmd.sk/hladanie/?q=" + KEYWORD_TEMP,
        "https://www.drogeria-vmd.sk/hladanie-stranka-{pageNumber}/?q=" + KEYWORD_TEMP,
        4, 12, 12,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID, LOVELA_ID)
)


/****************************/
/*            E             */
/****************************/
object EsoDrogeriaConfiguration : StaticEshopConfiguration(EshopCategory.DRUGSTORE,
        "https://www.esodrogeria.eu",
        "https://www.esodrogeria.eu/search-engine.htm?slovo=$KEYWORD_TEMP&search_submit=&hledatjak=2",
        //pozor pagenumber 2 je vlastne az tretia stranka
        "https://www.esodrogeria.eu/search-engine.htm?slovo=$KEYWORD_TEMP&search_submit=&hledatjak=2&page=$PAGE_NUMBER_TEMP&man=9",
        5, 12, 21,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, LOVELA_ID)
)

/****************************/
/*            F             */
/****************************/
object FarbyConfiguration : StaticEshopConfiguration(EshopCategory.NONE,
        "https://www.farby.sk",
        "https://www.farby.sk/vyhladavanie?hladaj=$KEYWORD_TEMP",
        "",
        1, 12, -1,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, LOVELA_ID)
)

object FeedoEshopConfiguration : StaticEshopConfiguration(EshopCategory.NONE,
        "https://www.feedo.sk",
        "https://www.feedo.sk/vysledky-hladania/$KEYWORD_TEMP/",
        "https://www.feedo.sk/vysledky-hladania/$KEYWORD_TEMP/?page=$PAGE_NUMBER_TEMP",
        5, 12, 20,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID, LOVELA_ID)
)

/****************************/
/*            K             */
/****************************/
object KidMarketEshopConfiguration : StaticEshopConfiguration(EshopCategory.NONE,
        "https://kidmarket.sk/",
        "https://kidmarket.sk/vyhladavanie?controller=search&orderby=position&orderway=desc&search_query=$KEYWORD_TEMP&submit_search=",
        "https://kidmarket.sk/vyhladavanie?controller=search&orderby=position&orderway=desc&search_query=$KEYWORD_TEMP&submit_search=&p=$PAGE_NUMBER_TEMP",
        5, 12, 32,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID, LOVELA_ID)
)

/****************************/
/*            L             */
/****************************/
object LekarenBellaEshopConfiguration : StaticEshopConfiguration(EshopCategory.PHARMACY,
        "https://www.lekaren-bella.sk",
        "https://www.lekaren-bella.sk/search/?query=$KEYWORD_TEMP",
        "https://www.lekaren-bella.sk/search:$PAGE_NUMBER_TEMP:12/$KEYWORD_TEMP",
        5, 12, 12,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)
)

object LekareVKockeEshopConfiguration : StaticEshopConfiguration(EshopCategory.PHARMACY,
        "https://www.lekarenvkocke.sk",
        "https://www.lekarenvkocke.sk/vyhladavanie?q=$KEYWORD_TEMP",
        "https://www.lekarenvkocke.sk/vyhladavanie:$PAGE_NUMBER_TEMP:16:00/$KEYWORD_TEMP",
        5, 12, 16,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)
)

object LekareExpresEshopConfiguration : StaticEshopConfiguration(EshopCategory.PHARMACY,
        "https://www.lekarenexpres.sk",
        "https://www.lekarenexpres.sk/search/?hledat=$KEYWORD_TEMP",
        "https://www.lekarenexpres.sk/search/?hledat=$KEYWORD_TEMP&start=$OFFSET_TEMP",
        5, 12, 24,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)
)


/****************************/
/*            M             */
/****************************/
object MallEshopConfiguration : DynamicEshopConfiguration(EshopCategory.NONE,
        "https://www.mall.sk",
        3, 12, -1,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID, LOVELA_ID)) {

    override fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int): String =
            when (keyword) {
                "pampers",
                "nutrilon" -> "https://www.mall.sk/znacka/$keyword?page=$pageNumber"
                else -> "https://www.mall.sk/hladaj?page=$pageNumber&s=$keyword"
            }
}


/****************************/
/*            P             */
/****************************/
object PerinbabaEshopConfiguration : StaticEshopConfiguration(EshopCategory.NONE,
        "http://www.perinbaba.sk/",
        "http://www.perinbaba.sk/catalogsearch/result/?limit=48&q=$KEYWORD_TEMP",
        "http://www.perinbaba.sk/catalogsearch/result/index/?limit=48&p={pageNumber}&q=$KEYWORD_TEMP",
        5, 12, -1,
        supportedSearchKeywordIds = listOf(PAMPERS_ID)
)

object PilulkaEshopConfiguration : DynamicEshopConfiguration(EshopCategory.PHARMACY,
        "https://www.pilulka.sk",
        5, 12, 40,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)) {

    override fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int): String {
        return when (keyword) {
            "pampers" -> "https://www.pilulka.sk/detske-plienky/pampers?page=$pageNumber"
            "nutrilon" -> "https://www.pilulka.sk/nutrilon?page=$pageNumber"
            else -> "https://www.pilulka.sk/vyhladavanie?page=$pageNumber&q=$keyword"
        }
    }
}

object Pilulka24EshopConfiguration : DynamicEshopConfiguration(EshopCategory.PHARMACY,
        "https://www.pilulka24.sk/",
        5, 12, 40,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)) {

    override fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int): String {
        return when (keyword) {
            "pampers" -> "https://www.pilulka24.sk/detske-plienky/pampers?page=$pageNumber"
            "nutrilon" -> "https://www.pilulka24.sk/nutrilon?page=$pageNumber"
            else -> "https://www.pilulka24.sk/vyhladavanie?page=$pageNumber&q=$keyword"
        }
    }
}

object PrvaLekarenEshopConfiguration : StaticEshopConfiguration(EshopCategory.PHARMACY,
        "https://www.prva-lekaren.sk",
        "https://www.prva-lekaren.sk/search?q=$KEYWORD_TEMP",
        "https://www.prva-lekaren.sk/search?lm=12&st=$PAGE_NUMBER_TEMP&q=$KEYWORD_TEMP",
        5, 12, 12,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID)
)

/****************************/
/*            T             */
/****************************/
object TescoEshopConfiguration : StaticEshopConfiguration(EshopCategory.NONE,
        "https://potravinydomov.itesco.sk",
        "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query=$KEYWORD_TEMP",
        "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query=$KEYWORD_TEMP&page=$PAGE_NUMBER_TEMP",
        5, 12, 24,
        supportedSearchKeywordIds = listOf(PAMPERS_ID, NUTRILON_ID, LOVELA_ID)
)

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
        val countToWaitInSecond: Int = 3,
        val supportedSearchKeywordIds: List<Long>) {

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
                                        maxCountOfProductOnPage: Int,
                                        supportedSearchKeywordIds: List<Long>)
    : EshopConfiguration(
        category,
        productStartUrl,
        false,
        searchTemplateUrl,
        searchTemplateUrlWithPageNumber,
        maxCountOfNewPages,
        olderThanInHours,
        maxCountOfProductOnPage,
        supportedSearchKeywordIds = supportedSearchKeywordIds) {

    override fun buildSearchUrlForKeyWord(keyword: String, pageNumber: Int): String {
        throw PrcoRuntimeException("can't be called for static search url")
    }
}

abstract class DynamicEshopConfiguration(category: EshopCategory,
                                         productStartUrl: String,
                                         maxCountOfNewPages: Int,
                                         olderThanInHours: Int,
                                         maxCountOfProductOnPage: Int,
                                         supportedSearchKeywordIds: List<Long>)
    : EshopConfiguration(
        category = category,
        productStartUrl = productStartUrl,
        isDynamicSearchUrlByKeyWord = true,
        searchTemplateUrl = null,
        searchTemplateUrlWithPageNumber = null,
        maxCountOfNewPages = maxCountOfNewPages,
        olderThanInHours = olderThanInHours,
        maxCountOfProductOnPage = maxCountOfProductOnPage,
        supportedSearchKeywordIds = supportedSearchKeywordIds) {

    //TODO po preklopenie vsetkych eshop to odkomentovat
//    override val searchTemplateUrl: String?
//        get() = throw PrcoRuntimeException("cant be called for dynamic")


}

