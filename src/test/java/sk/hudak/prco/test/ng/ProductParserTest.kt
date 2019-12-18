package sk.hudak.prco.test.ng

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.builder.SearchUrlBuilderImpl
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.eshop.*
import sk.hudak.prco.eshop.drugstore.DrogeriaVmdProductParser
import sk.hudak.prco.eshop.drugstore.DrogerkaProductParser
import sk.hudak.prco.eshop.pharmacy.*
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.eshopuid.EnumImplEshopUuidParserImpl
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.parser.unit.UnitParserImpl
import sk.hudak.prco.ssl.PrcoSslManager
import sk.hudak.prco.utils.UserAgentDataHolder

fun main() {

    val test = ProductParserTest()

//    val urlList = test.parseUrlsOfProduct(EshopUuid.FEEDO, "pampers")
//    println("count in list ${urlList.size}")
//    println("count in set ${HashSet(urlList).size}")
//    println(urlList)

    println(test.parseProductNewData("https://www.brendon.sk/pampers-premium-care-smallpack-s2-23-pcs-jednorazove-plienky-12386002"))

//    println(test.parseProductUpdateData("https://www.mall.sk/detske-mlieka/nutrilon-pronutra-3-6x800g-kasa-4x225g"))
}

class ProductParserTest {

    private fun getParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return when (eshopUuid) {
            // A
            EshopUuid.ALZA -> AlzaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // B
            EshopUuid.BRENDON -> BrendonProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // D
            EshopUuid.DR_MAX -> DrMaxProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.DROGERIA_VMD -> DrogeriaVmdProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.DROGERKA -> DrogerkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // F
            EshopUuid.FARBY -> FarbyProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.FEEDO -> FeedoProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.FOUR_KIDS -> FourKidsProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // G
            EshopUuid.GIGA_LEKAREN -> GigaLekarenProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // K
            EshopUuid.KID_MARKET -> KidMarketProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // L
            EshopUuid.LEKAREN_BELLA -> LekarenBellaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.LEKAREN_EXPRES -> LekarenExpresProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.LEKAREN_V_KOCKE -> LekarenVKockeProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // M
            EshopUuid.MALL -> MallProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//            EshopUuid.MAXIKOVY_HRACKY -> MaxikovyHrackyProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.METRO -> MetroProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.MOJA_LEKAREN -> MojaLekarenProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // P
            EshopUuid.PILULKA -> PilulkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.PILULKA_24 -> Pilulka24ProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.PERINBABA -> PerinbabaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
           // T
            EshopUuid.TESCO -> TescoProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)

            else -> throw PrcoRuntimeException("Pridaj implementaciu do testu pre eshop $eshopUuid")
        }
    }


    private val userAgentDataHolder: UserAgentDataHolder
    private val unitParser: UnitParser
    private val searchUrlBuilder: SearchUrlBuilder

    private val eshopUuidParser: EshopUuidParser

    init {
        PrcoSslManager.init()
        userAgentDataHolder = UserAgentDataHolder()
        userAgentDataHolder.init()
        unitParser = UnitParserImpl()
        searchUrlBuilder = SearchUrlBuilderImpl()
        eshopUuidParser = EnumImplEshopUuidParserImpl()
    }

    fun parseUrlsOfProduct(eshopUuid: EshopUuid, keyword: String): List<String> {
        return getParserForEshop(eshopUuid).parseUrlsOfProduct(keyword)
    }

    fun parseProductNewData(productUrl: String): ProductNewData {
        return getParserForEshop(eshopUuidParser.parseEshopUuid(productUrl)).parseProductNewData(productUrl)
    }

    fun parseProductUpdateData(productUrl: String): ProductUpdateData {
        return getParserForEshop(eshopUuidParser.parseEshopUuid(productUrl)).parseProductUpdateData(productUrl)
    }


}
