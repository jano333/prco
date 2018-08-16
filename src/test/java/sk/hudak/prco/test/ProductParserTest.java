package sk.hudak.prco.test;

import sk.hudak.prco.eshop.DrMaxProductParser;
import sk.hudak.prco.parser.impl.UnitParserImpl;
import sk.hudak.prco.ssl.PrcoSslManager;
import sk.hudak.prco.utils.UserAgentDataHolder;

public class ProductParserTest {

    public static void main(String[] args) {

        PrcoSslManager.getInstance().init();

        UserAgentDataHolder userAgentDataHolder = new UserAgentDataHolder();
        userAgentDataHolder.init();

        UnitParserImpl unitParser = new UnitParserImpl();

        // Dx max
//        System.out.println(new DrMaxProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
        System.out.println(new DrMaxProductParser(unitParser, userAgentDataHolder)
//                .parseNewProductInfo("https://www.drmax.sk/pampers-mega-maxi-132/"));
                .parseProductUpdateData("https://www.drmax.sk/pampers-mega-maxi-132/"));


        //Alza
//        System.out.println(new AlzaProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                System.out.println(new AlzaProductParser(unitParser, userAgentDataHolder)
//                .parseNewProductInfo("https://www.alza.sk/maxi/pampers-active-baby-dry-vel-4-maxi-174-ks-d4593569.htm"));


        //HORNBACH
//        System.out.println(new HornbachProductParser(new UnitParserImpl(), userAgentDataHolder)
////                .parseProductUpdateData("https://www.hornbach.sk/shop/Bosch-GBH-2-28-F-s-funkciou-Kick-Back-Control-vr-dlata-a-vrtaka/6348699/artikel.html"));
//                .parseProductUpdateData("https://www.hornbach.sk/shop/Zahradny-domcek-Duramax-Colossus-plechovy/6147837/artikel.html"));


        //OBI
//        System.out.println(new MallProductParser(new UnitParserImpl())
//                .parseUrlsOfProduct("pampers 4"));
//        System.out.println(new ObiProductParser(new UnitParserImpl(), userAgentDataHolder)
//                .parseNewProductInfo("https://www.obi.sk/zahradne-hadice/cmi-zahradna-hadica-12-5-mm-1-2-20-m-zelena/p/2235422"));
//        System.out.println(new MallProductParser(new UnitParserImpl())
//                .parseProductUpdateData("https://www.mall.sk/detske-mlieka/nutrilon-4-6-x-800g"));

        //Tesco
//        ProductForUpdateData productForUpdateData = new TescoProductParser(new UnitParserImpl())
//                .parseProductUpdateData("https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002020138951");
//        System.out.println(productForUpdateData); //Tesco


        //MALL
//        System.out.println(new MallProductParser(new UnitParserImpl())
//                .parseUrlsOfProduct("pampers 4"));
//        System.out.println(new MallProductParser(new UnitParserImpl())
//                .parseNewProductInfo("https://www.mall.sk/detske-mlieka/nutrilon-4-6-x-800g"));
//        System.out.println(new MallProductParser(new UnitParserImpl(), userAgentDataHolder)
//                .parseProductUpdateData("http://mall.sk/plienky-pampers-7-18-kg/pampers-active-baby-4-maxi-7-14kg-giant-box-90ks"));

        //Bambino
//        System.out.println(new BambinoProductParser(new UnitParserImpl())
//                .parseUrlsOfProduct("pampers 4"));
//        System.out.println(new BambinoProductParser(new UnitParserImpl())
//                .parseNewProductInfo("https://www.bambino.sk/jednorazove-plienky/plienkove-nohavicky-premium-pants-4-maxi-44-ks-1"));
//        System.out.println(ToStringBuilder.reflectionToString(
//                new BambinoProductParser(new UnitParserImpl(), userAgentDataHolder)
//                        .parseProductUpdateData("https://www.bambino.sk/jednorazove-plienky/abd-mega-box-plus-4-174ks-1"),
//                ToStringStyle.MULTI_LINE_STYLE));

        //Pilulka
//        System.out.println(new PilulkaProductParser(new UnitParserImpl())
//                .parseUrlsOfProduct("nutrilon"));
//        System.out.println(new PilulkaProductParser(new UnitParserImpl())
//                .parseNewProductInfo("https://www.pilulka.sk/nutrilon-3-ha-800g-5-1-zdarma"));
//        System.out.println(new PilulkaProductParser(new UnitParserImpl())
//                .parseProductUpdateData("https://www.pilulka.sk/nutrilon-3-ha-800g-5-1-zdarma"));

        //Metro
//        System.out.println(new MetroProductParser(new UnitParserImpl())
//                .parseUrlsOfProduct("pampers 4"));
//        System.out.println(new MetroProductParser(new UnitParserImpl())
//                .parseNewProductInfo("https://sortiment.metro.sk/sk/pampers-abd-mb-s4p-152ks/241760p/"));
//        System.out.println(new MetroProductParser(new UnitParserImpl())
//                .parseProductUpdateData("https://sortiment.metro.sk/sk/pampers-abd-mb-s4p-152ks/241760p/"));
//                .parseProductUpdateData("https://sortiment.metro.sk/sk/guacamole-mix-vanicka-380g/259947p/"));
        //Feedo
//        System.out.println(new MetroProductParser(new UnitParserImpl())
//                .parseUrlsOfProduct("pampers 4"));
//        System.out.println(new MetroProductParser(new UnitParserImpl())
//                .parseNewProductInfo("https://sortiment.metro.sk/sk/pampers-abd-mb-s4p-152ks/241760p/"));
//        System.out.println(new FeedoProductParser(new UnitParserImpl(), userAgentDataHolder)
//                .parseProductUpdateData("https://www.feedo.sk/pampers-active-baby-4-maxi-174ks-8-14kg-mesacna-zasoba-jednorazove-plienky/"));


    }
}
