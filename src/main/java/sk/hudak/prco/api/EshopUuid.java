package sk.hudak.prco.api;

/**
 * Created by jan.hudak on 9/29/2017.
 */
public enum EshopUuid {
    //TODO pridat prvy parameter boolean ktory bude hovorit o tom ci je alebo nie je enablovany dany eshop
//    ALZA("",
//            "",
//            "",
//            5, 24),

    OBI("https://www.obi.sk",
            "https://www.obi.sk/search/{keyword}/?isi=true",
            "https://www.obi.sk/search/{keyword}?page={pageNumber}",
            5, 24),

    BAMBINO("https://www.bambino.sk",
            "https://www.bambino.sk/vyhladavanie?search={keyword}",
            "https://www.bambino.sk/vyhladavanie/{pageNumber}?search={keyword}",
            3, 12),

    FEEDO("https://www.feedo.sk",
            "https://www.feedo.sk/vysledky-hladania/{keyword}/",
            // toto nefunguje: https://www.feedo.sk/vysledky-hladania/nutrilon%204/#page=2
            "https://www.feedo.sk/vysledky-hladania/{keyword}/filter/?strana={pageNumber}",
            5, 24),

    //TODO impl
    HORNBACH("https://www.hornbach.sk/",
            "https://www.hornbach.sk/shop/vyhladavanie/sortiment/{keyword}",
            "TODO nie je to cez parameter v stranke...",
            5, 24),

    MALL("http://mall.sk",
            "https://www.mall.sk/hladaj?s={keyword}",
            "https://www.mall.sk/hladaj?page={pageNumber}&s={keyword}",
            3, 24),

    METRO("https://sortiment.metro.sk",
            "https://sortiment.metro.sk/sk/search/?q={keyword}",
            "https://sortiment.metro.sk/sk/search/?p={pageNumber}&search_by_price=dph_without&category_id=0&ownbrand=0&product_type_id=0&inaction=0&mysort=0&pcheck=0&local=0&orderby=wght&direction=asc&q={keyword}&extorder=0&onstock=0",
            5, 12),

    PILULKA("https://www.pilulka.sk",
            "https://www.pilulka.sk/hledat?q={keyword}",
            "https://www.pilulka.sk/hledat?q={keyword}&page={pageNumber}",
            5, 24),

    TESCO("https://potravinydomov.itesco.sk",
            "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query={keyword}",
            "https://potravinydomov.itesco.sk/groceries/sk-SK/search?query={keyword}&page={pageNumber}",
            5, 12);

    private String productStartUrl;
    private String searchTemplateUrl;
    private String searchTemplateUrlWithPageNumber;
    private int maxCountOfNewPages;
    private int olderThanInHours;

    EshopUuid(String productStartUrl, String searchTemplateUrl, String searchTemplateUrlWithPageNumber,
              int maxCountOfNewPages, int olderThanInHours) {
        this.productStartUrl = productStartUrl;
        this.searchTemplateUrl = searchTemplateUrl;
        this.searchTemplateUrlWithPageNumber = searchTemplateUrlWithPageNumber;
        this.maxCountOfNewPages = maxCountOfNewPages;
        this.olderThanInHours = olderThanInHours;
    }

    public String getProductStartUrl() {
        return productStartUrl;
    }

    public String getSearchTemplateUrl() {
        return searchTemplateUrl;
    }

    public String getSearchTemplateUrlWithPageNumber() {
        return searchTemplateUrlWithPageNumber;
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

    public int getOlderThanInHours() {
        return olderThanInHours;
    }
}
