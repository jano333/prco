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

    //TODO niekde je delete a niekde remove na product ujednotit to ...

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
     * Update of 'common' price of product.
     *
     * @param productId      product id
     * @param newCommonPrice common(bezna cena) productu
     */
    void updateProductCommonPrice(Long productId, BigDecimal newCommonPrice);

    /**
     * set field 'LastTimeDataUpdated' to null for product in given eshop
     *
     * @param eshopUuid eshop unique identification
     */
    void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid);

    /**
     * set field 'lastTimeDataUpdated' to null for product with given id
     *
     * @param productId product id
     */
    void resetUpdateDateProduct(Long productId);

    /**
     * Reset of all prices to null, same for action and 'lastTimeDataUpdated'.
     *
     * @param productId product id
     */
    void markProductAsUnavailable(Long productId);

    /**
     * Move product to NotInteredted product. Before that it removes it from all group and after moving it delete it.
     *
     * @param productId product id
     */
    void markProductAsNotInterested(Long productId);


    /**
     * Remove product from all groups where it is and finally remove product itself.
     *
     * @param productId product id
     */
    void removeProduct(Long productId);

    // ----------- GET -----------

    /**
     * Product data for product with given id.
     *
     * @param productId product id
     * @return product data
     */
    ProductAddingToGroupDto getProduct(Long productId);

    /**
     * Eshop to which the product with given id belongs to.
     *
     * @param productId product id
     * @return eshop unique identifier
     */
    EshopUuid getEshopForProductId(Long productId);

    /**
     * @param eshopUuid        eshop unique identifier
     * @param olderThanInHours
     * @return
     */
    Optional<ProductDetailInfo> getProductForUpdate(EshopUuid eshopUuid, int olderThanInHours);

    /**
     * @param productId product id
     * @return
     */
    ProductDetailInfo getProductForUpdate(Long productId);

    /**
     * @param eshopUuid        eshop unique identifier
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
     * @param groupId       id of group
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
     * @param eshopUuid eshop unique identifier
     * @return list of product in action for given eshop
     */
    List<ProductInActionDto> findProductsInAction(EshopUuid eshopUuid);

    /**
     * @param eshopUuid eshop unique identifier
     * @return
     */
    List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid);

    /**
     * @return
     */
    List<ProductFullDto> findProductsForExport();

    /**
     * @param eshopUuid eshop unique identifier
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
