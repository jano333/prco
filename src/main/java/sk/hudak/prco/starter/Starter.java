package sk.hudak.prco.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.UnitData;
import sk.hudak.prco.dto.group.GroupCreateDto;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.group.GroupListDto;
import sk.hudak.prco.dto.group.GroupListExtendedDto;
import sk.hudak.prco.dto.group.GroupUpdateDto;
import sk.hudak.prco.dto.newproduct.NewProductInfoDetail;
import sk.hudak.prco.dto.product.ProductBestPriceInGroupDto;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductInActionDto;
import sk.hudak.prco.manager.AddingNewProductManager;
import sk.hudak.prco.manager.DbExportImportManager;
import sk.hudak.prco.manager.EshopThreadStatisticManager;
import sk.hudak.prco.manager.HtmlExportManager;
import sk.hudak.prco.manager.UpdateProductDataManager;
import sk.hudak.prco.manager.UpdateProductInfoListener;
import sk.hudak.prco.manager.UpdateStatusInfo;
import sk.hudak.prco.manager.WatchDogManager;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.service.UIService;
import sk.hudak.prco.service.WatchDogService;
import sk.hudak.prco.utils.CalculationUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by jan.hudak on 9/29/2017.
 */
@Component
@Slf4j
public class Starter {

    @Autowired
    private InternalTxService internalTxService;

    @Autowired
    private AddingNewProductManager newProductManager;

    @Autowired
    private UpdateProductDataManager updateProductDataManager;

    @Autowired
    private DbExportImportManager dbExportImportManager;

    @Autowired
    private HtmlExportManager htmlExportManager;

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private UIService uiService;

    @Autowired
    private WatchDogService watchDogService;

    @Autowired
    private WatchDogManager watchDogManager;

    @Autowired
    private EshopThreadStatisticManager theadStatisticManager;

    public void run() {

        theadStatisticManager.startShowingStatistics();

        // --- WATCH DOG SERVICE ---
//        watchDogManager.startWatching("https://www.obi.sk/zahradne-hadice/cmi-zahradna-hadica-12-5-mm-1-2-20-m-zelena/p/2235422",
//                BigDecimal.valueOf(4.99));

//        watchDogManager.collectAllUpdateAndSendEmail();


        // --- GROUPY --
//        createNewGroup("olej");
//        updateGroupName(1L, "pampers 4 zelene");
//        uiService.addProductsToGroup(1L,  769L);
//        uiService.addProductsToGroup(225L,  393L);
//        uiService.addProductsToGroup(225L,  391L);
//        uiService.addProductsToGroup(225L,  395L);
//        uiService.addProductsToGroup(225L,  386L);
//        uiService.addProductsToGroup(225L,  387L);
//        uiService.addProductsToGroup(225L,  388L);
//        uiService.addProductsToGroup(225L,  390L);
        showAllGroups();

        // --- PRODUCTS ---
//        showAllProducts();
        // pampers
        showProductsInGroup(1L);
        // nutrilon
//        showProductsInGroup(33L);
        // olej
//        showProductsInGroup(225L);

//        showAllProductsInAllGroups();
//        showProductsNotInAnyGroup();


//        existProduct("https://www.feedo.sk/pampers-active-baby-4-box-120ks-9-16-kg-jednorazove-plienky/");
//        uiService.deleteProducts(129L, 134L);
//        showProductsInEshop(EshopUuid.TESCO);
//        showProductsInEshop(EshopUuid.METRO);
//        showProductsInEshopInAction(EshopUuid.TESCO);
//        showProductsInEshopInAction(EshopUuid.METRO);
//        showProductInActionAll();

//        showProductsInEshopWithBestPriceInGroupOnly(EshopUuid.TESCO);
//        showProductsInEshopWithBestPriceInGroupOnly(EshopUuid.METRO);


//        watchDogService.notifyByEmail(Collections.emptyList());


        // --- UPDATE PRICE DATA ---
        UpdateProductInfoListener listener = new UpdateProductInfoListener() {
            @Override
            public void updateStatusInfo(UpdateStatusInfo updateStatusInfo) {
                log.debug(">> eshop: {}, updated/waiting: {}/{}", updateStatusInfo.getEshopUuid(),
                        updateStatusInfo.getCountOfProductsAlreadyUpdated(), updateStatusInfo.getCountOfProductsWaitingToBeUpdated());
            }
        };
        updateProductDataManager.updateAllProductsDataForAllEshops(listener);
//        updateProductDataManager.updateAllProductsDataForEshop(EshopUuid.TESCO, listener);
//        updateProductDataManager.updateAllProductsDataForEshop(EshopUuid.BAMBINO, listener);
//        updateProductDataManager.updateAllProductsDataForEshop(EshopUuid.METRO, listener);
        // updatne vsetky produkty v danej skupine
//        updateProductDataManager.updateAllProductsDataInGroup(33L);
//        updateProductDataManager.updateProductData(103L);
//        uiService.resetUpdateDateForAllProductsInEshop(EshopUuid.TESCO);
//        uiService.updateCommonPrice(449L, BigDecimal.valueOf(0.59));
//        uiService.updateCommonPrice(356L, BigDecimal.valueOf(0.99));
//        uiService.updateCommonPrice(455L, BigDecimal.valueOf(0.77));
//        uiService.updateCommonPrice(386L, BigDecimal.valueOf(3.89));
//        uiService.updateCommonPrice(387L, BigDecimal.valueOf(3.89));
//        uiService.updateCommonPrice(390L, BigDecimal.valueOf(3.89));
//        uiService.updateCommonPrice(419L, BigDecimal.valueOf(0.79));
//        uiService.updateCommonPrice(418L, BigDecimal.valueOf(0.79));
//        uiService.updateCommonPrice(545L, BigDecimal.valueOf(15.59));
//        uiService.updateCommonPrice(385L, BigDecimal.valueOf(1.99));

        // --- ADD NEW PRODUCTS ---
//        newProductManager.addNewProductsByKeywordForAllEshops("pampers 4");
//        newProductManager.addNewProductsByKeywordForAllEshops("nutrilon 4");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.METRO, "pampers 4");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.TESCO, "pampers 4");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.MALL, "pampers 4");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.BAMBINO, "pampers 4");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.PILULKA, "pampers 4");
//        newProductManager.addNewProductsByUrl(
//                "https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002120575818",
//                "https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002120307521",
//        "https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002014050505"
//       );


        // --- EXPORT ----

//*********************************************************
//        UC_fixOneInvalidNewProduct();
        // ---
//        internalTxService.confirmUnitDataForNewProducts(36L, 38L);
        // ---
//        long l = internalTxService.fixAutomaticalyProductUnitData(5);
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
//                1185L,
//                new UnitData(Unit.KUS, new BigDecimal("1"), Integer.valueOf(1)));

    }

