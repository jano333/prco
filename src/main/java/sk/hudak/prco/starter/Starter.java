package sk.hudak.prco.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.ErrorFindFilterDto;
import sk.hudak.prco.dto.GroupCreateDto;
import sk.hudak.prco.dto.GroupFilterDto;
import sk.hudak.prco.dto.GroupIdNameDto;
import sk.hudak.prco.dto.GroupListExtendedDto;
import sk.hudak.prco.dto.GroupProductKeywordsFullDto;
import sk.hudak.prco.dto.GroupUpdateDto;
import sk.hudak.prco.dto.NewProductInfoDetail;
import sk.hudak.prco.dto.NotInterestedProductFindDto;
import sk.hudak.prco.dto.NotInterestedProductFullDto;
import sk.hudak.prco.dto.UnitData;
import sk.hudak.prco.dto.product.ProductBestPriceInGroupDto;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductInActionDto;
import sk.hudak.prco.manager.AddingNewProductManager;
import sk.hudak.prco.manager.DbExportImportManager;
import sk.hudak.prco.manager.EshopThreadStatisticManager;
import sk.hudak.prco.manager.GroupProductResolver;
import sk.hudak.prco.manager.HtmlExportManager;
import sk.hudak.prco.manager.UpdateProductDataListener;
import sk.hudak.prco.manager.UpdateProductDataManager;
import sk.hudak.prco.manager.WatchDogManager;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.service.UIService;
import sk.hudak.prco.service.WatchDogService;
import sk.hudak.prco.ssl.PrcoSslManager;
import sk.hudak.prco.utils.CalculationUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;

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

    @Autowired
    private GroupProductResolver groupProductResolver;

    public void run() {

        //init ssl
        PrcoSslManager.INSTANCE.init();

        // start thred for showing statistics
        theadStatisticManager.startShowingStatistics();

        //TODO
        internalTxService.startErrorCleanUp();

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

//        System.out.println(ToStringBuilder.reflectionToString(uiService.getStatisticsOfProducts(), ToStringStyle.MULTI_LINE_STYLE));

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

        showProductsInGroupForFb(257L, true, EshopUuid.METRO);

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

        System.out.println("Errors:");
        ErrorFindFilterDto errorFindFilterDto = new ErrorFindFilterDto();
        errorFindFilterDto.setStatusCodesToSkip(new String[]{"404"});
        errorFindFilterDto.setLimit(50);
        internalTxService.findErrorsByFilter(errorFindFilterDto)
                .forEach(errorListDto -> System.out.println(errorListDto.customToString()));

        createGroupKeyWords();
        showGroupKeysWords();

        // --- UPDATE PRICE DATA ---
        UpdateProductDataListener listener = updateStatusInfo ->
                log.debug(">> eshop: {}, updated/waiting: {}/{}",
                        updateStatusInfo.getEshopUuid(), updateStatusInfo.getCountOfProductsAlreadyUpdated(), updateStatusInfo.getCountOfProductsWaitingToBeUpdated());

//        updateProductDataManager.updateProductDataForEachProductInEachEshop(listener);
//        updateProductDataManager.updateProductDataForEachProductNotInAnyGroup(listener);

//        updateProductDataManager.updateProductDataForEachProductInEshop(EshopUuid.LEKAREN_BELLA, listener);
        // updatne vsetky produkty v danej skupine
//        updateProductDataManager.updateProductDataForEachProductInGroup(33L);

//        updateProductDataManager.updateProductDataForEachProductNotInAnyGroup(listener);
//        updateProductDataManager.updateProductData(3118L);
//        uiService.resetUpdateDateForAllProductsInEshop(EshopUuid.TESCO);
//        uiService.updateProductCommonPrice(449L, BigDecimal.valueOf(0.59));

        // --- ADD NEW PRODUCTS ---
//        newProductManager.addNewProductsByKeywordsForAllEshops("pampers", "nutrilon", "lovela");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.PILULKA_24, "pampers");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.BAMBINO, "nutrilon");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.BAMBINO, "lovela");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.PERINBABA, "pampers 5");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.FEEDO, "pampers 4");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.ALZA, "pampers 5");
//        newProductManager.addNewProductsByKeywordForEshop(EshopUuid.ALZA, "nutrilon 4");
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

    private void createGroupKeyWords() {
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

    private void showGroupKeysWords() {
        Optional<GroupProductKeywordsFullDto> groupProductKeywordsByGroupId
                = internalTxService.getGroupProductKeywordsByGroupId(449L);
        System.out.println(groupProductKeywordsByGroupId.get());
        groupProductKeywordsByGroupId = internalTxService.getGroupProductKeywordsByGroupId(450L);
        System.out.println(groupProductKeywordsByGroupId.get());
//        groupProductKeywordsByGroupId = internalTxService.getGroupProductKeywordsByGroupId(451L);
//        System.out.println(groupProductKeywordsByGroupId.get());
//        groupProductKeywordsByGroupId = internalTxService.getGroupProductKeywordsByGroupId(452L);
//        System.out.println(groupProductKeywordsByGroupId.get());


    }

    private void showDuplicityProductsInEshops() {

//        internalTxService.removeProduct(2659L);

//        EshopUuid eshopUuid = EshopUuid.FEEDO;
        for (EshopUuid eshopUuid : EshopUuid.values()) {
            System.out.println("Duplicity for eshop: " + eshopUuid);
            List<ProductFullDto> result = internalTxService.findDuplicityProductsByNameAndPriceInEshop(eshopUuid);
            for (ProductFullDto productFullDto : result) {
                System.out.println(productFullDto.getId() + ", "
                        + productFullDto.getName() + " "
                        + productFullDto.getPriceForPackage() + " "
                        + productFullDto.getUrl() + " "
                        + formatDate(productFullDto.getCreated()));
            }
            System.out.println();
        }
    }

    private void deleteProductsFromNotInterested(EshopUuid eshopUuid) {
        internalTxService.deleteNotInterestedProducts(eshopUuid);
    }

    private void showProductNotInterested(EshopUuid eshopUuid) {
        List<NotInterestedProductFullDto> notInterestedProducts = internalTxService.findNotInterestedProducts(new NotInterestedProductFindDto(eshopUuid));
        System.out.println("Not interested product for eshop: " + eshopUuid);
        for (NotInterestedProductFullDto product : notInterestedProducts) {
            System.out.println("id " + product.getId() + ", "
                    + product.getEshopUuid() + " "
                    + " '" + product.getName() + "', "
                    + product.getUrl());
        }
    }

    private void showAllProductsInAllGroups(boolean withPriceOnly) {
        uiService.findGroups(new GroupFilterDto())
                .forEach(group -> showProductsInGroup(group.getId(), withPriceOnly));
    }

    private void showProductInActionAll() {
        List<ProductFullDto> products = uiService.findProducts(new ProductFilterUIDto(Boolean.TRUE));
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
        System.out.println();
        System.out.println("Produkty v akcii pre eshop " + eshopUuid + ":");

        for (ProductInActionDto product : uiService.findProductsInAction(eshopUuid)) {
            System.out.println("id " + product.getId() + ", "
                    + formatPrice(product.getPriceForPackage()) + "/"
                    + formatPrice(product.getCommonPrice()) + " "
                    + formatPercentage(product.getPriceForPackage(), product.getCommonPrice())
                    + " '" + product.getName() + "', "
                    + formatPrice(product.getPriceForOneItemInPackage(), 2) + " " +
                    "(" + formatValidTo(product.getActionValidTo()) + ") "
                    + product.getBestPriceInGroup() + " "
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

        return String.valueOf(CalculationUtils.INSTANCE.calculatePercetage(priceForPackage, commonPrice)) + "%";

    }

    private String formatValidTo(Date date) {
        if (date == null) {
            return "-";
        }
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    private void showProductsInEshop(EshopUuid eshopUuid) {
        System.out.println("Eshop " + eshopUuid);

        List<ProductFullDto> products = uiService.findProducts(new ProductFilterUIDto(eshopUuid));
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

    private String formatPriceFb(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "-";
        }


        return new DecimalFormat("00.00").format(bigDecimal).replace(".", ",");
    }

    private void existProduct(String productURL) {
        boolean existProductWithUrl = uiService.existProductWithUrl(productURL);
        System.out.println("product with URL " + productURL + " existuje: " + existProductWithUrl);
    }

    private void showProductsNotInAnyGroup() {
        System.out.println();
        System.out.println("Products not in any group:");

        List<ProductFullDto> products = uiService.findProductsWitchAreNotInAnyGroup();
        for (ProductFullDto product : products) {
            System.out.println("eshop " + product.getEshopUuid() + ", " +
                    "id " + product.getId() + ", " +
                    "name: " + product.getName() + ", " +
                    "url: " + product.getUrl() + ", " +
                    "unit: " + product.getUnitValue() + " " + product.getUnit() + " count: " + product.getUnitPackageCount());

        }
        if (products.isEmpty()) {
            System.out.println("ziadny");
        }
    }

    private void showProductsInGroup(long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip) {
        GroupIdNameDto group = uiService.getGroupById(groupId);
        List<ProductFullDto> productsInGroup = uiService.findProductsInGroup(groupId, withPriceOnly, eshopsToSkip);

        // vypis
        System.out.println();
        System.out.println("'" + group.getName() + "' id " + groupId + " count " + productsInGroup.size() + " withPriceOnly " + withPriceOnly);
        if (!asList(eshopsToSkip).isEmpty()) {
            System.out.println("Preskakujem eshopy: " + asList(eshopsToSkip));
        }
        for (ProductFullDto product : productsInGroup) {
            System.out.println("eshop: " + product.getEshopUuid().name() +
                    " price for unit " + product.getPriceForUnit() +
                    ", price for one item " + formatPrice(product.getPriceForOneItemInPackage()) +
                    ", id " + product.getId() +
                    ", '" + product.getName() +
                    "', " + product.getUrl() +
                    ", last updated " + formatDate(product.getLastTimeDataUpdated()));
        }
    }

    private void showProductsInGroupForFb(long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip) {
        System.out.println("For FB(top 5): ");
        GroupIdNameDto group = uiService.getGroupById(groupId);
        List<ProductFullDto> productsInGroup = uiService.findProductsInGroup(groupId, withPriceOnly, eshopsToSkip);

        StringBuilder sb = new StringBuilder();
        sb.append(group.getName()).append("(").append(productsInGroup.size()).append(" produktov):");
        sb.append("\n");
        for (int i = 0; i < 5; i++) {
            ProductFullDto product = productsInGroup.get(i);
            sb.append(i + 1).append(". ");
            sb.append(product.getPriceForUnit()).append("€/kus ");
            sb.append(formatPriceFb(product.getPriceForOneItemInPackage())).append("€/balenie ");
            sb.append(product.getUrl());
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
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

        NewProductInfoDetail firstInvalidNewProductInfo = internalTxService.findFirstInvalidNewProduct();
        if (firstInvalidNewProductInfo != null) {
            System.out.println("je to null");
            return;
        }
        System.out.println(firstInvalidNewProductInfo);

        //TODO z GUI vyplnit spravne data
        internalTxService.repairInvalidUnitForNewProduct(
                1185L,
                new UnitData(Unit.KUS, new BigDecimal("1"), Integer.valueOf(1)));

    }

    public void UC_markAsNotInterested(Long newProductIds) {
        internalTxService.markNewProductAsNotInterested(newProductIds);
    }

}
