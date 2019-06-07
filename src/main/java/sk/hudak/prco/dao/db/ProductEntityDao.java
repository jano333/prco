package sk.hudak.prco.dao.db;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.model.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductEntityDao extends BaseDao<ProductEntity> {

    boolean existWithUrl(String url);

    /**
     * @param eshopUuid
     * @param olderThanInHours pocet v hodinach, kolko minimalne sa neupdatoval dany record
     * @return
     */
    Optional<ProductEntity> findProductForUpdate(EshopUuid eshopUuid, int olderThanInHours);

    List<ProductEntity> findAll();

    List<ProductEntity> findByFilter(ProductFilterUIDto filter);

    Optional<ProductEntity> findByUrl(String productUrl);

    long count();

    long countOfAllProductInEshop(EshopUuid eshopUuid);

    long countOfAllProductInEshopUpdatedMax24Hours(EshopUuid eshopUuid);

    long countOfProductsWaitingToBeUpdated(EshopUuid eshopUuid, int olderThanInHours);

    long countOfProductsAlreadyUpdated(EshopUuid eshopUuid, int olderThanInHours);

    Optional<Long> getProductWithUrl(String productUrl, Long productIdToIgnore);
}
