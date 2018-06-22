package sk.hudak.prco.dao.db;

import sk.hudak.prco.model.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface GroupOfProductFindEntityDao {

    List<ProductEntity> findProductsWitchAreNotInAnyGroup();

    long countOfProductsWitchAreNotInAnyGroup();

    long countOfProductInGroup(String groupName);

    Optional<Long> findFirstProductGroupId(Long productId);

    List<Long> findGroupIdsWithProductId(Long productId);
}
