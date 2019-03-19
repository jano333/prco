package sk.hudak.prco.service;

public interface InternalTxService extends
        NewProductService,
        ProductCommonService,
        ProductService,
        NotInterestedProductService,
        GroupService,
        ErrorService,
        WatchDogService
{


    void test();
}
