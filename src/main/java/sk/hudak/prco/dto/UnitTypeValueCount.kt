package sk.hudak.prco.dto

import sk.hudak.prco.api.Unit

import java.math.BigDecimal

data class UnitTypeValueCount(
        val unit: Unit,
        val value: BigDecimal,
        val packageCount: Int?) { //TODO remove ? na konci
}
