package sk.hudak.prco.service;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductUpdateDataDto;
import sk.hudak.prco.dto.internal.StatisticForUpdateForEshopDto;
import sk.hudak.prco.dto.product.ProductBestPriceInGroupDto;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductInActionDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// TODO javadoc
public interface ProductService {

    boolean existProductWithUrl(String productURL);

    List<ProductFullDto> findProducts(ProductFilterUIDto filter);

    List<ProductInActionDto> findProductsInAction(EshopUuid eshopUuid);

    List<ProductFullDto> findProductsInGroup(Long groupId, EshopUuid... eshopsToSkip);

    List<ProductFullDto> findProductsNotInAnyGroup();

    Optional<ProductDetailInfo> findProductForUpdate(EshopUuid eshopUuid, int olderThanInHours);

    Optional<ProductDetailInfo> findProductForUpdate(Long productId);

    List<ProductFullDto> findProductsForExport();


    EshopUuid findEshopForProductId(Long productId);

    void updateProductData(ProductUpdateDataDto productUpdateDataDto);

    void updateCommonPrice(Long productId, BigDecimal newCommonPrice);

    void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid);

    void removeProduct(Long productId);

    /**
     * Resetne ceny a akcie na null, last update na aktualny datum
     *
     * @param productId id produktu
     */
    void markProductAsUnavailable(Long productId);

    void resetUpdateDateProduct(Long productId);

    List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid);

    StatisticForUpdateForEshopDto getStatisticForUpdateForEshop(EshopUuid eshopUuid, int olderThanInHours);
}
