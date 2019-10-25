package sk.hudak.prco.api;

public enum EshopUuid {

    // diskontdrogerie.cz
    // Demro
    // FunKids
    // Babyplace
    // CKD market.sk
    // predeti.sk

    // -- DROGERIE --
    //TODO https://www.diskontdrogerie.cz/plenky/pampers-premium-care-1-new-born-88ks.html

    // -- LEKARNE SK --
    //TODO https://www.lekarenexpres.sk/kozmetika-hygiena-domacnost/hygienicke-prostriedky-a-prostriedky-pre-domacnos/plienky-a-plenove-nohavicky-pre-deti/pampers-premium-care-1-newborn-88ks-18815.html
    //TODO http://www.sos-lekaren.sk/tehotne-a-kojici-deti/pampers-premium-care-newborn-2-5kg-88ks/
    //TODO https://www.lekaren-doktorka.sk/3230449-pampers-premium-care-1-newborn-88ks
    //TODO https://www.liekyrazdva.sk/zq7b441326fad7a1ce98481e5344efbb5f-pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-1x88-ks
    //TODO https://www.lekarensedmokraska.sk/kategorie/kozmetika-a-drogeria/pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-88-ks.html#16920
    //TODO https://www.lekarentriveze.sk/z7b441326fad7a1ce98481e5344efbb5f-pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-kg-1x88-ks

    // -- INE SK--
    //TODO https://edigital.sk/plienky/pampers-activebaby-dry-monthly-box-4-maxi-plienky-174-ks-p510052
    //TODO https://www.pompo.sk/1/pSOL%2081603989-1/?utm_campaign=20-40&utm_content=Hracky+a+vsetko+pre+baby+%7C+Plienky+a+prebalovanie&utm_medium=cpc&utm_source=heureka.sk&utm_term=Pampers+Active+Baby-Dry+Vel.+4%2C+174+ks
    //TODO http://www.bugy.sk/static/produkt/36496/PAMPERS-Premium-Care-1-Najhebkejsie-jednorazove-plienky-pre-deti-od-2kg-do-5kg-88ks/
    //TODO https://www.babyplace.sk/detske-plienky-88-ks-2-5-kg-pampers-premium-newborn-1/
    //TODO https://www.parfemomania.sk/pampers-premium-care-1-newborn-2-5-kg-plenkove-kalhotky-88-kusu/
    //TODO https://www.vitalek.sk/detske-plienky/pampers-premium-care-1-newborn-detske-plienky-od-narodenia-2-5-k+36344/
    //TODO https://www.vitazenit.sk/plienky/pampers-premium-care-1-newborn-88-ks--2-5-kg-/
    //TODO https://apateka.sk/produkt/pampers-premium-care-1-newborn-2-5-kg-88-kusov/
    //TODO https://www.farby.sk/210477/pampers-premium-newborn-88/

    // -- LEKARNE CZ --
    //TODO http://www.novalekarna.eu/index.php?detail=3230449&jazyk=sk
    //TODO http://www.lekynadosah.cz/index.php?detail=3230449&mena=eu
    //TODO https://www.lekarna-oaza.cz/3230449-pampers-premium-care-1-newborn-88ks
    //TODO https://www.mujlekarnik.cz/pampers-premium-care-1-newborn-88ks_detail/?currency=EUR

    // -- INE CZ--
    //TODO https://www.funkids.cz/pampers-active-baby-monthly-box-s4-174ks
    //TODO https://www.elixi.cz/jednorazove-pleny-2/pampers-active-baby-dry-3-midi-4-9kg--66ks/

    ALZA(AlzaEshopConfiguration.INSTANCE),
    AMD_DROGERIA(AmdDrogeriaEshopConfiguration.INSTANCE),

    BRENDON(BrendonEshopConfiguration.INSTANCE),

    DR_MAX(DrMaxConfiguration.INSTANCE),
    DROGERIA_VMD(DrogeriaVmdConfiguration.INSTANCE),
    DROGERKA(DrogerkaConfiguration.INSTANCE),

