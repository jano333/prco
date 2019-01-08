package sk.hudak.prco.api;

import static sk.hudak.prco.api.SearchTemplateConstants.KEYWORD_TEMP;
import static sk.hudak.prco.api.SearchTemplateConstants.OFFSET_TEMP;
import static sk.hudak.prco.api.SearchTemplateConstants.PAGE_NUMBER_TEMP;

/**
 * Created by jan.hudak on 9/29/2017.
 */
public enum EshopUuid {

    // -- DROGERIE --
    //TODO https://www.amddrogeria.sk/pampers-active-baby-junior-5-plienky-64-ks/
    //TODO https://www.drogeria-vmd.sk/pampers-prem-1-newbor-88ks-2-5kg-1602/
    //TODO https://www.diskontdrogerie.cz/plenky/pampers-premium-care-1-new-born-88ks.html

    //TODO https://www.drogerka.sk/pampers-premium-care-detske-plienky-newborn-88-ks // nie su tam pampers 5

    // -- LEKARNE --
    //TODO http://www.internetovalekaren.eu/akcia-pampers-plienky-premuim-utierky-sensitive/?utm_source=heureka&utm_medium=porovnavac&utm_campaign=heureka
    //TODO http://www.gigalekaren.sk/produkt/pampers-premium-care-newborn-2-5kg-88ks/
    //TODO https://www.mojalekaren.sk/pampers-premium-care-1-newborn-2-5kg-88-kusov/?utm_source=heureka.sk&utm_medium=product&utm_campaign=heureka.sk
    //TODO http://www.novalekarna.eu/index.php?detail=3230449&jazyk=sk
    //TODO https://www.prva-lekaren.sk/zbozi/3230449/pampers-premium-care-1-newborn-88ks
    //TODO https://www.lekarenvkocke.sk/zq7b441326fad7a1ce98481e5344efbb5f-pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-1x88-ks
    //TODO https://www.lekaren-bella.sk/zbozi/3230449/pampers-premium-care-1-newborn-88ks
    //TODO https://www.lekarenexpres.sk/kozmetika-hygiena-domacnost/hygienicke-prostriedky-a-prostriedky-pre-domacnos/plienky-a-plenove-nohavicky-pre-deti/pampers-premium-care-1-newborn-88ks-18815.html
    //TODO http://www.sos-lekaren.sk/tehotne-a-kojici-deti/pampers-premium-care-newborn-2-5kg-88ks/
    //TODO https://www.lekaren-doktorka.sk/3230449-pampers-premium-care-1-newborn-88ks
    //TODO https://www.liekyrazdva.sk/zq7b441326fad7a1ce98481e5344efbb5f-pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-1x88-ks
    //TODO https://www.lekarensedmokraska.sk/kategorie/kozmetika-a-drogeria/pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-88-ks.html#16920
    //TODO https://www.lekarentriveze.sk/z7b441326fad7a1ce98481e5344efbb5f-pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-1x88-ks

    // -- LEKARNE CZ --
    //TODO http://www.lekynadosah.cz/index.php?detail=3230449&mena=eu
    //TODO https://www.lekarna-oaza.cz/3230449-pampers-premium-care-1-newborn-88ks
    //TODO https://www.mujlekarnik.cz/pampers-premium-care-1-newborn-88ks_detail/?currency=EUR

    // -- INE --
    //TODO https://www.funkids.cz/pampers-active-baby-monthly-box-s4-174ks
    //TODO https://edigital.sk/plienky/pampers-activebaby-dry-monthly-box-4-maxi-plienky-174-ks-p510052
    //TODO https://www.pompo.sk/1/pSOL%2081603989-1/?utm_campaign=20-40&utm_content=Hracky+a+vsetko+pre+baby+%7C+Plienky+a+prebalovanie&utm_medium=cpc&utm_source=heureka.sk&utm_term=Pampers+Active+Baby-Dry+Vel.+4%2C+174+ks
    //TODO http://www.bugy.sk/static/produkt/36496/PAMPERS-Premium-Care-1-Najhebkejsie-jednorazove-plienky-pre-deti-od-2kg-do-5kg-88ks/
    //TODO https://www.babyplace.sk/detske-plienky-88-ks-2-5-kg-pampers-premium-newborn-1/
    //TODO https://www.parfemomania.sk/pampers-premium-care-1-newborn-2-5-kg-plenkove-kalhotky-88-kusu/
    //TODO https://www.vitalek.sk/detske-plienky/pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-k+36344/
    //TODO https://www.vitazenit.sk/plienky/pampers-premium-care-1-newborn-88-ks--2-5-kg-/
    //TODO https://www.elixi.cz/jednorazove-pleny-2/pampers-active-baby-dry-3-midi-4-9kg--66ks/
    //TODO https://apateka.sk/produkt/pampers-premium-care-1-newborn-2-5-kg-88-kusov/
    //TODO https://www.farby.sk/210477/pampers-premium-newborn-88/

