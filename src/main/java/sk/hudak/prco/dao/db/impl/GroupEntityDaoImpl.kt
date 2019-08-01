package sk.hudak.prco.dao.db.impl

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.GroupEntityDao
import sk.hudak.prco.dao.db.GroupOfProductFindEntityDao
import sk.hudak.prco.dao.db.ProductEntityDao
import sk.hudak.prco.dto.GroupFilterDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.model.GroupEntity
import sk.hudak.prco.model.ProductEntity
import sk.hudak.prco.model.QGroupEntity
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

@Repository
class GroupEntityDaoImpl(
        @Autowired private val productEntityDao: ProductEntityDao,
        @Autowired private val groupOfProductFindEntityDao: GroupOfProductFindEntityDao
) : BaseDaoImpl<GroupEntity>(), GroupEntityDao {

    override fun findById(id: Long): GroupEntity {
        return findById(GroupEntity::class.java, id)
    }

    override fun findGroups(groupFilterDto: GroupFilterDto): List<GroupEntity> {
        val query = from(QGroupEntity.groupEntity)
        // ids
        val ids = groupFilterDto.ids
        if (ids != null) {
            query.where(QGroupEntity.groupEntity.id.`in`(*ids))
        }
        // name
        val name = groupFilterDto.name
        if (StringUtils.isNotBlank(name)) {
            query.where(QGroupEntity.groupEntity.name.isNotNull)
            query.where(QGroupEntity.groupEntity.name.eq(name!!))
        }
        // eshop only
        val eshopOnly = groupFilterDto.eshopOnly
        if (eshopOnly != null) {
            //TODO impl
        }
        query.orderBy(OrderSpecifier(Order.ASC, QGroupEntity.groupEntity.name))
        return query.fetch()
    }

    override fun findGroupsWithoutProduct(productId: Long?): List<GroupEntity> {
        //FIXME optimalizovat
        val byId = productEntityDao.findById(productId!!)
        val query = from(QGroupEntity.groupEntity)
        query.where(QGroupEntity.groupEntity.products.contains(byId).not())
        query.orderBy(OrderSpecifier(Order.ASC, QGroupEntity.groupEntity.name))
        return query.distinct().fetch()
    }

    override fun findGroupByName(groupName: String): GroupEntity? {
        val query = from(QGroupEntity.groupEntity)
        query.where(QGroupEntity.groupEntity.name.eq(groupName))
        return query.fetchFirst()
    }

    override fun existGroupByName(groupName: String, groupIdToSkip: Long?): Boolean {
        queryFactory
                .select(QGroupEntity.groupEntity.id)
                .from(QGroupEntity.groupEntity)
                .where(QGroupEntity.groupEntity.id.ne(groupIdToSkip!!))
                .where(QGroupEntity.groupEntity.name.eq(groupName))
                .exists()


        val query = from(QGroupEntity.groupEntity)
        if (groupIdToSkip != null) {
            query.where(QGroupEntity.groupEntity.id.ne(groupIdToSkip))
        }
        query.where(QGroupEntity.groupEntity.name.eq(groupName))
        //FIXME optimalizovat na exist...
        return query.fetchFirst() != null
    }

    override fun findProductsInGroup(groupId: Long?, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid): List<ProductEntity> {
        val query = from(QGroupEntity.groupEntity)
        query.where(QGroupEntity.groupEntity.id.eq(groupId!!))
        val groupEntity = query.fetchFirst()
                ?: throw PrcoRuntimeException(GroupEntity::class.java.simpleName + " not found by id " + groupId)

//FIXME cez DB urobit neaky komplikovany select..
        val products = groupEntity.products
        //filter na tie, ktore maju cenu
        var productEntityStream = products.stream()
        if (withPriceOnly) {
            productEntityStream = productEntityStream.filter { p -> p.priceForUnit != null }
        }
        if (eshopsToSkip != null) {
            productEntityStream = productEntityStream.filter { p -> !Arrays.asList(*eshopsToSkip).contains(p.eshopUuid) }
        }

        val withValidPriceForPackage = productEntityStream.collect(Collectors.toList())

        // FIXME cez db query
        //        Collections.sort(withValidPriceForPackage, Comparator.comparing(ProductEntity::getPriceForUnit));
        Collections.sort(withValidPriceForPackage,
                Comparator.comparing<ProductEntity?, BigDecimal?> { productEntity ->
                    if (productEntity?.priceForUnit != null)
                        productEntity.priceForUnit else BigDecimal.ZERO
                }
        )

        return withValidPriceForPackage
    }

    override fun findAllGroupNames(): List<String> {
        return queryFactory
                .select(QGroupEntity.groupEntity.name)
                .from(QGroupEntity.groupEntity)
                .fetch()
    }

    override fun findGroupsForProduct(productId: Long?): List<GroupEntity> {
        return queryFactory
                .select(QGroupEntity.groupEntity)
                .from(QGroupEntity.groupEntity)
                .where(QGroupEntity.groupEntity.id.`in`(groupOfProductFindEntityDao!!.findGroupIdsWithProductId(productId)))
                .fetch()
    }


}
