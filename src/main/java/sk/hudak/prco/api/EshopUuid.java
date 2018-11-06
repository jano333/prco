package sk.hudak.prco.api;

import static sk.hudak.prco.api.SearchTemplateConstants.KEYWORD_TEMP;

/**
 * Created by jan.hudak on 9/29/2017.
 */
public enum EshopUuid {

    // TODO ANDREA_SHOP -> neviem pagging ako...
    // TODO https://www.brendon.sk

    //TODO pridat prvy parameter boolean ktory bude hovorit o tom ci je alebo nie je enablovany dany eshop

    ALZA("https://www.alza.sk",
            "https://www.alza.sk/search.htm?exps=" + KEYWORD_TEMP,
            "https://www.alza.sk/search-p{pageNumber}.htm?exps=" + KEYWORD_TEMP,
            3, 12, -1),

//    ANDREA_SHOP("https://www.andreashop.sk",
//            "https://www.andreashop.sk/vyhladavanie?op=search&search={keyword}",
//            "??",
//            3, 12, -1),


    BAMBINO("https://www.bambino.sk",
            "https://www.bambino.sk/vyhladavanie?search=" + KEYWORD_TEMP,
            "https://www.bambino.sk/vyhladavanie/{pageNumber}?search={keyword}",
            3, 12, -1),

    BRENDON("https://www.brendon.sk",
            "https://www.brendon.sk/Products/List?searchtext={keyword}",
            "https://www.brendon.sk/Products/List?SearchText={keyword}&&Page={pageNumber}&Order=onweb&ProdNo=30",
            5, 12, 30),

    DR_MAX("https://www.drmax.sk",
            "https://www.drmax.sk/catalog/search/?q={keyword}",
            "https://www.drmax.sk/catalog/search/?q={keyword}&offset={offset}&limit={limit}",
            3, 12, 24),

    MAMA_A_JA("https://eshop.mamaaja.sk",
            "https://eshop.mamaaja.sk/catalog/search/?q={keyword}",
            "https://eshop.mamaaja.sk/catalog/search/?q={keyword}&offset={offset}",
            3, 12, 24),

    FEEDO("https://www.feedo.sk",
            "https://www.feedo.sk/vysledky-hladania/{keyword}/",
            "https://www.feedo.sk/vysledky-hladania/{keyword}/filter/?strana={pageNumber}",
            5, 12, -1),

    //TODO impl
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
