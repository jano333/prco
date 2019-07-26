package sk.hudak.prco.exception

import java.math.BigDecimal

class StringToNumberConvertPrcoException(val value: String, cause: Throwable) :
        PrcoRuntimeException(
                "error while converting value $value to ${BigDecimal::class.java.name}", cause)
