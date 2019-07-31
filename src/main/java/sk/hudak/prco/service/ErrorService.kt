package sk.hudak.prco.service

import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.dto.ErrorFindFilterDto
import sk.hudak.prco.dto.ErrorListDto
import java.util.concurrent.Future

interface ErrorService {

    val statisticForErrors: Map<ErrorType, Long>

    // TODO cron na odmazavanie

    fun createError(createDto: ErrorCreateDto): Long?

    fun findAll(): List<ErrorListDto>

    /**
     * Najde maximalne `limit` error zotriedenych od najnovsieho po najstarsi
     * @param limit
     * @return
     */
    fun findErrorByMaxCount(limit: Int, errorType: ErrorType): List<ErrorListDto>

    fun findErrorsByTypes(vararg errorTypes: ErrorType): List<ErrorListDto>

    fun findErrorsByFilter(findDto: ErrorFindFilterDto): List<ErrorListDto>

    /**
     * Odmaze:
     * - vsetky chyby ktore maju update date starsi ako 30 dni.
     * - vsetky chyby, ktore maju URL rovnaku ako v not interested produkts
     * -
     */
    fun startErrorCleanUp(): Future<Void>
}