    ESO_DROGERIA(EsoDrogeriaConfiguration.INSTANCE),

    FARBY(FarbyConfiguration.INSTANCE),
    FEEDO(FeedoEshopConfiguration.INSTANCE),
    FOUR_KIDS(FourKidsEshopConfiguration.INSTANCE),

    GIGA_LEKAREN(GigaLekarenEshopConfiguration.INSTANCE),

    //TODO impl,
    HORNBACH(HornbachEshopConfiguration.INSTANCE),

    INTERNETOVA_LEKAREN(InternetovaLekarenEshopConfiguration.INSTANCE),

    KID_MARKET(KidMarketEshopConfiguration.INSTANCE),

    LEKAREN_BELLA(LekarenBellaEshopConfiguration.INSTANCE),
    LEKAREN_EXPRES(LekareExpresEshopConfiguration.INSTANCE),
    LEKAREN_V_KOCKE(LekareVKockeEshopConfiguration.INSTANCE),

    MAGANO(MaganoEshopConfiguration.INSTANCE),
    MALL(MallEshopConfiguration.INSTANCE),
    METRO(MetroEshopConfiguration.INSTANCE),
    MOJA_LEKAREN(MojaLekarenEshopConfiguration.INSTANCE),

    //TODO impl
    OBI(ObiEshopConfiguration.INSTANCE),

    PERINBABA(PerinbabaEshopConfiguration.INSTANCE),
    PRVA_LEKAREN(PrvaLekarenEshopConfiguration.INSTANCE),
    PILULKA(PilulkaEshopConfiguration.INSTANCE),
    PILULKA_24(Pilulka24EshopConfiguration.INSTANCE),

    TESCO(TescoEshopConfiguration.INSTANCE);

    private String productStartUrl;
    private String searchTemplateUrl;
    private String searchTemplateUrlWithPageNumber;
    private int maxCountOfNewPages;
    private int olderThanInHours;
    private int maxCountOfProductOnPage;
    private int countToWaitInSecond;
    private EshopCategory category;
    private EshopConfiguration config;

    EshopUuid(EshopConfiguration config) {
        this.config = config;
        //TODO zrusit tie premenenne a nahladit imploementociou pre dynamik
        this.category = config.getCategory();
        this.productStartUrl = config.getProductStartUrl();
        this.searchTemplateUrl = config.getSearchTemplateUrl();
        this.searchTemplateUrlWithPageNumber = config.getSearchTemplateUrlWithPageNumber();
        this.maxCountOfNewPages = config.getMaxCountOfNewPages();
        this.olderThanInHours = config.getOlderThanInHours();
        this.maxCountOfProductOnPage = config.getMaxCountOfProductOnPage();
        this.countToWaitInSecond = 3;
    }

    /**
     * use config constructor instead
     *
     * @param category
     * @param productStartUrl
     * @param searchTemplateUrl
     * @param searchTemplateUrlWithPageNumber
     * @param maxCountOfNewPages
     * @param olderThanInHours
     * @param maxCountOfProductOnPage
     */
    @Deprecated
    EshopUuid(EshopCategory category, String productStartUrl, String searchTemplateUrl, String searchTemplateUrlWithPageNumber,
              int maxCountOfNewPages, int olderThanInHours, int maxCountOfProductOnPage) {
        this.category = category;
        this.productStartUrl = productStartUrl;
        this.searchTemplateUrl = searchTemplateUrl;
        this.searchTemplateUrlWithPageNumber = searchTemplateUrlWithPageNumber;
        this.maxCountOfNewPages = maxCountOfNewPages;
        this.olderThanInHours = olderThanInHours;
        this.maxCountOfProductOnPage = maxCountOfProductOnPage;
        this.countToWaitInSecond = 3;
    }

    public EshopConfiguration getConfig() {
        return config;
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

    public EshopCategory getCategory() {
        return category;
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

    public int getCountToWaitInSecond() {
        return countToWaitInSecond;
    }
}
