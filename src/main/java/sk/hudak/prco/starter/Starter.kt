package sk.hudak.prco.starter

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.console.PrcoConsole
import sk.hudak.prco.dto.GroupFilterDto
import sk.hudak.prco.dto.SearchKeywordCreateDto
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.manager.add.AddProductManager
import sk.hudak.prco.manager.export.DbExportImportManager
import sk.hudak.prco.manager.export.HtmlExportManager
import sk.hudak.prco.manager.group.GroupProductResolver
import sk.hudak.prco.manager.remove.RemoveEshopManager
import sk.hudak.prco.manager.update.UpdateProductManager
import sk.hudak.prco.manager.watchdog.WatchDogManager
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
class Starter(private val addProductManager: AddProductManager,
              private val updateProductManager: UpdateProductManager,
              private val internalTxService: InternalTxService,
              private val uiService: UIService,
              private val facebookReporter: FacebookReporter,
              private val prcoConsole: PrcoConsole) {

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

        //TODO
        internalTxService.startErrorCleanUp()

        println(uiService.statisticsOfProducts().toString())
        //        System.out.println(">> --------");


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


        //        uiService.deleteProducts(169L, 802L);


        //        showDuplicityProductsInEshops();

        //        internalTxService.deleteNotInterestedProducts(
        //                97L,
        //                99L,
        //                5170L);

        //        uiService.deleteNewProducts(5505L);


        // --- PRODUCTS ---
        //        internalTxService.removeProductByUrl("https://www.brendon.sk/Products/Details/118425");
        //        deleteProductsFromNotInterested(EshopUuid.DR_MAX);

        println(facebookReporter.doFullReport())

        //        showProductNotInterested(EshopUuid.DROGERIA_VMD);


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

//        println("Errors:")
//        val errorFindFilterDto = ErrorFindFilterDto()
//        errorFindFilterDto.statusCodesToSkip = arrayOf("404")
//        errorFindFilterDto.limit = 50
//        internalTxService.findErrorsByFilter(errorFindFilterDto)
////                .forEach { println(it.customToString()) }
//                .forEach {
//                    println("${it.eshopUuid} - ${it.id} - ${it.updated}")
//                    println(it.message)
//                    println(it.additionalInfo)
//                    println("---")
//                }


        // --- UPDATE PRICE DATA ---
//        val listener: UpdateProductDataListener = object : UpdateProductDataListener { updateStatusInfo: UpdateStatusInfo ->
//            log.debug(">> eshop: {}, updated/waiting: {}/{}",
//                    updateStatusInfo.eshopUuid, updateStatusInfo.countOfProductsAlreadyUpdated, updateStatusInfo.countOfProductsWaitingToBeUpdated)
//        }

        prcoConsole.showInConsole()

//        searchKeywords()

//        updateProductDataManager.updateProductDataForEachProductInEachEshop(listener)
        //        updateProductDataManager.updateProductDataForEachProductNotInAnyGroup(listener);

//                updateProductDataManager.updateProductDataForEachProductInEshop(EshopUuid.FEEDO, listener)
        // updatne vsetky produkty v danej skupine
        //        updateProductDataManager.updateProductDataForEachProductInGroup(33L);

        //        updateProductDataManager.updateProductDataForEachProductNotInAnyGroup(listener);
        //        updateProductDataManager.updateProductData(3118L);
//                uiService.resetUpdateDateForAllProductsInEshop(EshopUuid.LEKAREN_V_KOCKE)
        //        uiService.updateProductCommonPrice(449L, BigDecimal.valueOf(0.59));

        // --- ADD NEW PRODUCTS ---
//        newProductManager.addNewProductsByConfiguredKeywordsForAllEshops()
//                newProductManager.addNewProductsByKeywordsForAllEshops(PAMPERS_ID, NUTRILON_ID, LOVELA_ID)
//                newProductManager.addNewProductsByKeywordForEshop(EshopUuid.FEEDO, PAMPERS_ID)
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


    private fun searchKeywords() {
        //id: 1
        internalTxService.createSearchKeyword(SearchKeywordCreateDto("pampers"))
        //id: 2
        internalTxService.createSearchKeyword(SearchKeywordCreateDto("nutrilon"))
        //id: 3
        internalTxService.createSearchKeyword(SearchKeywordCreateDto("lovela"))
    }


    private fun deleteProductsFromNotInterested(eshopUuid: EshopUuid) {
        internalTxService.deleteNotInterestedProducts(eshopUuid)
    }

    private fun showProductNotInterested(eshopUuid: EshopUuid) {
        val notInterestedProducts = internalTxService.findNotInterestedProducts(NotInterestedProductFindDto(eshopUuid))
        println("Not interested product for eshop: $eshopUuid")
        for (product in notInterestedProducts) {
            println("id " + product.id + ", "
                    + product.eshopUuid + " "
                    + " '" + product.name + "', "
                    + product.url)
        }
    }

    private fun showAllProductsInAllGroups(withPriceOnly: Boolean) {
        uiService.findGroups(GroupFilterDto())
                .forEach { group -> group.id?.let { showProductsInGroup(it, withPriceOnly) } }
    }

    private fun showProductInActionAll() {
        val products = uiService.findProducts(ProductFilterUIDto.withActionOnly())
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
        val existProductWithUrl = uiService.existProductWithUrl(productURL)
        println("product with URL $productURL existuje: $existProductWithUrl")
    }


    private fun showProductsInGroup(groupId: Long, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid) {
        val group = uiService.getGroupById(groupId)
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


}
