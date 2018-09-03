package sk.hudak.prco.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductStatisticInfoDto;
import sk.hudak.prco.dto.group.GroupCreateDto;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.group.GroupListDto;
import sk.hudak.prco.dto.group.GroupListExtendedDto;
import sk.hudak.prco.dto.group.GroupUpdateDto;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.product.ProductBestPriceInGroupDto;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductInActionDto;
import sk.hudak.prco.dto.product.ProductUnitDataDto;
import sk.hudak.prco.service.InternalTxService;
import sk.hudak.prco.service.UIService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Delegator pattern.
 */
@Component
public class UIServiceImpl implements UIService {

    @Autowired
    private InternalTxService internalTxService;

    @Override
    public List<NewProductFullDto> findNewProducts(NewProductFilterUIDto filter) {
        return internalTxService.findNewProducts(filter);
    }

    @Override
    public void confirmUnitDataForNewProduct(Long newProductId) {
        internalTxService.confirmUnitDataForNewProducts(newProductId);
    }

    @Override
    public void markNewProductAsInterested(Long newProductId) {
        internalTxService.markNewProductAsInterested(newProductId);
    }

    @Override
    public void markNewProductAsNotInterested(Long newProductId) {
        internalTxService.markNewProductAsNotInterested(newProductId);
    }

    @Override
    public void updateProductUnitData(ProductUnitDataDto productUnitDataDto) {
        internalTxService.updateProductUnitData(productUnitDataDto);
    }

    @Override
    public void updateCommonPrice(Long productId, BigDecimal newCommonPrice) {
        internalTxService.updateCommonPrice(productId, newCommonPrice);
    }

    @Override
    public void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid) {
        internalTxService.resetUpdateDateForAllProductsInEshop(eshopUuid);
    }

    @Override
    public List<ProductFullDto> findProducts(ProductFilterUIDto filter) {
        return internalTxService.findProducts(filter);
    }

    @Override
    public void removeProduct(Long productId) {
        internalTxService.removeProduct(productId);
    }

    @Override
    public List<ProductFullDto> findProductsInGroup(Long groupId, EshopUuid... eshopsToSkip) {
        return internalTxService.findProductsInGroup(groupId, eshopsToSkip);
    }

    @Override
    public List<ProductFullDto> findProductsWitchAreNotInAnyGroup() {
        return internalTxService.findProductsNotInAnyGroup();
    }

    @Override
    public void tryToRepairInvalidUnitForNewProductByReprocessing(Long newProductId) {
        internalTxService.repairInvalidUnitForNewProductByReprocessing(newProductId);
    }

    @Override
    public Long createGroup(GroupCreateDto groupCreateDto) {
        return internalTxService.createGroup(groupCreateDto);
    }

    @Override
    public void updateGroup(GroupUpdateDto groupUpdateDto) {
        internalTxService.updateGroup(groupUpdateDto);
    }

    @Override
    public GroupIdNameDto getGroupById(Long groupId) {
        return internalTxService.getGroupById(groupId);
    }

    @Override
    public List<GroupListDto> findGroups(GroupFilterDto groupFilterDto) {
        return internalTxService.findGroups(groupFilterDto);
    }

    @Override
    public void addProductsToGroup(Long groupId, Long... productIds) {
        internalTxService.addProductsToGroup(groupId, productIds);
    }

    @Override
    public void removeProductsFromGroup(Long groupId, Long... productIds) {
        internalTxService.removeProductsFromGroup(groupId, productIds);
    }

    @Override
    public List<GroupListDto> getGroupsWithoutProduct(Long productId) {
        return internalTxService.getGroupsWithoutProduct(productId);
    }

    @Override
    public List<GroupListExtendedDto> findAllGroupExtended() {
        return internalTxService.findAllGroupExtended();
    }

    @Override
    public ProductStatisticInfoDto getStatisticsOfProducts() {
        return internalTxService.getStatisticsOfProducts();
    }

    @Override
    public boolean existProductWithUrl(String productURL) {
        return internalTxService.existProductWithUrl(productURL);
    }

    @Override
    public void deleteProducts(Long... productIds) {
        if (productIds == null) {
            return;
        }
        for (Long productId : productIds) {
            internalTxService.removeProduct(productId);
        }


    }

    @Override
    public List<ProductInActionDto> findProductsInAction(EshopUuid eshopUuid) {
        return internalTxService.findProductsInAction(eshopUuid);
    }

    @Override
    public List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid) {
        return internalTxService.findProductsBestPriceInGroupDto(eshopUuid);
    }
}
