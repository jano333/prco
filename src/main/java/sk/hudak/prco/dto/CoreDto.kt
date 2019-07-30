package sk.hudak.prco.dto

import sk.hudak.prco.api.Unit
import java.io.Serializable
import java.math.BigDecimal

/**
 * Marker interface.
 */
interface DtoAble : Serializable

abstract class IdNameDto : DtoAble {
    var id: Long? = null
    var name: String? = null

    override fun toString(): String {
        return "IdNameDto(id=$id, name=$name)"
    }
}

class UnitData(val unit: Unit, val unitValue: BigDecimal, val unitPackageCount: Int?)

data class UnitTypeValueCount(
        val unit: Unit,
        val value: BigDecimal,
        val packageCount: Int?) { //TODO remove ? na konci
}