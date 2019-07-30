package sk.hudak.prco.utils

import java.math.BigDecimal
import java.math.RoundingMode

object CalculationUtils {

    private val SCALE = 5
    private val VALUE_1000 = BigDecimal(1000)

    @JvmStatic
    fun recalculateToKilograms(grams: BigDecimal): BigDecimal {
        return grams.divide(VALUE_1000, SCALE, RoundingMode.HALF_UP)
    }

    @JvmStatic
    fun recalculateToLites(militers: BigDecimal): BigDecimal {
        return militers.divide(VALUE_1000, SCALE, RoundingMode.HALF_UP)
    }

    @JvmStatic
    fun calculatePercetage(actionValue: BigDecimal, defaultValue: BigDecimal): Int {
        val divide = actionValue
                .multiply(BigDecimal.valueOf(100))
                .divide(defaultValue, SCALE, RoundingMode.HALF_UP)
        val i = divide.toInt()
        return 100 - i
    }
}
