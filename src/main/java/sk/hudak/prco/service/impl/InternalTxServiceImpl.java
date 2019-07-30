package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductStatisticInfoDto;
import sk.hudak.prco.dto.ProductUpdateDataDto;
import sk.hudak.prco.dto.UnitData;
import sk.hudak.prco.dto.WatchDogAddDto;
import sk.hudak.prco.dto.WatchDogDto;
import sk.hudak.prco.dto.WatchDogNotifyUpdateDto;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.error.ErrorFindFilterDto;
import sk.hudak.prco.dto.error.ErrorListDto;
import sk.hudak.prco.dto.group.GroupCreateDto;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.group.GroupListDto;
import sk.hudak.prco.dto.group.GroupListExtendedDto;
import sk.hudak.prco.dto.group.GroupProductKeywordsCreateDto;
import sk.hudak.prco.dto.group.GroupProductKeywordsFullDto;
import sk.hudak.prco.dto.group.GroupUpdateDto;
import sk.hudak.prco.dto.internal.StatisticForUpdateForEshopDto;
import sk.hudak.prco.dto.newproduct.NewProductCreateDto;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.newproduct.NewProductInfoDetail;
import sk.hudak.prco.dto.notinteretedproduct.NotInterestedProductFindDto;
import sk.hudak.prco.dto.notinteretedproduct.NotInterestedProductFullDto;
import sk.hudak.prco.dto.product.ProductAddingToGroupDto;
import sk.hudak.prco.dto.product.ProductBestPriceInGroupDto;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductInActionDto;
import sk.hudak.prco.dto.product.ProductUnitDataDto;
import sk.hudak.prco.service.ErrorService;
import sk.hudak.prco.service.GroupProductKeywordsService;
import sk.hudak.prco.service.GroupService;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.service.NewProductService;
import sk.hudak.prco.service.NotInterestedProductService;
import sk.hudak.prco.service.ProductCommonService;
import sk.hudak.prco.service.ProductService;
import sk.hudak.prco.service.WatchDogService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Transaction wrapper for internal services.
 */
@Slf4j
@Service
public class InternalTxServiceImpl implements InternalTxService {

    private final NewProductService newProductService;
    private final ProductCommonService productCommonService;
    private final ProductService productService;
    private final NotInterestedProductService notInterestedProductService;
    private final GroupService groupService;
    private final WatchDogService watchDogService;
    private final ErrorService errorService;
    private final GroupProductKeywordsService groupProductKeywordsService;

