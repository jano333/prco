package sk.hudak.prco.dto

import sk.hudak.prco.api.Unit

import java.math.BigDecimal


class UnitData(val unit: Unit, val unitValue: BigDecimal, val unitPackageCount: Int?)
