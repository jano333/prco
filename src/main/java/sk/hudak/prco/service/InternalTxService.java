package sk.hudak.prco.service;

public interface InternalTxService extends
        NewProductService,
        ProductCommonService,
        ProductService,
        NotInterestedProductService,
        GroupService,
        GroupProductKeywordsService,
        ErrorService,
        WatchDogService
{


    void test();
}