    private void showAllProductsInAllGroups() {
        List<GroupListDto> groups = uiService.findGroups(new GroupFilterDto());
        groups.forEach(group -> showProductsInGroup(group.getId()));
    }

    private void showProductInActionAll() {
        ProductFilterUIDto filter = new ProductFilterUIDto();
        filter.setOnlyInAction(Boolean.TRUE);
        List<ProductFullDto> products = uiService.findProducts(filter);
        System.out.println("Produkty v akcii:");
        for (ProductFullDto product : products) {
            System.out.println("id " + product.getId() + ", "
                    + product.getEshopUuid() + " "
                    + formatPrice(product.getPriceForPackage()) +
                    "/" + formatPrice(product.getCommonPrice()) +
                    " (" + formatValidTo(product.getActionValidTo()) + ") "
                    + " '" + product.getName() + "', "
                    + product.getUrl());
        }
    }

    private void showProductsInEshopInAction(EshopUuid eshopUuid) {
        List<ProductInActionDto> products = uiService.findProductsInAction(eshopUuid);

        System.out.println();
        System.out.println("Produkty v akcii pre eshop " + eshopUuid + ":");
        for (ProductInActionDto product : products) {
            System.out.println("id " + product.getId() + ", "
                    + formatPrice(product.getPriceForOneItemInPackage(), 2) + " "
                    + formatPrice(product.getPriceForPackage()) + "/"
                    + formatPrice(product.getCommonPrice()) + " "
                    + formatPercentage(product.getPriceForPackage(), product.getCommonPrice()) +
                    "(" + formatValidTo(product.getActionValidTo()) + ") "
                    + product.getBestPriceInGroup()
                    + " '" + product.getName() + "', "
                    + product.getUrl());
        }
    }

    private void showProductsInEshopWithBestPriceInGroupOnly(EshopUuid eshopUuid) {
        System.out.println();
        System.out.println("Produkty v akcii a najlepsou cenou v groupe pre eshop " + eshopUuid + ":");

        List<ProductBestPriceInGroupDto> products = uiService.findProductsBestPriceInGroupDto(eshopUuid);
        for (ProductBestPriceInGroupDto product : products) {
            System.out.println("id " + product.getId() + ", "
                    + formatPrice(product.getPriceForUnit(), 2) + " "
                    + formatPrice(product.getPriceForOneItemInPackage(), 2) + " "
                    + formatPrice(product.getPriceForPackage()) + "/"
                    + formatPrice(product.getCommonPrice()) + " "
                    + formatPercentage(product.getPriceForPackage(), product.getCommonPrice()) +
                    "(" + formatValidTo(product.getActionValidTo()) + ") "
                    + " '" + product.getName() + "', "
                    + product.getUrl());
        }

    }

