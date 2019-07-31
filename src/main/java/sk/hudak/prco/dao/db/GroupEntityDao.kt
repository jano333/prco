package sk.hudak.prco.dao.db;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.dto.GroupFilterDto;
import sk.hudak.prco.model.GroupEntity;
import sk.hudak.prco.model.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface GroupEntityDao extends BaseDao<GroupEntity> {

    Optional<GroupEntity> findGroupByName(String groupName);

    //TODO zmenit navratovu hodnotu na Optional<GroupEntity>
    boolean existGroupByName(String name, Long groupIdToSkip);

    List<GroupEntity> findGroupsWithoutProduct(Long productId);

    List<GroupEntity> findGroups(GroupFilterDto groupFilterDto);

    //TODO move to ProductService...
    List<ProductEntity> findProductsInGroup(Long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip);

    List<String> findAllGroupNames();

    /**
     * Zoznam vsetkych group, v ktorych sa dany product nachadza.
     *
     * @param productId
     * @return
     */
    List<GroupEntity> findGroupsForProduct(Long productId);
}
