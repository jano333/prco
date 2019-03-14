package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dao.db.GroupEntityDao;
import sk.hudak.prco.dao.db.ProductEntityDao;
import sk.hudak.prco.dto.group.GroupCreateDto;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.group.GroupListDto;
import sk.hudak.prco.dto.group.GroupListExtendedDto;
import sk.hudak.prco.dto.group.GroupUpdateDto;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.GroupEntity;
import sk.hudak.prco.model.ProductEntity;
import sk.hudak.prco.service.GroupService;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sk.hudak.prco.utils.Validate.atLeastOneIsNotNull;
import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Slf4j
@Service("groupService")
public class GroupServiceImpl implements GroupService {

    public static final String PRODUCT_IDS = "productIds";

    @Autowired
    private ProductEntityDao productEntityDao;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    private PrcoOrikaMapper prcoMapper;

    @Override
    public Long createGroup(GroupCreateDto createDto) {
        notNull(createDto, "createDto");
        notNullNotEmpty(createDto.getName(), "name");

        // validacia, ci uz group s takym nazvom existuje
        if (groupEntityDao.findGroupByName(createDto.getName()).isPresent()) {
            throw new PrcoRuntimeException(GroupEntity.class.getSimpleName() + " with name '" + createDto.getName() + "' already exist");
        }

        GroupEntity entity = new GroupEntity();
        entity.setName(createDto.getName());
        if (createDto.getProductIds() != null) {
            entity.setProducts(createDto.getProductIds().stream()
                    .map(productEntityDao::findById)
                    .collect(Collectors.toList()));
        }

        Long id = groupEntityDao.save(entity);
        log.debug("create entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());
        return id;
    }

    @Override
    public void updateGroup(GroupUpdateDto updateDto) {
        notNull(updateDto, "updateDto");
        notNull(updateDto.getId(), "id");
        notNullNotEmpty(updateDto.getName(), "name");

        if (groupEntityDao.existGroupByName(updateDto.getName(), updateDto.getId())) {
            throw new PrcoRuntimeException("Another " + GroupEntity.class.getSimpleName() + " with name '" + updateDto.getName() + "' already exist");
        }

        GroupEntity groupEntity = groupEntityDao.findById(updateDto.getId());
        //FIXME cez Oriku
        groupEntity.setName(updateDto.getName());
        groupEntityDao.update(groupEntity);

        log.debug("update entity {} with id {}", GroupEntity.class.getSimpleName(), updateDto.getId());
    }

    @Override
    public void addProductsToGroup(Long groupId, Long... productIds) {
        notNull(groupId, "groupId");
        notNull(productIds, PRODUCT_IDS);
        atLeastOneIsNotNull(productIds, PRODUCT_IDS);

        //FIXME fix optimalizaciu lebo to viem pridat cez id cka do tabulky...
        GroupEntity groupEntity = groupEntityDao.findById(groupId);
        boolean isGroupEmpty = groupEntity.getProducts().isEmpty();

        for (Long productId : productIds) {
            ProductEntity productEntity = productEntityDao.findById(productId);

            // kontorola ci uz taky produkt v skupine nie je...
            List<Long> groupProudctIds = groupEntity.getProducts().stream()
                    .map(ProductEntity::getId)
                    .collect(Collectors.toList());
            if (groupProudctIds.contains(productEntity.getId())) {
                throw new PrcoRuntimeException("Group width id " + groupId + " already contains product with id " + productId);
            }

            // kontrola, ci unit pridavaneho produktu je rovnaka ako unit uz pridaneho produktu
            if (!isGroupEmpty) {
                Unit unitInGroup = groupEntity.getProducts().get(0).getUnit();
                if (!unitInGroup.equals(productEntity.getUnit())) {
                    throw new PrcoRuntimeException("Unit not match. Group unit " + unitInGroup + ", product unit " + productEntity.getUnit());
                }
            }

            groupEntity.getProducts().add(productEntity);
            log.debug("adding product {} to group {}", productEntity.getName(), groupEntity.getName());
        }

        groupEntityDao.update(groupEntity);
        log.debug("update entity {} with id {}", groupEntity.getClass().getSimpleName(), groupEntity.getId());
    }

    @Override
    public void removeProductsFromGroup(Long groupId, Long... productIds) {
        notNull(groupId, "groupId");
        notNull(productIds, PRODUCT_IDS);
        atLeastOneIsNotNull(productIds, PRODUCT_IDS);

        GroupEntity groupEntity = groupEntityDao.findById(groupId);

        for (Long productId : productIds) {
            groupEntity.getProducts().remove(productEntityDao.findById(productId));
        }

        groupEntityDao.update(groupEntity);

        log.debug("products id {} removed from group {}", productIds, groupId);
    }

    @Override
    public List<GroupListDto> getGroupsWithoutProduct(Long productId) {
        notNull(productId, "productId");

        return groupEntityDao.findGroupsWithoutProduct(productId).stream()
                .map(entity -> prcoMapper.map(entity, GroupListDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupListDto> findGroups(GroupFilterDto groupFilterDto) {
        notNull(groupFilterDto, "groupFilterDto");

        return groupEntityDao.findGroups(groupFilterDto).stream()
                .map(entity -> prcoMapper.map(entity, GroupListDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupListExtendedDto> findAllGroupExtended() {
        return groupEntityDao.findGroups(new GroupFilterDto()).stream()
                .map(groupEntity -> {
                    //FIXME cez orika mapper
                    GroupListExtendedDto dto = new GroupListExtendedDto();
                    dto.setId(groupEntity.getId());
                    dto.setName(groupEntity.getName());
                    dto.setCountOfProduct(Long.valueOf(groupEntity.getProducts().size()));
                    dto.setCountOfProductInEshop(findCountOfProductInGroupPerEshop(groupEntity));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public GroupIdNameDto getGroupById(Long groupId) {
        return prcoMapper.map(groupEntityDao.findById(groupId), GroupIdNameDto.class);
    }

    private Map<EshopUuid, Long> findCountOfProductInGroupPerEshop(GroupEntity groupEntity) {
        List<ProductEntity> products = groupEntity.getProducts();
        if (products.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<EshopUuid, Long> result = new EnumMap<>(EshopUuid.class);
        products.stream().forEach(productEntity -> {
            EshopUuid eshopUuid = productEntity.getEshopUuid();
            if (!result.containsKey(eshopUuid)) {
                result.put(eshopUuid, 1L);
            } else {
                long previousValue = result.get(eshopUuid).longValue();
                result.put(eshopUuid, ++previousValue);
            }
        });
        return result;
    }
}
