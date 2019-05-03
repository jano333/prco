package sk.hudak.prco.service;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductUpdateDataDto;
import sk.hudak.prco.dto.internal.StatisticForUpdateForEshopDto;
import sk.hudak.prco.dto.product.ProductAddingToGroupDto;
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

    /**
     * @param productURL url of product
     * @return true, if product exist (product table only)
     */
    boolean existProductWithUrl(String productURL);

    void updateProduct(ProductUpdateDataDto productUpdateDataDto);

    void updateProductPrice(Long productId, BigDecimal newCommonPrice);

    void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid);

    void resetUpdateDateProduct(Long productId);

    /**
     * Resetne ceny a akcie na null, last update na aktualny datum
     *
     * @param productId product id
     */
    void markProductAsUnavailable(Long productId);

    /**
     * @param productId product id
     */
    void markProductAsNotInterested(Long productId);

    //TODO niekde je delete a niekde remove...

    /**
     * @param productId product id
     */
    void removeProduct(Long productId);

    // ----------- GET -----------

    /**
     * @param productId product id
     * @return
     */
    ProductAddingToGroupDto getProduct(Long productId);

    /**
     * @param productId product id
     * @return
     */
    EshopUuid getEshopForProductId(Long productId);

    /**
     * @param eshopUuid
     * @param olderThanInHours
     * @return
     */
    Optional<ProductDetailInfo> getProductForUpdate(EshopUuid eshopUuid, int olderThanInHours);

    /**
     * @param productId product id
     * @return
     */
    Optional<ProductDetailInfo> getProductForUpdate(Long productId);

    /**
     * @param eshopUuid
     * @param olderThanInHours
     * @return
     */
    StatisticForUpdateForEshopDto getStatisticForUpdateForEshop(EshopUuid eshopUuid, int olderThanInHours);

    // ----------- FIND -----------

    List<ProductFullDto> findProducts(ProductFilterUIDto filter);

    List<ProductFullDto> findProductsInGroup(Long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip);

    /**
     * @return list of products which are not in any group
     */
    List<ProductFullDto> findProductsNotInAnyGroup();

    /**
     * @param eshopUuid eshop id
     * @return return list of product in action for given eshop code <code>eshopUuid</code>
     */
    List<ProductInActionDto> findProductsInAction(EshopUuid eshopUuid);

    List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid);

    List<ProductFullDto> findProductsForExport();

    List<ProductFullDto> findDuplicityProductsByNameAndPriceInEshop(EshopUuid eshopUuid);
}
