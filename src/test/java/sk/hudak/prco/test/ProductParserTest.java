package sk.hudak.prco.test;

import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.builder.impl.SearchUrlBuilderImpl;
import sk.hudak.prco.eshop.MallProductParser;
import sk.hudak.prco.parser.impl.UnitParserImpl;
import sk.hudak.prco.ssl.PrcoSslManager;
import sk.hudak.prco.utils.UserAgentDataHolder;

public class ProductParserTest {

    public static void main(String[] args) {

        PrcoSslManager.getInstance().init();

        UserAgentDataHolder userAgentDataHolder = new UserAgentDataHolder();
        userAgentDataHolder.init();

        UnitParserImpl unitParser = new UnitParserImpl();

        SearchUrlBuilder searchUrlBuilder = new SearchUrlBuilderImpl();

////         Lekaren Bella
//        System.out.println(new LekarenBellaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
////                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.lekaren-bella.sk/zbozi/3616340/pampers-kalhotkove-plenky-jumbo-pack-s3-60ks"));
////                .parseProductUpdateData("https://www.lekaren-bella.sk/zbozi/3134039/pampers-active-baby-vpp-4-maxi-58ks"));

//         Drogerka
//        System.out.println(new DrogerkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
////                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.drogerka.sk/pampers-premium-care-detske-plienky-junior-44-ks?search=pampers"));
////                .parseProductUpdateData("https://www.drogerka.sk/pampers-premium-care-detske-plienky-junior-44-ks?search=pampers"));

        //         Lekaren expres
//        System.out.println(new LekarenExpresProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.lekarenexpres.sk/matka-a-dieta/detska-vyziva/mliecna-vyziva/kase/nutrilon-kase-allergy-nemlecna-proexpert-250g-4m-11682.html"));
//                .parseProductUpdateData("https://www.lekarenexpres.sk/kozmetika-hygiena-domacnost/hygienicke-prostriedky-a-prostriedky-pre-domacnos/obrusky-hygienicke/pro-deti/pampers-vlhcene-ubrousky-baby-fresh-64ks-14882.html"));
//                .parseProductUpdateData("https://www.drogerka.sk/pampers-premium-care-detske-plienky-junior-44-ks?search=pampers"));
//
         // Giga lekaren
//        System.out.println(new GigaLekarenProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.gigalekaren.sk/produkt/pampers-premium-care-newborn-2-5kg-88ks/"));
//                .parseProductUpdateData("https://www.gigalekaren.sk/produkt/pampers-active-baby-vpp-junior-11-18kg-50ks/"));
//
        // Manago
//        System.out.println(new ManagoProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.magano.sk/pampers-detske-vlhcene-utierky-64ks-fresh-clean"));
//                .parseProductUpdateData("https://www.magano.sk/pampers-pants-midi-60ks-6-11kg-velkost-3"));


        // Internetova lekaren
//        System.out.println(new InternetovaLekarenProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("http://www.internetovalekaren.eu/pampers-premium-newborn-1-2-5kg-88-ks-pampers-premium-newborn-1-2-5kg-88ks-mesacna-zasoba-plienok-darceky-zdarma/"));
//                .parseProductUpdateData("http://www.internetovalekaren.eu/pampers-premium-newborn-3-midi-5-9kg-60-ks-pampers-premium-newborn-3-midi-5-9kg-60-ks-mesacna-zasoba-plienok-darcekyzdarma/"));


//        Eso drogeria
//        System.out.println(new EsoDrogeriaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.esodrogeria.eu/PAMPERS-ACTIVE-BABY-DRY-3-MIDI-4-9KG-GIANT-PACK-90KS-d435.htm"));
//                .parseProductUpdateData("https://www.esodrogeria.eu/PAMPERS-ACTIVE-BABY-DRY-3-MIDI-4-9KG-GIANT-PACK-90KS-d435.htm"));

        // Amd drogeria
//        System.out.println(new AmdDrogeriaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.amddrogeria.sk/pampers-active-baby-plienky-4-maxi-7-14-kg-76-ks/"));
//                .parseProductUpdateData("https://www.amddrogeria.sk/pampers-giant-pack-junior-5-150-ks/"));
//                .parseProductUpdateData("https://www.amddrogeria.sk/pampers-active-baby-plienky-4-maxi-7-14-kg-76-ks/"));

        // Drogeria Vmd
//        System.out.println(new DrogeriaVmdProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.drogeria-vmd.sk/pampers-maxi-pack-4-9-14kg-58ks-0819/"));
//                .parseProductUpdateData("https://www.drogeria-vmd.sk/pampers-maxi-pack-4-9-14kg-58ks-0819/"));

//        // Alza
//        System.out.println(new AlzaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseProductNewData("https://www.alza.sk/maxi/pampers-active-baby-dry-vel-4-maxi-174-ks-d4593569.htm"));
//                .parseProductUpdateData("https://www.alza.sk/maxi/lovela-prasok-color-5-kg-40-prani-d5531490.htm"));
//
//        // Bambino
//        System.out.println(new BambinoProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseProductNewData("https://www.bambino.sk/jednorazove-plienky/plienkove-nohavicky-premium-pants-4-maxi-44-ks-1"));
//                .parseProductUpdateData("https://www.bambino.sk/jednorazove-plienky/abd-mega-box-plus-4-174ks-1"),
//
//        // Brendon
//        System.out.println(new BrendonProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.brendon.sk/Products/Details/118437"));
//                .parseProductUpdateData("https://www.brendon.sk/Products/Details/84545"));
//
//        // Feedo
//        System.out.println(new FeedoProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.feedo.sk/pampers-active-baby-4-maxi-174ks-8-14kg-mesacna-zasoba-jednorazove-plienky/"));
//                .parseProductUpdateData("https://www.feedo.sk/pampers-active-baby-4-10-15-kg-70-ks-giant-pack-jednorazove-plienky/"));
//
        // Lekaren v kocke
//        System.out.println(new LekarenVKockeProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.lekarenvkocke.sk/zq63a34d05b2ee15854983854d75bc3696-pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-1x22-ks"));
//                .parseProductUpdateData("https://www.lekarenvkocke.sk/zqcca7c9d69cc89a501513a05ef16592f4-nutrilon-4-batolacia-mliecna-vyziva-v-prasku-24-36-mesiacov-inov-2018-1x800-g"));
//
//        // MaxikovyHracky
//        System.out.println(new MaxikovyHrackyProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("lego"));
//                .parseProductNewData("https://www.maxikovy-hracky.cz/pampers-active-baby-3-midi-174ks"));
//                .parseProductUpdateData("https://www.maxikovy-hracky.cz/pampers-active-baby-monthly-box-s4-152ks?zmena_meny=EUR"));

//        // FourKids
//        System.out.println(new FourKidsProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.maxikovy-hracky.cz/pampers-active-baby-3-midi-174ks"));
//                .parseProductUpdateData("https://www.maxikovy-hracky.cz/pampers-active-baby-3-midi-174ks?zmena_meny=EUR"));
//
//        // KidMarket
//        System.out.println(new KidMarketProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://kidmarket.sk/jednorazove-plienky/183-pampers-active-baby-4-maxi-pack-58ks.html"));
//                .parseProductUpdateData("https://kidmarket.sk/jednorazove-plienky/183-pampers-active-baby-4-maxi-pack-58ks.html"));
//                .parseProductUpdateData("https://kidmarket.sk/jednorazove-plienky/183-pampers-active-baby-4-maxi-pack-58ks.html"));
//                .parseProductUpdateData("https://kidmarket.sk/jednorazove-plienky/177-pampers-active-baby-5-giant-pack-64ks.html?search_query=pampers&results=64"));
//
//        // Perinbaba
//        System.out.println(new PerinbabaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseProductNewData("http://www.perinbaba.sk/plienky-active-baby-dry-3-midi-4-9kg-mega-box-plus-174kg.html"));
//                .parseProductUpdateData("http://www.perinbaba.sk/plienky-active-baby-dry-3-midi-4-9kg-mega-box-plus-174kg.html"));
//
        // Prva Lekaren
//        System.out.println(new PrvaLekarenProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://www.prva-lekaren.sk/zbozi/3376796/pampers-premium-care-pack-s0-30-ks-newborn"));
//                .parseProductUpdateData("https://www.prva-lekaren.sk/zbozi/3376796/pampers-premium-care-pack-s0-30-ks-newborn"));
//
//        // HORNBACH
//        System.out.println(new HornbachProductParser(new UnitParserImpl(), userAgentDataHolder, searchUrlBuilder)
////                .parseProductUpdateData("https://www.hornbach.sk/shop/Bosch-GBH-2-28-F-s-funkciou-Kick-Back-Control-vr-dlata-a-vrtaka/6348699/artikel.html"));
//                .parseProductUpdateData("https://www.hornbach.sk/shop/Zahradny-domcek-Duramax-Colossus-plechovy/6147837/artikel.html"));

// Dx max
//        System.out.println(new DrMaxProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers/ 5"));
//                .parseProductNewData("https://www.drmax.sk/nutrilon-1-pronutra/"));
//                .parseProductUpdateData("https://www.drmax.sk/pampers-mega-maxi-132/"));


        // OBI
//        System.out.println(new ObiProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseProductNewData("https://www.obi.sk/zahradne-hadice/cmi-zahradna-hadica-12-5-mm-1-2-20-m-zelena/p/2235422"));
//                .parseProductUpdateData("https://www.mall.sk/detske-mlieka/nutrilon-4-6-x-800g"));

        // Tesco
//        System.out.println(new TescoProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseProductNewData("https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002120523541"));
//                 .parseProductUpdateData("https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002017045669"));


//         MALL
        System.out.println(new MallProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
                .parseProductNewData("http://mall.sk/plienky-pampers-7-18-kg/pampers-pure-protection-s4-28-ks-914-kg"));
//                .parseProductUpdateData("http://mall.sk/plienky-pampers-7-18-kg/pampers-active-baby-4-maxi-7-14kg-giant-box-90ks"));

        //        MamaAJa(UZ neexistuje)
//        System.out.println(new MamaAJaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers"));
//                .parseProductNewData("https://eshop.mamaaja.sk/pampers-premium-care-1-newborn-22ks/"));
//                .parseProductUpdateData("https://eshop.mamaaja.sk/pampers-premium-care-1-newborn-22ks/"));


        // Metro
//        System.out.println(new MetroProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseProductNewData("https://sortiment.metro.sk/sk/pampers-abd-mb-s4p-152ks/241760p/"));
//                .parseProductUpdateData("https://sortiment.metro.sk/sk/pampers-abd-mb-s4p-152ks/241760p/"));

        // Moja Lekaren
//        System.out.println(new MojaLekarenProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("pampers 4"));
//                .parseProductNewData("https://www.mojalekaren.sk//pampers-active-baby-dry-5-junior-11-18kg-150-kusov/"));
//                .parseProductUpdateData("https://www.mojalekaren.sk//pampers-active-baby-dry-5-junior-11-18kg-150-kusov/"));

        // Pilulka
//        System.out.println(new PilulkaProductParser(unitParser, userAgentDataHolder, searchUrlBuilder)
//                .parseUrlsOfProduct("nutrilon"));
//                .parseProductNewData("https://www.pilulka.sk/pampers-s4-active-baby-monthly-box-174ks"));
//                .parseProductUpdateData("https://www.pilulka.sk/nutrilon-3-ha-800g-5-1-zdarma"));


    }

}
