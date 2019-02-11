package sk.hudak.prco.test;

import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.builder.impl.SearchUrlBuilderImpl;
import sk.hudak.prco.eshop.DrMaxProductParser;
import sk.hudak.prco.parser.impl.UnitParserImpl;
import sk.hudak.prco.ssl.PrcoSslManager;
import sk.hudak.prco.utils.UserAgentDataHolder;

public class DrMaxProductParserTest {

    public static void main(String[] args) {
        PrcoSslManager.getInstance().init();

        UserAgentDataHolder userAgentDataHolder = new UserAgentDataHolder();
        userAgentDataHolder.init();

        UnitParserImpl unitParser = new UnitParserImpl();

        SearchUrlBuilder searchUrlBuilder = new SearchUrlBuilderImpl();

        // Dx max
        System.out.println(new DrMaxProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers/ 5"));
                .parseNewProductInfo("https://www.drmax.sk/pampers-plienky-active-baby-dry-mega-quad-s5/"));
//                .parseNewProductInfo("https://www.drmax.sk/pampers-baby-wipes-fresh-clean-64ks/"));
//                .parseNewProductInfo("https://www.drmax.sk/pampers-mega-maxi-132/"));
//                .parseProductUpdateData("https://www.drmax.sk/pampers-mega-maxi-132/"));
    }
}
