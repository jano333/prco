package sk.hudak.prco.test;

import sk.hudak.prco.eshop.KidMarketProductParser;
import sk.hudak.prco.parser.impl.UnitParserImpl;
import sk.hudak.prco.ssl.PrcoSslManager;
import sk.hudak.prco.utils.UserAgentDataHolder;

public class ProductParserTest {

    public static void main(String[] args) {

        PrcoSslManager.getInstance().init();

        UserAgentDataHolder userAgentDataHolder = new UserAgentDataHolder();
        userAgentDataHolder.init();

        UnitParserImpl unitParser = new UnitParserImpl();

        //KidMarket
        System.out.println(new KidMarketProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 5"));
//                .parseNewProductInfo("https://kidmarket.sk/jednorazove-plienky/176-pampers-active-baby-dry-vel6-extra-large-56ks-4015400736394.html?search_query=pampers+5&results=46"));
                .parseProductUpdateData("https://kidmarket.sk/jednorazove-plienky/176-pampers-active-baby-dry-vel6-extra-large-56ks-4015400736394.html?search_query=pampers+5&results=46"));

        //Perinbaba
//        System.out.println(new PerinbabaProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("http://www.perinbaba.sk/plienky-active-baby-dry-3-midi-4-9kg-mega-box-plus-174kg.html"));
//                .parseProductUpdateData("http://www.perinbaba.sk/plienky-active-baby-dry-3-midi-4-9kg-mega-box-plus-174kg.html"));


        // Alza
//        System.out.println(new AlzaProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                System.out.println(new AlzaProductParser(unitParser, userAgentDataHolder)
//                .parseNewProductInfo("https://www.alza.sk/maxi/pampers-active-baby-dry-vel-4-maxi-174-ks-d4593569.htm"));

        //Bambino
//        System.out.println(new BambinoProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("https://www.bambino.sk/jednorazove-plienky/plienkove-nohavicky-premium-pants-4-maxi-44-ks-1"));
//                .parseProductUpdateData("https://www.bambino.sk/jednorazove-plienky/abd-mega-box-plus-4-174ks-1"),

        // Dx max
//        System.out.println(new DrMaxProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("https://www.drmax.sk/nutrilon-4-bez-prichute-akciove-balenie/"));
//                .parseProductUpdateData("https://www.drmax.sk/pampers-mega-maxi-132/"));
        // Feedo
//        System.out.println(new FeedoProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("https://www.feedo.sk/pampers-active-baby-4-maxi-174ks-8-14kg-mesacna-zasoba-jednorazove-plienky/"));
//                .parseProductUpdateData("https://www.feedo.sk/pampers-active-baby-4-maxi-174ks-8-14kg-mesacna-zasoba-jednorazove-plienky/"));


        // HORNBACH
//        System.out.println(new HornbachProductParser(new UnitParserImpl(), userAgentDataHolder)
////                .parseProductUpdateData("https://www.hornbach.sk/shop/Bosch-GBH-2-28-F-s-funkciou-Kick-Back-Control-vr-dlata-a-vrtaka/6348699/artikel.html"));
//                .parseProductUpdateData("https://www.hornbach.sk/shop/Zahradny-domcek-Duramax-Colossus-plechovy/6147837/artikel.html"));


        // OBI
//        System.out.println(new ObiProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("https://www.obi.sk/zahradne-hadice/cmi-zahradna-hadica-12-5-mm-1-2-20-m-zelena/p/2235422"));
//                .parseProductUpdateData("https://www.mall.sk/detske-mlieka/nutrilon-4-6-x-800g"));

        // Tesco
//        System.out.println(new TescoProductParser(unitParser, userAgentDataHolder)
//                 .parseProductUpdateData("https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002017045669"));
//                .parseNewProductInfo("https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002121218158")); //Tesco


        // MALL
//        System.out.println(new MallProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("https://www.mall.sk/detske-mlieka/nutrilon-4-6-x-800g"));
//                .parseProductUpdateData("http://mall.sk/plienky-pampers-7-18-kg/pampers-active-baby-4-maxi-7-14kg-giant-box-90ks"));

        // Metro
//        System.out.println(new MetroProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("https://sortiment.metro.sk/sk/pampers-abd-mb-s4p-152ks/241760p/"));
//                .parseProductUpdateData("https://sortiment.metro.sk/sk/pampers-abd-mb-s4p-152ks/241760p/"));

        // Moja Lekaren
//        System.out.println(new MojaLekarenProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseNewProductInfo("https://www.mojalekaren.sk/pampers-nohavickove-plienky-jumbo-pack-velkost-4-9-14kg-52-kusov/"));
//                .parseProductUpdateData("https://www.mojalekaren.sk/pampers-nohavickove-plienky-jumbo-pack-velkost-4-9-14kg-52-kusov/"));

        // Pilulka
//        System.out.println(new PilulkaProductParser(unitParser, userAgentDataHolder)
//                .parseUrlsOfProduct("nutrilon"));
//                .parseNewProductInfo("https://www.pilulka.sk/pa-plienky-abd-giant-cube-plus-s5-78"));
//                .parseProductUpdateData("https://www.pilulka.sk/nutrilon-3-ha-800g-5-1-zdarma"));


    }
}
