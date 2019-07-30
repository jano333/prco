package sk.hudak.prco.test.ng;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.builder.SearchUrlBuilderImpl;
import sk.hudak.prco.dto.internal.ProductNewData;
import sk.hudak.prco.dto.internal.ProductUpdateData;
import sk.hudak.prco.eshop.AlzaProductParser;
import sk.hudak.prco.eshop.PilulkaProductParser;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.EshopProductsParser;
import sk.hudak.prco.parser.EshopUuidParser;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.EnumImplEshopUuidParserImpl;
import sk.hudak.prco.parser.impl.UnitParserImpl;
import sk.hudak.prco.ssl.PrcoSslManager;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.util.List;

public class ProductParserTest {

    private final UserAgentDataHolder userAgentDataHolder;
    private final UnitParser unitParser;
    private final SearchUrlBuilder searchUrlBuilder;
    private final EshopUuidParser eshopUuidParser;

    public ProductParserTest() {
        PrcoSslManager.INSTANCE.init();
        userAgentDataHolder = new UserAgentDataHolder();
        userAgentDataHolder.init();
        unitParser = new UnitParserImpl();
        searchUrlBuilder = new SearchUrlBuilderImpl();
        eshopUuidParser = new EnumImplEshopUuidParserImpl();
    }

    public static void main(String[] args) {
        ProductParserTest test = new ProductParserTest();

//        System.out.println(test.parseUrlsOfProduct(EshopUuid.MALL, "pampers"));
//        System.out.println(test.parseProductNewData("https://www.pilulka24.sk/pampers-activebaby-giant-pack-3-midi-90"));
        System.out.println(test.parseProductUpdateData("https://www.pilulka.sk/pampers-active-baby-mqp-5-junior-124ks-utierky-zadarmo"));
    }

    private EshopProductsParser getParserForEshop(EshopUuid eshopUuid) {
        switch (eshopUuid) {
            case ALZA:
                return new AlzaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder);
            case PILULKA:
                return new PilulkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder);


            default:
                throw new PrcoRuntimeException("Pridaj implementaciu do testu pre eshop " + eshopUuid);
        }
    }

    private List<String> parseUrlsOfProduct(EshopUuid eshopUuid, String keyword) {
        return getParserForEshop(eshopUuid).parseUrlsOfProduct(keyword);
    }

    private ProductNewData parseProductNewData(String productUrl) {
        return getParserForEshop(eshopUuidParser.parseEshopUuid(productUrl)).parseProductNewData(productUrl);
    }

    private ProductUpdateData parseProductUpdateData(String productUrl) {
        return getParserForEshop(eshopUuidParser.parseEshopUuid(productUrl)).parseProductUpdateData(productUrl);
    }

}