    // TODO ANDREA_SHOP -> neviem pagging ako...

    //TODO pridat prvy parameter boolean ktory bude hovorit o tom ci je alebo nie je enablovany dany eshop


    ALZA("https://www.alza.sk",
            "https://www.alza.sk/search.htm?exps=" + KEYWORD_TEMP,
            "https://www.alza.sk/search-p" + PAGE_NUMBER_TEMP + ".htm?exps=" + KEYWORD_TEMP,
            5, 12, 24),

//    ANDREA_SHOP("https://www.andreashop.sk",
//            "https://www.andreashop.sk/vyhladavanie?op=search&search={keyword}",
//            "??",
//            3, 12, -1),

    AMD_DROGERIA("https://www.amddrogeria.sk",
            "https://www.amddrogeria.sk/catalog/search/?q=" + KEYWORD_TEMP,
            "https://www.amddrogeria.sk/catalog/search/?q={keyword}&offset=" + OFFSET_TEMP,
            5, 12, 24),

    BAMBINO("https://www.bambino.sk",
            "https://www.bambino.sk/vyhladavanie?search=" + KEYWORD_TEMP,
            "https://www.bambino.sk/vyhladavanie/{pageNumber}?search={keyword}",
            4, 12, -1),

    BRENDON("https://www.brendon.sk",
            "https://www.brendon.sk/Products/List?searchtext={keyword}",
            "https://www.brendon.sk/Products/List?SearchText={keyword}&&Page={pageNumber}&Order=onweb&ProdNo=30",
            5, 12, 30),

    DR_MAX("https://www.drmax.sk",
            "https://www.drmax.sk/catalog/search/?q={keyword}",
            "https://www.drmax.sk/catalog/search/?q={keyword}&offset={offset}&limit={limit}",
            4, 12, 24),

    FEEDO("https://www.feedo.sk",
            "https://www.feedo.sk/vysledky-hladania/{keyword}/",
            "https://www.feedo.sk/vysledky-hladania/{keyword}/filter/?strana={pageNumber}",
            5, 12, 20),

    FOUR_KIDS("https://www.4kids.sk",
            "https://www.4kids.sk/vyhledavani?search=" + KEYWORD_TEMP,
            "https://www.4kids.sk/vyhledavani?page=" + PAGE_NUMBER_TEMP + "&search=" + KEYWORD_TEMP,
            5, 12, -1),

    //TODO impl,
    HORNBACH("https://www.hornbach.sk/",
            "https://www.hornbach.sk/shop/vyhladavanie/sortiment/{keyword}",
            "TODO nie je to cez parameter v stranke...",
            5, 12, -1),

    KID_MARKET("https://kidmarket.sk/",
            "https://kidmarket.sk/vyhladavanie?controller=search&orderby=position&orderway=desc&search_query={keyword}&submit_search=",
            "https://kidmarket.sk/vyhladavanie?controller=search&orderby=position&orderway=desc&search_query={keyword}&submit_search=&p={pageNumber}",
            5, 12, -1),

    PERINBABA(
            "http://www.perinbaba.sk/",
            "http://www.perinbaba.sk/catalogsearch/result/?limit=48&q={keyword}",
            "http://www.perinbaba.sk/catalogsearch/result/index/?limit=48&p={pageNumber}&q={keyword}",
            5, 12, -1),

    MALL("http://mall.sk",
            "https://www.mall.sk/hladaj?s={keyword}",
            "https://www.mall.sk/hladaj?page={pageNumber}&s={keyword}",
            2, 12, -1),

    //TODO tento skoncil
    MAMA_A_JA("https://eshop.mamaaja.sk",
            "https://eshop.mamaaja.sk/catalog/search/?q={keyword}",
            "https://eshop.mamaaja.sk/catalog/search/?q={keyword}&offset={offset}",
            5, 12, 24),

