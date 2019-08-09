package sk.hudak.prco.test.ng

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.builder.SearchUrlBuilderImpl
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.eshop.AlzaProductParser
import sk.hudak.prco.eshop.MallProductParser
import sk.hudak.prco.eshop.pharmacy.LekarenBellaProductParser
import sk.hudak.prco.eshop.pharmacy.LekarenExpresProductParser
import sk.hudak.prco.eshop.pharmacy.PilulkaProductParser
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.EshopProductsParser
import sk.hudak.prco.parser.EshopUuidParser
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.parser.impl.EnumImplEshopUuidParserImpl
import sk.hudak.prco.parser.impl.UnitParserImpl
import sk.hudak.prco.ssl.PrcoSslManager
import sk.hudak.prco.utils.UserAgentDataHolder

fun main() {

    val test = ProductParserTest()

    //       println(test.parseUrlsOfProduct(EshopUuid.MALL, "pampers"));
//
//    println(test.parseProductNewData("https://www.alza.sk/maxi/pampers-premium-care-vel-4-maxi-168-ks-mesacne-balenie-d4842712.htm"))

    println(test.parseProductUpdateData(             "https://www.lekarenexpres.sk/kozmetika-hygiena-domacnost/hygienicke-prostriedky-a-prostriedky-pre-domacnos/plienky-a-plenove-nohavicky-pre-deti/pampers-active-baby-vpp-4-maxi-plus-53ks-17305.html"))
}

class ProductParserTest {

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

    private fun getParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return when (eshopUuid) {
            EshopUuid.ALZA -> AlzaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.PILULKA -> PilulkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.LEKAREN_BELLA -> LekarenBellaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.LEKAREN_EXPRES -> LekarenExpresProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            EshopUuid.MALL -> MallProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
            //TODO others
            else -> throw PrcoRuntimeException("Pridaj implementaciu do testu pre eshop $eshopUuid")
        }
    }

    public fun parseUrlsOfProduct(eshopUuid: EshopUuid, keyword: String): List<String> {
        return getParserForEshop(eshopUuid).parseUrlsOfProduct(keyword)
    }

    public fun parseProductNewData(productUrl: String): ProductNewData {
        return getParserForEshop(eshopUuidParser.parseEshopUuid(productUrl)).parseProductNewData(productUrl)
    }

    public fun parseProductUpdateData(productUrl: String): ProductUpdateData {
        return getParserForEshop(eshopUuidParser.parseEshopUuid(productUrl)).parseProductUpdateData(productUrl)
    }


}
