package sk.hudak.prco.service;

public interface InternalTxService extends
        NewProductService,
        ProductCommonService,
        ProductService,
        GroupService,
        ErrorService,
        WatchDogService
{


    void test();
}
