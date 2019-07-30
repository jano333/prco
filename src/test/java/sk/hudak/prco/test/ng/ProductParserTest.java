package sk.hudak.prco.test.ng;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.builder.SearchUrlBuilderImpl;
import sk.hudak.prco.dto.internal.ProductNewData;
import sk.hudak.prco.eshop.AlzaProductParser;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.EshopProductsParser;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.UnitParserImpl;
import sk.hudak.prco.ssl.PrcoSslManager;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.util.List;

public class ProductParserTest {

    private final UserAgentDataHolder userAgentDataHolder;
    private final UnitParser unitParser;
    private final SearchUrlBuilder searchUrlBuilder;

    public ProductParserTest() {
        PrcoSslManager.INSTANCE.init();
        userAgentDataHolder = new UserAgentDataHolder();
        userAgentDataHolder.init();
        unitParser = new UnitParserImpl();
        searchUrlBuilder = new SearchUrlBuilderImpl();
    }

    public static void main(String[] args) {
        ProductParserTest test = new ProductParserTest();

        System.out.println(test.parseUrlsOfProduct(EshopUuid.MALL, "pampers"));

        System.out.println(test.parseProductNewData("https://www.pilulka24.sk/pampers-activebaby-giant-pack-3-midi-90"));
    }

    private List<String> parseUrlsOfProduct(EshopUuid eshopUuid, String keyword) {
        return getParserForEshop(eshopUuid).parseUrlsOfProduct(keyword);
    }

    private ProductNewData parseProductNewData(String productUrl) {
//        eshopUuidParser.parseEshopUuid(productUrl)
        return null;
    }

    private EshopProductsParser getParserForEshop(EshopUuid eshopUuid) {
        switch (eshopUuid) {
            case ALZA:
                return new AlzaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder);


            default:
                throw new PrcoRuntimeException("Pridaj implementaciu");
        }
    }

}
