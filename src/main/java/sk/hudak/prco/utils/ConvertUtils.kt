package sk.hudak.prco.utils

import sk.hudak.prco.exception.StringToNumberConvertPrcoException
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.math.BigDecimal

object ConvertUtils {

    private const val COMMA = ","
    private const val DOT = "."

    @JvmStatic
    fun convertToBigDecimal(value: String): BigDecimal {
        notNullNotEmpty(value, "value")

        try {
            return BigDecimal(value.replace(COMMA, DOT))

        } catch (e: Exception) {
            throw StringToNumberConvertPrcoException(value, e)
        }

    }
}
