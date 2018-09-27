package sk.hudak.prco.api;

import lombok.Getter;

/**
 * Created by jan.hudak on 9/29/2017.
 */
public enum EshopUuid {

    //TODO pridat prvy parameter boolean ktory bude hovorit o tom ci je alebo nie je enablovany dany eshop


    ALZA("https://www.alza.sk",
            "https://www.alza.sk/search.htm?exps={keyword}",
            "https://www.alza.sk/search-p{pageNumber}.htm?exps={keyword}",
            3, 12, 5),

    // TODO ANDREA_SHOP

    BAMBINO("https://www.bambino.sk",
            "https://www.bambino.sk/vyhladavanie?search={keyword}",
            "https://www.bambino.sk/vyhladavanie/{pageNumber}?search={keyword}",
            3, 12, 5),

    DR_MAX("https://www.drmax.sk",
            "https://www.drmax.sk/catalog/search/?q={keyword}",
            "https://www.drmax.sk/catalog/search/?q={keyword}&offset={offset}&limit={limit}",
            3, 12, 24),

    FEEDO("https://www.feedo.sk",
            "https://www.feedo.sk/vysledky-hladania/{keyword}/",
            // toto nefunguje: https://www.feedo.sk/vysledky-hladania/nutrilon%204/#page=2
            "https://www.feedo.sk/vysledky-hladania/{keyword}/filter/?strana={pageNumber}",
            5, 12),

    //TODO impl
    HORNBACH("https://www.hornbach.sk/",
            "https://www.hornbach.sk/shop/vyhladavanie/sortiment/{keyword}",
            "TODO nie je to cez parameter v stranke...",
            5, 12),

    // TODO KID_MARKET
    // TODO PERINBABA
    //TODO  https://www.brendon.sk

    PERINBABA(
            "http://www.perinbaba.sk/",
            "http://www.perinbaba.sk/catalogsearch/result/?limit=48&q={keyword}",
            "http://www.perinbaba.sk/catalogsearch/result/index/?limit=48&p={pageNumber}&q={keyword}",
            5, 12),

    MALL("http://mall.sk",
            "https://www.mall.sk/hladaj?s={keyword}",
            "https://www.mall.sk/hladaj?page={pageNumber}&s={keyword}",
            2, 12),

    MOJA_LEKAREN("https://www.mojalekaren.sk/",
            "https://www.mojalekaren.sk/vyhladavanie/?query={keyword}",
            "https://www.mojalekaren.sk/vyhladavanie/?query={keyword}&strana={pageNumber}",
            5, 12),

    METRO("https://sortiment.metro.sk",
            "https://sortiment.metro.sk/sk/search/?q={keyword}",
            "https://sortiment.metro.sk/sk/search/?p={pageNumber}&search_by_price=dph_without&category_id=0&ownbrand=0&product_type_id=0&inaction=0&mysort=0&pcheck=0&local=0&orderby=wght&direction=asc&q={keyword}&extorder=0&onstock=0",
            5, 12),

    //TODO impl
    OBI("https://www.obi.sk",
            "https://www.obi.sk/search/{keyword}/?isi=true",
            "https://www.obi.sk/search/{keyword}?page={pageNumber}",
            5, 12),

    // TODO picture NOT yet
    PILULKA("https://www.pilulka.sk",
            "https://www.pilulka.sk/hledat?q={keyword}",
            "https://www.pilulka.sk/hledat?q={keyword}&page={pageNumber}",
            5, 12),

    TESCO("https://potravinydomov.itesco.sk",
            "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query={keyword}",
            "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query={keyword}&page={pageNumber}",
            5, 12);

    @Getter
    private String productStartUrl;

    @Getter
    private String searchTemplateUrl;

    @Getter
    private String searchTemplateUrlWithPageNumber;

    @Getter
    private int maxCountOfNewPages;

    @Getter
    private int olderThanInHours;

    @Getter
    private int maxCountOfProductOnPage;

    /**
     * @param productStartUrl
     * @param searchTemplateUrl
     * @param searchTemplateUrlWithPageNumber
     * @param maxCountOfNewPages
     * @param olderThanInHours
     * @param maxCountOfProductOnPage
     */
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
     * @param productStartUrl
     * @param searchTemplateUrl
     * @param searchTemplateUrlWithPageNumber
     * @param maxCountOfNewPages
     * @param olderThanInHours
     * @deprecated nepouzivat
     */
    @Deprecated
    EshopUuid(String productStartUrl, String searchTemplateUrl, String searchTemplateUrlWithPageNumber,
              int maxCountOfNewPages, int olderThanInHours) {
        this(productStartUrl, searchTemplateUrl, searchTemplateUrlWithPageNumber, maxCountOfNewPages, olderThanInHours, 5);
    }


    /**
     * Definuje maximalne pocet stran ktore prechadzat pre pridavanie novych produktov, pre dane vyhladavanie slovo
     * {@link Integer#MAX_VALUE} je unlimeted, whis is default.
     *
     * @return
     */
    public int getMaxCountOfNewPages() {
        return maxCountOfNewPages;
    }

}
