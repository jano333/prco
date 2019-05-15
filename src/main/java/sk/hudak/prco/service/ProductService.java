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

/**
 *
 */
public interface ProductService {

    /**
     * @param productURL URL of product
     * @return true, if product exist (product table only)
     */
    boolean existProductWithUrl(String productURL);

    /**
     * @param productUpdateDataDto
     */
    void updateProduct(ProductUpdateDataDto productUpdateDataDto);

    /**
     * @param productId      product id
     * @param newCommonPrice
     */
    void updateProductPrice(Long productId, BigDecimal newCommonPrice);

    /**
     * @param eshopUuid eshop unique identification
     */
    void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid);

    /**
     * @param productId product id
     */
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

    /**
     * @param filter
     * @return
     */
    List<ProductFullDto> findProducts(ProductFilterUIDto filter);

    /**
     * @param groupId
     * @param withPriceOnly
     * @param eshopsToSkip
     * @return
     */
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

    /**
     * @param eshopUuid
     * @return
     */
    List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid);

    /**
     * @return
     */
    List<ProductFullDto> findProductsForExport();

    /**
     * @param eshopUuid
     * @return
     */
    List<ProductFullDto> findDuplicityProductsByNameAndPriceInEshop(EshopUuid eshopUuid);

    /**
     * @param productUrl        URL of product
     * @param productIdToIgnore product id which will be ignored during finding
     * @return product id if found, empty if not
     */
    Optional<Long> getProductWithUrl(String productUrl, Long productIdToIgnore);
}