    private String formatPercentage(BigDecimal priceForPackage, BigDecimal commonPrice) {
        if (priceForPackage == null || commonPrice == null) {
            return "";
        }

        return String.valueOf(CalculationUtils.calculatePercetage(priceForPackage, commonPrice)) + "%";

    }

    private String formatValidTo(Date date) {
        if (date == null) {
            return "-";
        }
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    private void showProductsInEshop(EshopUuid eshopUuid) {
        System.out.println("Eshop " + eshopUuid);
        ProductFilterUIDto filter = new ProductFilterUIDto();
        filter.setEshopUuid(eshopUuid);
        List<ProductFullDto> products = uiService.findProducts(filter);
        for (ProductFullDto product : products) {
            System.out.println("id " + product.getId() + ", "
                    + formatPrice(product.getPriceForPackage()) + "(" + formatValidTo(product.getActionValidTo()) + ") " + formatPrice(product.getCommonPrice())
                    + " '" + product.getName() + "', "
                    + product.getGroupList() + "  "
                    + product.getUrl());
        }
    }

    private String formatPrice(BigDecimal bigDecimal, int countOfDecimal) {
        if (bigDecimal == null) {
            return "-";
        }
        bigDecimal = bigDecimal.setScale(countOfDecimal, RoundingMode.HALF_UP);
        return formatPrice(bigDecimal);
    }

    private String formatPrice(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "-";
        }
        return bigDecimal.toString().replaceAll("0+$", "");

    }

    private void existProduct(String productURL) {
        boolean existProductWithUrl = uiService.existProductWithUrl(productURL);
        System.out.println("product with URL " + productURL + " existuje: " + existProductWithUrl);
    }

    private void showProductsNotInAnyGroup() {
        System.out.println("Product not in any group:");

        List<ProductFullDto> products = uiService.findProductsWitchAreNotInAnyGroup();
        for (ProductFullDto product : products) {
            System.out.println("id " + product.getId() + ", " +
                    "name: " + product.getName() + ", " +
                    "url: " + product.getUrl());
        }
        if (products.isEmpty()) {
            System.out.println("ziadny");
        }
    }

    private void showProductsInGroup(long groupId, EshopUuid... eshopsToSkip) {
        GroupIdNameDto group = uiService.getGroupById(groupId);
        List<ProductFullDto> productsInGroup = uiService.findProductsInGroup(groupId, eshopsToSkip);

        // vypis
        System.out.println();
        System.out.println("'" + group.getName() + "' id " + groupId + " count " + productsInGroup.size());
        for (ProductFullDto product : productsInGroup) {
            System.out.println("eshop: " + product.getEshopUuid().name() +
                    " price for unit " + product.getPriceForUnit() +
                    ", price for one item " + formatPrice(product.getPriceForOneItemInPackage()) +
                    ", id " + product.getId() +
                    ", '" + product.getName() +
                    "', " + product.getUrl());
        }
    }

    private void showAllProducts() {
        List<ProductFullDto> products = uiService.findProducts(new ProductFilterUIDto());
        for (ProductFullDto product : products) {
            System.out.println("id " + product.getId() + ", name " + product.getName() + ", url " + product.getUrl());
        }
    }

    private void showAllGroups() {
        System.out.println("Zoznam groups:");
        List<GroupListExtendedDto> groups = uiService.findAllGroupExtended();
        for (GroupListExtendedDto group : groups) {
            System.out.print(group.getId() + "/" + group.getName() + "(" + group.getCountOfProduct() + ") ");
            Map<EshopUuid, Long> countOfProductInEshop = group.getCountOfProductInEshop();
            if (!countOfProductInEshop.isEmpty()) {
                for (EshopUuid eshopUuid : countOfProductInEshop.keySet()) {
                    System.out.print(eshopUuid + "/" + countOfProductInEshop.get(eshopUuid) + ", ");
                }
            }
            System.out.println();
        }
        System.out.println("---");
    }


    private void updateGroupName(long id, String name) {
        uiService.updateGroup(new GroupUpdateDto(id, name));
    }

    private void createNewGroup(String groupName) {
        uiService.createGroup(new GroupCreateDto(groupName));
    }

    public void UC_fixOneInvalidNewProduct() {
        System.out.println("pocet neplatnych: " + internalTxService.getCountOfInvalidNewProduct());

        Optional<NewProductInfoDetail> firstInvalidNewProductInfo = internalTxService.findFirstInvalidNewProduct();
        if (!firstInvalidNewProductInfo.isPresent()) {
            System.out.println("je to null");
            return;
        }
        System.out.println(firstInvalidNewProductInfo.get());

        //TODO z GUI vyplnit spravne data
        internalTxService.repairInvalidUnitForNewProduct(
                1185L,
                new UnitData(Unit.KUS, new BigDecimal("1"), Integer.valueOf(1)));

    }

    public void UC_markAsNotInterested(Long newProductIds) {
        internalTxService.markNewProductAsNotInterested(newProductIds);
    }

}
