package sk.hudak.prco.starter

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.*
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.manager.EshopThreadStatisticManager
import sk.hudak.prco.manager.GroupProductResolver
import sk.hudak.prco.manager.WatchDogManager
import sk.hudak.prco.manager.addprocess.AddingNewProductManager
import sk.hudak.prco.manager.export.DbExportImportManager
import sk.hudak.prco.manager.export.HtmlExportManager
import sk.hudak.prco.manager.remove.RemoveEshopManager
import sk.hudak.prco.manager.updateprocess.UpdateProductDataListener
import sk.hudak.prco.manager.updateprocess.UpdateProductDataManager
import sk.hudak.prco.manager.updateprocess.UpdateStatusInfo
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.reporter.FacebookReporter
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.service.UIService
import sk.hudak.prco.service.WatchDogService
import sk.hudak.prco.ssl.PrcoSslManager
import sk.hudak.prco.utils.CalculationUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Arrays.asList

/**
 * Created by jan.hudak on 9/29/2017.
 */
@Component
class Starter(private val updateProductDataManager: UpdateProductDataManager,
              private val internalTxService: InternalTxService,
              private val uiService: UIService,
              private val theadStatisticManager: EshopThreadStatisticManager,
              private val newProductManager: AddingNewProductManager,
              private val facebookReporter: FacebookReporter) {

    @Autowired
    private val dbExportImportManager: DbExportImportManager? = null

    @Autowired
    private val htmlExportManager: HtmlExportManager? = null

    @Autowired
    private val htmlParser: HtmlParser? = null

    @Autowired
    private val watchDogService: WatchDogService? = null

    @Autowired
    private val watchDogManager: WatchDogManager? = null

    @Autowired
    private val groupProductResolver: GroupProductResolver? = null

    @Autowired
    private val removeEshopManager: RemoveEshopManager? = null


    companion object {
        val log = LoggerFactory.getLogger(Starter::class.java)!!
    }

    fun run() {

        //init ssl
        PrcoSslManager.init()

        // start thred for showing statistics
        theadStatisticManager.startShowingStatistics()

        //TODO
        internalTxService.startErrorCleanUp()

        val statisticsOfProducts = uiService.statisticsOfProducts
        println(statisticsOfProducts.toString())


        //        removeEshopManager.removeAllForEshop(EshopUuid.MAMA_A_JA);

        //        System.out.println(">> --------");

        //        Optional<GroupProductKeywords> groupProductKeywords = groupProductResolver.resolveGroup("Pampers Plienky S4P Active Baby mesačné balenie");

        //        System.out.println("");

        //        for (ProductFullDto productFullDto : internalTxService.findProductsInGroup(257L)) {
        //            Optional<GroupProductKeywords> groupProductKeywords = groupProductResolver.resolveGroup(productFullDto.getName());
        //            StringBuilder sb = new StringBuilder();
        //            if (groupProductKeywords.isPresent()) {
        //                sb.append(groupProductKeywords.get().name());
        //            } else {
        //                sb.append("NONE");
        //            }
        //            sb.append(" " + productFullDto.getName());
        //            sb.append(" " + productFullDto.getUrl());
        //            System.out.println(sb.toString());
        //        }

        //        List<ProductFullDto> products = internalTxService.findProductsNotInAnyGroup();
        //        for (ProductFullDto product : products) {
        //            Optional<GroupProductKeywords> groupProductKeywords = groupProductResolver.resolveGroup(product.getName());
        //            StringBuilder sb = new StringBuilder();
        //            if (groupProductKeywords.isPresent()) {
        //                if(!PAMPERS_ZELENE_4.equals(groupProductKeywords.get())){
        //                    continue;
        //                }
        //                sb.append(groupProductKeywords.get().name());
        //            } else {
        //                sb.append("NONE");
        //            }
        //            sb.append(" [" + product.getId()+"]");
        //            sb.append(" " + product.getName());
        //            sb.append(" " + product.getUrl());
        //            System.out.println(sb.toString());
        //        }


        // --- WATCH DOG SERVICE ---
        //        watchDogManager.startWatching("https://www.obi.sk/zahradne-hadice/cmi-zahradna-hadica-12-5-mm-1-2-20-m-zelena/p/2235422",
        //                BigDecimal.valueOf(4.99));
        //        watchDogManager.collectAllUpdateAndSendEmail();

        // --- GROUPY --
        //        createNewGroup("nutrilon 1");
        //        createNewGroup("nutrilon 2");
        //        createNewGroup("nutrilon 3");
        //        createNewGroup("pampers 5 premium");
        //        updateGroupName(1L, "pampers 4 zelene");
        //nutrilon 4
        //        uiService.addProductsToGroup(33L, 1697L);
        //nutrilon 5
        //        uiService.addProductsToGroup(257L, 1700L);
        // pampers zelene 4
        //        uiService.addProductsToGroup(1L, 1669L, 1668L, 1667L, 1666L, 1665L);
        // pampers zelene 5
        //        uiService.addProductsToGroup(321L,1698L, 1699L);

        //        showAllGroups();
        //        uiService.removeProductsFromGroup(1L, 994L, 1226L);


        //        uiService.deleteProducts(169L, 802L);


        //        showDuplicityProductsInEshops();

        //        internalTxService.deleteNotInterestedProducts(
        //                97L,
        //                99L,
        //                5170L);

        //        uiService.deleteNewProducts(5505L);


        // --- PRODUCTS ---
        //        showAllProducts();

        // pampers 4
        //        showProductsInGroup(1L, true, EshopUuid.METRO);
        // pampers 5
        //        showProductsInGroup(321L, true);


        //        internalTxService.removeProductByUrl("https://www.brendon.sk/Products/Details/118425");

        println(facebookReporter.doFullReport())

        // nutrilon 4
        //        showProductsInGroup(33L);
        // nutrilon 5
        //        showProductsInGroup(257L, true);

        //        showProductNotInterested(EshopUuid.DROGERIA_VMD);

        //        deleteProductsFromNotInterested(EshopUuid.DR_MAX);

        // olej
        //        showProductsInGroup(225L);


        //        showAllProductsInAllGroups();
        //        showProductsNotInAnyGroup();


        //        existProduct("https://www.feedo.sk/pampers-active-baby-4-box-120ks-9-16-kg-jednorazove-plienky/");
        //        showProductsInEshop(EshopUuid.TESCO);
        //        showProductsInEshop(EshopUuid.METRO);
        //        showProductsInEshopInAction(EshopUuid.TESCO);
        //        showProductsInEshopInAction(EshopUuid.METRO);
        //        showProductInActionAll();

        //        showProductsInEshopWithBestPriceInGroupOnly(EshopUuid.TESCO);
        //        showProductsInEshopWithBestPriceInGroupOnly(EshopUuid.METRO);


        //        watchDogService.notifyByEmail(Collections.emptyList());

        println("Errors:")
        val errorFindFilterDto = ErrorFindFilterDto()
        errorFindFilterDto.statusCodesToSkip = arrayOf("404")
        errorFindFilterDto.limit = 50
        internalTxService.findErrorsByFilter(errorFindFilterDto)
                .forEach { println(it.customToString()) }

        createGroupKeyWords()
        showGroupKeysWords()

        // --- UPDATE PRICE DATA ---
//        val listener: UpdateProductDataListener = object : UpdateProductDataListener { updateStatusInfo: UpdateStatusInfo ->
//            log.debug(">> eshop: {}, updated/waiting: {}/{}",
//                    updateStatusInfo.eshopUuid, updateStatusInfo.countOfProductsAlreadyUpdated, updateStatusInfo.countOfProductsWaitingToBeUpdated)
//        }

        val listener: UpdateProductDataListener = object : UpdateProductDataListener {
            override fun onUpdateStatus(updateStatusInfo: UpdateStatusInfo) {
                log.debug(">> eshop: {}, updated/waiting: {}/{}",
                        updateStatusInfo.eshopUuid, updateStatusInfo.countOfProductsAlreadyUpdated, updateStatusInfo.countOfProductsWaitingToBeUpdated)

            }
        }

        updateProductDataManager.updateProductDataForEachProductInEachEshop(listener)
        //        updateProductDataManager.updateProductDataForEachProductNotInAnyGroup(listener);

        //        updateProductDataManager.updateProductDataForEachProductInEshop(EshopUuid.ALZA, listener);
        // updatne vsetky produkty v danej skupine
        //        updateProductDataManager.updateProductDataForEachProductInGroup(33L);

        //        updateProductDataManager.updateProductDataForEachProductNotInAnyGroup(listener);
        //        updateProductDataManager.updateProductData(3118L);
        //        uiService.resetUpdateDateForAllProductsInEshop(EshopUuid.TESCO);
        //        uiService.updateProductCommonPrice(449L, BigDecimal.valueOf(0.59));

        // --- ADD NEW PRODUCTS ---
//                newProductManager.addNewProductsByKeywordsForAllEshops("pampers", "nutrilon", "lovela");
        //        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.KID_MARKET, "lovela");
        //        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.PILULKA_24, "lovela");
        //        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.FEEDO, "pampers");
        //        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.ALZA, "pampers 5");
        //        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.ALZA, "pampers");
        //        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.FEEDO, "nutrilon 5");
        //        newProductManager.addNewProductsByUrl(
        //                  "https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002120575818",
        //                  "https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002120307521",
        //                  "https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002014050505"
        //       );

        // --- EXPORT ----

        //*********************************************************
        //        UC_fixOneInvalidNewProduct();
        // ---
        //        internalTxService.confirmUnitDataForNewProducts(36L, 38L);
        // ---
        //        long l = internalTxService.fixAutomaticallyProductUnitData(5);
        // ---
        //        internalTxService.markNewProductAsInterested(33L,34L,36L);

        //        internalTxService.resetUpdateDateProduct(97L);


        //        dbExportImportManager.importNewProductsFromCsv();
        //        dbExportImportManager.importAllTables();


        //        UC_fixOneInvalidNewProduct();

        //        internalTxService.markNewProductAsInterested( );
        //        internalTxService.markNewProductAsNotInterested( );

        //        dbExportImportManager.exportAllTablesToCsvFiles();
        //        htmlExportManager.buildHtml();

        //        internalTxService.test();

        //TODO z GUI vyplnit spravne data
        //        internalTxService.repairInvalidUnitForNewProduct(
        //                348L,
        //                new UnitData(Unit.KILOGRAM, new BigDecimal(5.6), Integer.valueOf(1)));

    }

    private fun createGroupKeyWords() {
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "pro", "care", "s2")));
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "2")));
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "care", "2")));
        //
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "care", "2,")));
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "care", "s2")));
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "care", "newborn", "(2)")));
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "pack", "s2")));
        //
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "newborn", "2")));
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premium", "procare", "2")));
        //        internalTxService.createGroupProductKeywords(new GroupProductKeywordsCreateDto(
        //                452L,
        //                asList("pampers", "premiumcare", "2")));

    }

    private fun showGroupKeysWords() {
        var groupProductKeywordsByGroupId = internalTxService!!.getGroupProductKeywordsByGroupId(449L)
        println(groupProductKeywordsByGroupId.get())
        groupProductKeywordsByGroupId = internalTxService.getGroupProductKeywordsByGroupId(450L)
        println(groupProductKeywordsByGroupId.get())
        //        groupProductKeywordsByGroupId = internalTxService.getGroupProductKeywordsByGroupId(451L);
        //        System.out.println(groupProductKeywordsByGroupId.get());
        //        groupProductKeywordsByGroupId = internalTxService.getGroupProductKeywordsByGroupId(452L);
        //        System.out.println(groupProductKeywordsByGroupId.get());


    }

    private fun showDuplicityProductsInEshops() {

        //        internalTxService.removeProduct(2659L);

        //        EshopUuid eshopUuid = EshopUuid.FEEDO;
        for (eshopUuid in EshopUuid.values()) {
            println("Duplicity for eshop: $eshopUuid")
            val result = internalTxService!!.findDuplicityProductsByNameAndPriceInEshop(eshopUuid)
            for (productFullDto in result) {
                println(productFullDto.id.toString() + ", "
                        + productFullDto.name + " "
                        + productFullDto.priceForPackage + " "
                        + productFullDto.url + " "
                        + formatDate(productFullDto.created))
            }
            println()
        }
    }

    private fun deleteProductsFromNotInterested(eshopUuid: EshopUuid) {
        internalTxService!!.deleteNotInterestedProducts(eshopUuid)
    }

    private fun showProductNotInterested(eshopUuid: EshopUuid) {
        val notInterestedProducts = internalTxService!!.findNotInterestedProducts(NotInterestedProductFindDto(eshopUuid))
        println("Not interested product for eshop: $eshopUuid")
        for (product in notInterestedProducts) {
            println("id " + product.id + ", "
                    + product.eshopUuid + " "
                    + " '" + product.name + "', "
                    + product.url)
        }
    }

    private fun showAllProductsInAllGroups(withPriceOnly: Boolean) {
        uiService!!.findGroups(GroupFilterDto())
                .forEach { group -> group.id?.let { showProductsInGroup(it, withPriceOnly) } }
    }

    private fun showProductInActionAll() {
        val products = uiService!!.findProducts(ProductFilterUIDto(java.lang.Boolean.TRUE))
        println("Produkty v akcii:")
        for (product in products) {
            println("id " + product.id + ", "
                    + product.eshopUuid + " "
                    + formatPrice(product.priceForPackage) +
                    "/" + formatPrice(product.commonPrice) +
                    " (" + formatValidTo(product.actionValidTo) + ") "
                    + " '" + product.name + "', "
                    + product.url)
        }
    }

    private fun showProductsInEshopInAction(eshopUuid: EshopUuid) {
        println()
        println("Produkty v akcii pre eshop $eshopUuid:")

        for (product in uiService!!.findProductsInAction(eshopUuid)) {
            println("id " + product.id + ", "
                    + formatPrice(product.priceForPackage) + "/"
                    + formatPrice(product.commonPrice) + " "
                    + formatPercentage(product.priceForPackage, product.commonPrice)
                    + " '" + product.name + "', "
                    + formatPrice(product.priceForOneItemInPackage, 2) + " " +
                    "(" + formatValidTo(product.actionValidTo) + ") "
                    + product.bestPriceInGroup + " "
                    + product.url)
        }
    }

    private fun showProductsInEshopWithBestPriceInGroupOnly(eshopUuid: EshopUuid) {
        println()
        println("Produkty v akcii a najlepsou cenou v groupe pre eshop $eshopUuid:")

        val products = uiService!!.findProductsBestPriceInGroupDto(eshopUuid)
        for (product in products) {
            println("id " + product.id + ", "
                    + formatPrice(product.priceForUnit, 2) + " "
                    + formatPrice(product.priceForOneItemInPackage, 2) + " "
                    + formatPrice(product.priceForPackage) + "/"
                    + formatPrice(product.commonPrice) + " "
                    + formatPercentage(product.priceForPackage, product.commonPrice) +
                    "(" + formatValidTo(product.actionValidTo) + ") "
                    + " '" + product.name + "', "
                    + product.url)
        }

    }

    private fun formatPercentage(priceForPackage: BigDecimal?, commonPrice: BigDecimal?): String {
        return if (priceForPackage == null || commonPrice == null) {
            ""
        } else CalculationUtils.calculatePercetage(priceForPackage, commonPrice).toString() + "%"

    }

    private fun formatValidTo(date: Date?): String {
        return if (date == null) {
            "-"
        } else SimpleDateFormat("dd.MM.yyyy").format(date)
    }

    private fun showProductsInEshop(eshopUuid: EshopUuid) {
        println("Eshop $eshopUuid")

        val products = uiService!!.findProducts(ProductFilterUIDto(eshopUuid))
        for (product in products) {
            println("id " + product.id + ", "
                    + formatPrice(product.priceForPackage) + "(" + formatValidTo(product.actionValidTo) + ") " + formatPrice(product.commonPrice)
                    + " '" + product.name + "', "
                    + product.groupList + "  "
                    + product.url)
        }
    }

    private fun formatPrice(bigDecimal: BigDecimal?, countOfDecimal: Int): String {
        var bigDecimal: BigDecimal? = bigDecimal ?: return "-"
        bigDecimal = bigDecimal!!.setScale(countOfDecimal, RoundingMode.HALF_UP)
        return formatPrice(bigDecimal)
    }

    private fun formatPrice(bigDecimal: BigDecimal?): String {
        return bigDecimal?.toString()?.replace("0+$".toRegex(), "") ?: "-"
    }

    private fun formatPriceFb(bigDecimal: BigDecimal?): String {
        return if (bigDecimal == null) {
            "-"
        } else DecimalFormat("00.00").format(bigDecimal).replace(".", ",")


    }

    private fun existProduct(productURL: String) {
        val existProductWithUrl = uiService!!.existProductWithUrl(productURL)
        println("product with URL $productURL existuje: $existProductWithUrl")
    }

    private fun showProductsNotInAnyGroup() {
        println()
        println("Products not in any group:")

        val products = uiService!!.findProductsWitchAreNotInAnyGroup()
        for (product in products) {
            println("eshop " + product.eshopUuid + ", " +
                    "id " + product.id + ", " +
                    "name: " + product.name + ", " +
                    "url: " + product.url + ", " +
                    "unit: " + product.unitValue + " " + product.unit + " count: " + product.unitPackageCount)

        }
        if (products.isEmpty()) {
            println("ziadny")
        }
    }

    private fun showProductsInGroup(groupId: Long, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid) {
        val group = uiService!!.getGroupById(groupId)
        val productsInGroup = uiService.findProductsInGroup(groupId, withPriceOnly, *eshopsToSkip)

        // vypis
        println()
        println("'" + group.name + "' id " + groupId + " count " + productsInGroup.size + " withPriceOnly " + withPriceOnly)
        if (!asList(*eshopsToSkip).isEmpty()) {
            println("Preskakujem eshopy: " + asList(*eshopsToSkip))
        }
        for (product in productsInGroup) {
            println("eshop: " + product.eshopUuid!!.name +
                    " price for unit " + product.priceForUnit +
                    ", price for one item " + formatPrice(product.priceForOneItemInPackage) +
                    ", id " + product.id +
                    ", '" + product.name +
                    "', " + product.url +
                    ", last updated " + formatDate(product.lastTimeDataUpdated))
        }
    }


    private fun formatDate(date: Date?): String {
        return if (date == null) {
            ""
        } else SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date)
    }

    private fun showAllProducts() {
        val products = uiService!!.findProducts(ProductFilterUIDto())
        for (product in products) {
            println("id " + product.id + ", name " + product.name + ", url " + product.url)
        }
    }

    private fun showAllGroups() {
        println("Zoznam groups:")
        val groups = uiService!!.findAllGroupExtended()
        for (group in groups) {
            print(group.id.toString() + "/" + group.name + "(" + group.countOfProduct + ") ")
            val countOfProductInEshop = group.countOfProductInEshop
            if (!countOfProductInEshop.isEmpty()) {
                for (eshopUuid in countOfProductInEshop.keys) {
                    print(eshopUuid.toString() + "/" + countOfProductInEshop[eshopUuid] + ", ")
                }
            }
            println()
        }
        println("---")
    }


    private fun updateGroupName(id: Long, name: String) {
        uiService!!.updateGroup(GroupUpdateDto(id, name))
    }

    private fun createNewGroup(groupName: String) {
        uiService!!.createGroup(GroupCreateDto(groupName))
    }

    fun UC_fixOneInvalidNewProduct() {
        println("pocet neplatnych: " + internalTxService!!.countOfInvalidNewProduct)

        val firstInvalidNewProductInfo = internalTxService.findFirstInvalidNewProduct()
        if (firstInvalidNewProductInfo != null) {
            println("je to null")
            return
        }
        println(firstInvalidNewProductInfo)

        //TODO z GUI vyplnit spravne data
        internalTxService.repairInvalidUnitForNewProduct(
                1185L,
                UnitData(Unit.KUS, BigDecimal("1"), Integer.valueOf(1)))

    }

    fun UC_markAsNotInterested(newProductIds: Long?) {
        internalTxService!!.markNewProductAsNotInterested(newProductIds!!)
    }

}
