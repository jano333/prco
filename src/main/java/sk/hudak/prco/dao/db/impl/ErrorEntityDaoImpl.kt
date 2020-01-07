package sk.hudak.prco.dao.db.impl

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import org.springframework.stereotype.Repository
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.ErrorEntityDao
import sk.hudak.prco.dto.ErrorFindFilterDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.model.ErrorEntity
import sk.hudak.prco.model.QErrorEntity
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.persistence.EntityManager
import kotlin.collections.ArrayList

// musi byt open koli springu, lebo ked nie je tak nespusti spring boot app
@Repository
open class ErrorEntityDaoImpl(em: EntityManager) : BaseDaoImpl<ErrorEntity>(em), ErrorEntityDao {

    override fun findById(id: Long): ErrorEntity {
        return findById(ErrorEntity::class.java, id)
    }

    override fun findByUrl(url: String): ErrorEntity? {
        return queryFactory
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.url.eq(url))
                .fetchFirst()
    }

    override fun findAll(): List<ErrorEntity> {
        return queryFactory
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .fetch()
    }

    override fun findByTypes(vararg errorTypes: ErrorType): List<ErrorEntity> {
        return queryFactory
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.errorType.`in`(*errorTypes))
                .fetch()
    }

    override fun getCountOfType(type: ErrorType): Long? {
        return queryFactory
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.errorType.eq(type))
                .fetchCount()
    }

    override fun findOlderThan(unitCount: Int, timeUnit: TimeUnit): List<ErrorEntity> {
        return queryFactory
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.updated.lt(calculateDate(unitCount, timeUnit)))
                .fetch()
    }

    override fun findErrorsByFilter(findDto: ErrorFindFilterDto): List<ErrorEntity> {
        var result: ArrayList<ErrorEntity> = ArrayList()

        EshopUuid.values().forEach { eshopUuid ->
            val query = queryFactory
                    .select(QErrorEntity.errorEntity)
                    .from(QErrorEntity.errorEntity)
                    .where(QErrorEntity.errorEntity.eshopUuid.eq(eshopUuid))

            if (findDto.errorTypes != null) {
                query.where(QErrorEntity.errorEntity.errorType.`in`(*findDto.errorTypes!!))
            }
            if (findDto.errorTypesToSkip != null) {
                query.where(QErrorEntity.errorEntity.errorType.notIn(*findDto.errorTypesToSkip!!))
            }

            if (findDto.statusCodes != null) {
                query.where(QErrorEntity.errorEntity.statusCode.`in`(*findDto.statusCodes!!))
            }
            if (findDto.statusCodesToSkip != null) {
                query.where(QErrorEntity.errorEntity.statusCode.isNull
                        .or(QErrorEntity.errorEntity.statusCode.notIn(*findDto.statusCodesToSkip!!)))
            }
            query.limit(findDto.maxCountPerEshop.toLong())
            query.orderBy(OrderSpecifier(Order.DESC, QErrorEntity.errorEntity.updated))
            result.addAll(query.fetch())
        }

        //zosortulem vsetky podla datumu
        result.sortWith(Comparator { e1, e2 ->
            e1.updated.compareTo(e2.updated)*(-1)
        })
        return result
    }

    override fun findByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): List<ErrorEntity> =
            from(QErrorEntity.errorEntity)
                    .where(QErrorEntity.errorEntity.eshopUuid.eq(eshopUuid))
                    .limit(maxCountToDelete)
                    .fetch()

    override fun findByMaxCount(limit: Int, errorType: ErrorType?): List<ErrorEntity> {
        val from = queryFactory
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
        if (errorType != null) {
            from.where(QErrorEntity.errorEntity.errorType.eq(errorType))
        }
        from.orderBy(OrderSpecifier(Order.DESC, QErrorEntity.errorEntity.created))
        return from.limit(limit.toLong()).fetch()
    }

    //TODO move to utils
    private fun calculateDate(unitCount: Int, timeUnit: TimeUnit): Date {
        var unit: TemporalUnit?
        when (timeUnit) {
            TimeUnit.DAYS -> unit = ChronoUnit.DAYS
            //TODO ostatne
            else -> throw PrcoRuntimeException("Not yet implemented")
        }
        return Date.from(Date().toInstant().minus(unitCount.toLong(), unit))
    }

    override fun findByUrls(urls: List<String>): List<ErrorEntity> {
        return queryFactory
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.url.`in`(*urls.toTypedArray()))
                .fetch()
    }
}
