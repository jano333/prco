package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.GroupEntityDao
import sk.hudak.prco.dao.db.ProductEntityDao
import sk.hudak.prco.dto.*
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.model.GroupEntity
import sk.hudak.prco.service.GroupService
import sk.hudak.prco.utils.Validate.notEmpty
import sk.hudak.prco.utils.Validate.notNull
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import java.util.stream.Collectors

@Service("groupService")
class GroupServiceImpl(
        @Autowired private val productEntityDao: ProductEntityDao,
        @Autowired private val groupEntityDao: GroupEntityDao,
        @Autowired private val prcoMapper: PrcoOrikaMapper) : GroupService {

    companion object {
        val log = LoggerFactory.getLogger(GroupServiceImpl::class.java)!!

        const val PRODUCT_IDS = "productIds"
    }

    override fun createGroup(createDto: GroupCreateDto): Long {
        notEmpty(createDto.name, "name")

        // validacia, ci uz group s takym nazvom existuje
        if (groupEntityDao.findGroupByName(createDto.name) != null) {
            throw PrcoRuntimeException(GroupEntity::class.java.simpleName + " with name '" + createDto.name + "' already exist")
        }

        val entity = GroupEntity()
        entity.name = createDto.name
        if (createDto.productIds != null) {
            entity.products = createDto.productIds.stream()
                    .map { productEntityDao.findById(it) }
                    .collect(Collectors.toList())
        }

        val id = groupEntityDao.save(entity)
        log.debug("create entity {} with id {}", entity.javaClass.simpleName, entity.id)
        //TODO !! nakonci odsranit ked budu urobene DAO
        return id!!
    }

    override fun updateGroup(updateDto: GroupUpdateDto) {
        notNull(updateDto.id, "id")
        notNullNotEmpty(updateDto.name, "name")

        if (groupEntityDao.existGroupByName(updateDto.name!!, updateDto.id)) {
            throw PrcoRuntimeException("Another " + GroupEntity::class.java.simpleName + " with name '" + updateDto.name + "' already exist")
        }

        val groupEntity = groupEntityDao.findById(updateDto.id!!)
        //FIXME cez Oriku
        groupEntity.name = updateDto.name
        groupEntityDao.update(groupEntity)

        log.debug("update entity {} with id {}", GroupEntity::class.java.simpleName, updateDto.id)
    }

    override fun addProductsToGroup(groupId: Long, vararg productIds: Long) {
        notNull(productIds, PRODUCT_IDS)
        //TODO
        //        atLeastOneIsNotNull(productIds, PRODUCT_IDS);

        //FIXME fix optimalizaciu lebo to viem pridat cez id cka do tabulky...
        val groupEntity = groupEntityDao.findById(groupId)
        val isGroupEmpty = groupEntity.products.isEmpty()

        for (productId in productIds) {
            val productEntity = productEntityDao.findById(productId)

            // kontorola ci uz taky produkt v skupine nie je...
            val groupProudctIds = groupEntity.products.stream()
                    .map { it.id }
                    .collect(Collectors.toList())

            if (groupProudctIds.contains(productEntity.id)) {
                throw PrcoRuntimeException("Group width id $groupId already contains product with id $productId")
            }

            // kontrola, ci unit pridavaneho produktu je rovnaka ako unit uz pridaneho produktu
            if (!isGroupEmpty) {
                val unitInGroup = groupEntity.products[0].unit
                if (unitInGroup != productEntity.unit) {
                    throw PrcoRuntimeException("Unit not match. Group unit " + unitInGroup + ", product unit " + productEntity.unit)
                }
            }

            groupEntity.products.add(productEntity)
            log.debug("adding product {} to group {}", productEntity.name, groupEntity.name)
        }

        groupEntityDao.update(groupEntity)
        log.debug("update entity {} with id {}", groupEntity.javaClass.simpleName, groupEntity.id)
    }

    override fun removeProductsFromGroup(groupId: Long?, vararg productIds: Long) {
        notNull(groupId, "groupId")
        notNull(productIds, PRODUCT_IDS)
        //TODO
        //        atLeastOneIsNotNull(productIds, PRODUCT_IDS);

        val groupEntity = groupEntityDao.findById(groupId!!)

        for (productId in productIds) {
            groupEntity.products.remove(productEntityDao.findById(productId))
        }

        groupEntityDao.update(groupEntity)

        log.debug("products id {} removed from group {}", productIds, groupId)
    }

    override fun getGroupsWithoutProduct(productId: Long?): List<GroupListDto> {
        notNull(productId, "productId")

        return groupEntityDao.findGroupsWithoutProduct(productId).stream()
                .map { entity -> prcoMapper.map(entity, GroupListDto::class.java) }
                .collect(Collectors.toList())
    }

    override fun findGroups(groupFilterDto: GroupFilterDto): List<GroupListDto> {
        return groupEntityDao.findGroups(groupFilterDto).stream()
                .map { entity -> prcoMapper.map(entity, GroupListDto::class.java) }
                .collect(Collectors.toList())
    }

    override fun findAllGroupExtended(): List<GroupListExtendedDto> {
        return groupEntityDao.findGroups(GroupFilterDto()).stream()
                .map { groupEntity ->
                    //FIXME cez orika mapper
                    val dto = GroupListExtendedDto()
                    dto.id = groupEntity.id
                    dto.name = groupEntity.name
                    dto.countOfProduct = java.lang.Long.valueOf(groupEntity.products.size.toLong())
                    dto.countOfProductInEshop = findCountOfProductInGroupPerEshop(groupEntity)
                    dto
                }
                .collect(Collectors.toList())
    }

    override fun getGroupById(groupId: Long?): GroupIdNameDto {
        return prcoMapper.map(groupEntityDao.findById(groupId!!), GroupIdNameDto::class.java)
    }

    private fun findCountOfProductInGroupPerEshop(groupEntity: GroupEntity): Map<EshopUuid, Long> {
        val products = groupEntity.products
        if (products.isEmpty()) {
            return emptyMap()
        }
        val result = EnumMap<EshopUuid, Long>(EshopUuid::class.java)
        products.stream().forEach { productEntity ->
            val eshopUuid = productEntity.eshopUuid
            if (!result.containsKey(eshopUuid)) {
                result[eshopUuid] = 1L
            } else {
                var previousValue = result[eshopUuid]!!
                result[eshopUuid] = previousValue.plus(1L)
            }
        }
        return result
    }


}
