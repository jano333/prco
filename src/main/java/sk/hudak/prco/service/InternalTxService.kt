package sk.hudak.prco.service

interface InternalTxService :
        NewProductService,
        ProductCommonService,
        ProductService,
        NotInterestedProductService,
        GroupService,
        GroupProductKeywordsService,
        ErrorService,
        WatchDogService {


    fun test()
}