    MAXIKOVY_HRACKY("https://www.maxikovy-hracky.cz",
            "https://www.maxikovy-hracky.cz/vyhledavani?search=" + KEYWORD_TEMP,
            "https://www.maxikovy-hracky.cz/vyhledavani?page=" + PAGE_NUMBER_TEMP + "&search=" + KEYWORD_TEMP,
            3, 12, 60),

    MOJA_LEKAREN("https://www.mojalekaren.sk/",
            "https://www.mojalekaren.sk/vyhladavanie/?query={keyword}",
            "https://www.mojalekaren.sk/vyhladavanie/?query={keyword}&strana={pageNumber}",
            5, 12, -1),

    METRO("https://sortiment.metro.sk",
            "https://sortiment.metro.sk/sk/search/?q={keyword}",
            "https://sortiment.metro.sk/sk/search/?p={pageNumber}&search_by_price=dph_without&category_id=0&ownbrand=0&product_type_id=0&inaction=0&mysort=0&pcheck=0&local=0&orderby=wght&direction=asc&q={keyword}&extorder=0&onstock=0",
            5, 12, -1),

    //TODO impl
    OBI("https://www.obi.sk",
            "https://www.obi.sk/search/{keyword}/?isi=true",
            "https://www.obi.sk/search/{keyword}?page={pageNumber}",
            5, 12, -1),

    // TODO picture NOT yet
    PILULKA("https://www.pilulka.sk",
            "https://www.pilulka.sk/hledat?q={keyword}",
            "https://www.pilulka.sk/hledat?q={keyword}&page={pageNumber}",
            5, 12, -1),

    TESCO("https://potravinydomov.itesco.sk",
            "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query={keyword}",
            "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query={keyword}&page={pageNumber}",
            5, 12, -1);

    private String productStartUrl;

    private String searchTemplateUrl;

    private String searchTemplateUrlWithPageNumber;

    private int maxCountOfNewPages;

    private int olderThanInHours;

    private int maxCountOfProductOnPage;

    EshopUuid(String productStartUrl, String searchTemplateUrl, String searchTemplateUrlWithPageNumber,
              int maxCountOfNewPages, int olderThanInHours, int maxCountOfProductOnPage) {

        this.productStartUrl = productStartUrl;
        this.searchTemplateUrl = searchTemplateUrl;
        this.searchTemplateUrlWithPageNumber = searchTemplateUrlWithPageNumber;
        this.maxCountOfNewPages = maxCountOfNewPages;
        this.olderThanInHours = olderThanInHours;
        this.maxCountOfProductOnPage = maxCountOfProductOnPage;
    }

    /**
     * Definuje prefix URL(vecsioun domain name) pre dany eshop, pouziva pre doskladanie full URL,
     * ak mame len relativnu cestu. napr. pre URL k obrazku daneho produktu.
     *
     * @return
     */
    public String getProductStartUrl() {
        return productStartUrl;
    }

    /**
     * @return URL, ktora sa pouzije pre vyhladavanie bez pagging atributov ako su pageNumber, offset, limit, ...
     */
    public String getSearchTemplateUrl() {
        return searchTemplateUrl;
    }

    /**
     * @return URL, ktora sa pouzije pre vyhladavanie spolu s paggin atributmy ako ako su pageNumber, offset, limit, ...
     */
    public String getSearchTemplateUrlWithPageNumber() {
        return searchTemplateUrlWithPageNumber;
    }

    /**
     * Definuje maximalny pocet stran, ktore prechadzat, pre pridavanie novych produktov.<br/>
     * <b>Pozor</b>, nie je to maximalny pocet produktov ale maximalny pocet stran.
     *
     * @return
     */
    public int getMaxCountOfNewPages() {
        return maxCountOfNewPages;
    }

    /**
     * maximalne kolko stare data pre produkt su este povazovane za aktualne voci aktualnemu datumu
     *
     * @return
     */
    public int getOlderThanInHours() {
        return olderThanInHours;
    }

    /**
     * maximalne kolko produktov moze byt na stranke,  takzvany 'limit' <br/>
     * -1 definuje ze dany eshop to nepodporuje<br/>
     * pozri volanie danej metody
     *
     * @return
     */
    public int getMaxCountOfProductOnPage() {
        return maxCountOfProductOnPage;
    }
}
