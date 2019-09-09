package sk.hudak.prco.test.ng

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.builder.SearchUrlBuilderImpl
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.eshop.*
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
//    https://www.maxikovy-hracky.cz/vyhledavani?search=pampers
//           println(test.parseUrlsOfProduct(EshopUuid.MALL, "pampers"));
//
    println(test.parseProductNewData("https://www.gigalekaren.sk/produkt/nutrilon-kase-pronutra-ml-kr-s-piskoty-6m-225g/"))

//    println(test.parseProductUpdateData("https://www.farby.sk/156498/pampers-premium-care-plienky-maxi-52-ks/"))
}

class ProductParserTest {

    private fun getParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return when (eshopUuid) {
            // A
            EshopUuid.ALZA -> AlzaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // B
            EshopUuid.BRENDON -> BrendonProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // D
            EshopUuid.DROGERKA -> DrogerkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.DR_MAX -> DrMaxProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // F
            EshopUuid.FOUR_KIDS -> FourKidsProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.FARBY -> FarbyProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // G
            EshopUuid.GIGA_LEKAREN -> GigaLekarenProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // P
            EshopUuid.PILULKA -> PilulkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // L
            EshopUuid.LEKAREN_BELLA -> LekarenBellaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.LEKAREN_EXPRES -> LekarenExpresProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            // M
            EshopUuid.MALL -> MallProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.MAXIKOVY_HRACKY -> MaxikovyHrackyProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            //TODO others
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