    public InternalTxServiceImpl(@Qualifier("newProductService") NewProductService newProductService,
                                 @Qualifier("productCommonService") ProductCommonService productCommonService,
                                 @Qualifier("productService") ProductService productService,
                                 @Qualifier("notInterestedProductService") NotInterestedProductService notInterestedProductService,
                                 @Qualifier("groupService") GroupService groupService,
                                 @Qualifier("watchDogService") WatchDogService watchDogService,
                                 @Qualifier("errorService") ErrorService errorService,
                                 @Qualifier("groupProductKeywordsService") GroupProductKeywordsService groupProductKeywordsService) {

        this.newProductService = newProductService;
        this.productCommonService = productCommonService;
        this.productService = productService;
        this.notInterestedProductService = notInterestedProductService;
        this.groupService = groupService;
        this.watchDogService = watchDogService;
        this.errorService = errorService;
        this.groupProductKeywordsService = groupProductKeywordsService;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existProductWithURL(String productURL) {
        return productCommonService.existProductWithURL(productURL);
    }

    @Override
    @Transactional
    public long createNewProduct(NewProductCreateDto newProductCreateDto) {
        return newProductService.createNewProduct(newProductCreateDto);
    }

    @Override
    @Transactional(readOnly = true)
    public NewProductFullDto getNewProduct(long newProductId) {
        return newProductService.getNewProduct(newProductId);
    }

    @Override
    @Transactional(readOnly = true)
    public NewProductInfoDetail findFirstInvalidNewProduct() {
        return newProductService.findFirstInvalidNewProduct();
    }

    @Override
    @Transactional(readOnly = true)
    public long getCountOfInvalidNewProduct() {
        return newProductService.getCountOfInvalidNewProduct();
    }

    @Override
    @Transactional
    public void repairInvalidUnitForNewProduct(Long newProductId,
                                               UnitData correctUnitData) {
        newProductService.repairInvalidUnitForNewProduct(newProductId, correctUnitData);
    }

    @Override
    @Transactional
    public void reprocessProductData(Long newProductId) {
        newProductService.reprocessProductData(newProductId);
    }

    @Override
    @Transactional
    public void confirmUnitDataForNewProducts(Long... newProductIds) {
        newProductService.confirmUnitDataForNewProducts(newProductIds);
    }

    @Override
    @Transactional
    public long fixAutomaticallyProductUnitData(int maxCountOfInvalid) {
        return newProductService.fixAutomaticallyProductUnitData(maxCountOfInvalid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewProductFullDto> findNewProducts(NewProductFilterUIDto filter) {
        return newProductService.findNewProducts(filter);
    }

    @Override
    @Transactional
    public void markNewProductAsInterested(Long... newProductIds) {
        productCommonService.markNewProductAsInterested(newProductIds);
    }

    @Override
    @Transactional
    public void markNewProductAsNotInterested(Long... newProductIds) {
        productCommonService.markNewProductAsNotInterested(newProductIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDetailInfo> getProductForUpdate(EshopUuid eshopUuid, int olderThanInHours) {
        return productService.getProductForUpdate(eshopUuid, olderThanInHours);
    }

    @Override
    @Transactional
    public void updateProduct(ProductUpdateDataDto productUpdateDataDto) {
        productService.updateProduct(productUpdateDataDto);
    }

    @Override
    @Transactional
    public void updateProductUnitData(ProductUnitDataDto productUnitDataDto) {
        newProductService.updateProductUnitData(productUnitDataDto);
    }

    @Override
    @Transactional
    public void deleteNewProducts(Long... newProductIds) {
        newProductService.deleteNewProducts(newProductIds);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCountOfAllNewProducts() {
        return newProductService.getCountOfAllNewProducts();
    }

    @Override
    @Transactional
    public void deleteNotInterestedProducts(EshopUuid eshopUuid) {
        notInterestedProductService.deleteNotInterestedProducts(eshopUuid);
    }

    @Override
    @Transactional
    public void markProductAsUnavailable(Long productId) {
        productService.markProductAsUnavailable(productId);
    }

    @Override
    @Transactional
    public void resetUpdateDateProduct(Long productId) {
        productService.resetUpdateDateProduct(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid) {
        return productService.findProductsBestPriceInGroupDto(eshopUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticForUpdateForEshopDto getStatisticForUpdateForEshop(EshopUuid eshopUuid, int olderThanInHours) {
        return productService.getStatisticForUpdateForEshop(eshopUuid, olderThanInHours);
    }

    @Override
    @Transactional
    public void markProductAsNotInterested(Long productId) {
        productService.markProductAsNotInterested(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewProductFullDto> findNewProductsForExport() {
        return newProductService.findNewProductsForExport();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFullDto> findProductsForExport() {
        return productService.findProductsForExport();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFullDto> findProducts(ProductFilterUIDto filter) {
        return productService.findProducts(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInActionDto> findProductsInAction(EshopUuid eshopUuid) {
        return productService.findProductsInAction(eshopUuid);
    }

    @Override
    @Transactional

    public void removeProduct(Long productId) {
        productService.removeProduct(productId);
    }

    @Override
    @Transactional
    public void removeProductByUrl(String productUrl) {
        productService.removeProductByUrl(productUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFullDto> findProductsInGroup(Long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip) {
        return productService.findProductsInGroup(groupId, withPriceOnly, eshopsToSkip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFullDto> findProductsNotInAnyGroup() {
        return productService.findProductsNotInAnyGroup();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAddingToGroupDto getProduct(Long productId) {
        return productService.getProduct(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existProductWithUrl(String productURL) {
        return productService.existProductWithUrl(productURL);
    }

    @Override
    @Transactional
    public void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid) {
        productService.resetUpdateDateForAllProductsInEshop(eshopUuid);
    }

    @Override
    @Transactional
    public void updateProductCommonPrice(Long productId, BigDecimal newCommonPrice) {
        productService.updateProductCommonPrice(productId, newCommonPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public EshopUuid getEshopForProductId(Long productId) {
        return productService.getEshopForProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailInfo getProductForUpdate(Long productId) {
        return productService.getProductForUpdate(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotInterestedProductFullDto> findNotInterestedProducts(NotInterestedProductFindDto findDto) {
        return productCommonService.findNotInterestedProducts(findDto);
    }

    @Override
    @Transactional
    public long importNewProducts(List<NewProductFullDto> newProductList) {
        return productCommonService.importNewProducts(newProductList);
    }

    @Override
    @Transactional
    public long importProducts(List<ProductFullDto> productList) {
        return productCommonService.importProducts(productList);
    }

    @Override
    @Transactional
    public long importNotInterestedProducts(List<NotInterestedProductFullDto> notInterestedProductList) {
        return productCommonService.importNotInterestedProducts(notInterestedProductList);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStatisticInfoDto getStatisticsOfProducts() {
        return productCommonService.getStatisticsOfProducts();
    }

    // group

    @Override
    @Transactional
    public Long createGroup(GroupCreateDto createDto) {
        return groupService.createGroup(createDto);
    }

    @Override
    @Transactional
    public void updateGroup(GroupUpdateDto updateDto) {
        groupService.updateGroup(updateDto);
    }

    @Override
    @Transactional
    public void addProductsToGroup(Long groupId, Long... productId) {
        groupService.addProductsToGroup(groupId, productId);
    }

    @Override
    @Transactional
    public void removeProductsFromGroup(Long groupId, Long... productIds) {
        groupService.removeProductsFromGroup(groupId, productIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupListDto> getGroupsWithoutProduct(Long productId) {
        return groupService.getGroupsWithoutProduct(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupListDto> findGroups(GroupFilterDto groupFilterDto) {
        return groupService.findGroups(groupFilterDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupListExtendedDto> findAllGroupExtended() {
        return groupService.findAllGroupExtended();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupIdNameDto getGroupById(Long groupId) {
        return groupService.getGroupById(groupId);
    }

    @Override
    @Transactional
    public Long addNewProductToWatch(WatchDogAddDto addDto) {
        return watchDogService.addNewProductToWatch(addDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<EshopUuid, List<WatchDogDto>> findProductsForWatchDog() {
        return watchDogService.findProductsForWatchDog();
    }

    @Override
    public void notifyByEmail(List<WatchDogNotifyUpdateDto> toBeNotified) {
        // nemusi bezat v tranzakcii
        watchDogService.notifyByEmail(toBeNotified);
    }


    @Override
    @Transactional
    public Long createError(ErrorCreateDto createDto) {
        return errorService.createError(createDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorListDto> findAll() {
        return errorService.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorListDto> findErrorByMaxCount(int limit, ErrorType errorType) {
        return errorService.findErrorByMaxCount(limit, errorType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorListDto> findErrorsByFilter(ErrorFindFilterDto findDto) {
        return errorService.findErrorsByFilter(findDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorListDto> findErrorsByTypes(ErrorType... errorTypes) {
        return errorService.findErrorsByTypes(errorTypes);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<ErrorType, Long> getStatisticForErrors() {
        return errorService.getStatisticForErrors();
    }

    @Override
    @Transactional
    public Future<Void> startErrorCleanUp() {
        return errorService.startErrorCleanUp();
    }

    @Override
    @Transactional
    public void deleteNotInterestedProducts(Long... notInterestedProductIds) {
        notInterestedProductService.deleteNotInterestedProducts(notInterestedProductIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFullDto> findDuplicityProductsByNameAndPriceInEshop(EshopUuid eshopUuid) {
        return productService.findDuplicityProductsByNameAndPriceInEshop(eshopUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> getProductWithUrl(String productUrl, Long productIdToIgnore) {
        return productService.getProductWithUrl(productUrl, productIdToIgnore);
    }

    @Override
    @Transactional
    public Long createGroupProductKeywords(GroupProductKeywordsCreateDto groupProductKeywordsCreateDto) {
        return groupProductKeywordsService.createGroupProductKeywords(groupProductKeywordsCreateDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GroupProductKeywordsFullDto> getGroupProductKeywordsByGroupId(Long groupId) {
        return groupProductKeywordsService.getGroupProductKeywordsByGroupId(groupId);
    }

    @Override
    @Transactional
    public void removeAllKeywordForGroupId(Long groupId) {
        groupProductKeywordsService.removeAllKeywordForGroupId(groupId);
    }

    // tests

    @Override
    @Transactional
    public void test() {
        throw new UnsupportedOperationException();
    }
}
