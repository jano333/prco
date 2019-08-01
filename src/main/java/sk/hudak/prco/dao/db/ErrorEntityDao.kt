package sk.hudak.prco.dao.db

import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.ErrorFindFilterDto
import sk.hudak.prco.model.ErrorEntity
import java.util.concurrent.TimeUnit

interface ErrorEntityDao : BaseDao<ErrorEntity> {

    fun findByUrl(url: String): ErrorEntity?

    fun findAll(): List<ErrorEntity>

    fun findByTypes(vararg errorTypes: ErrorType): List<ErrorEntity>

    /**
     * vrati pocet chyb na zaklade typu
     *
     * @param type
     * @return
     */
    fun getCountOfType(type: ErrorType): Long?

    /**
     * vyhlada starsie ako pocet jednotie z vstupu(zatial podpovovane len dni !!)
     *
     * @param unitCount
     * @param timeUnit
     * @return zoznam entit
     */
    fun findOlderThan(unitCount: Int, timeUnit: TimeUnit): List<ErrorEntity>

    fun findByUrls(urls: List<String>): List<ErrorEntity>

    fun findByMaxCount(limit: Int, errorType: ErrorType?): List<ErrorEntity>

    fun findErrorsByFilter(findDto: ErrorFindFilterDto): List<ErrorEntity>
}
