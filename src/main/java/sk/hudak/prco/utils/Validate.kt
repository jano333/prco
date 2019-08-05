package sk.hudak.prco.utils

import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.exception.RequiredParameterException

object Validate {

    private const val ONE = 1

    @JvmStatic
    fun notNull(value: Any?, parameterName: String) {
        if (value == null) {
            throw RequiredParameterException(parameterName)
        }
    }

    @JvmStatic
    fun notEmpty(value: String, parameterName: String) {
        if (value.isBlank()) {
            throw RequiredParameterException(parameterName)
        }
    }


    @JvmStatic
    fun notNullNotEmpty(value: String?, parameterName: String) {
        if (value.isNullOrEmpty()) {
            throw RequiredParameterException(parameterName)
        }
    }

    @JvmStatic
    fun notNullNotEmpty(values: Array<String>?, parameterName: String) {
        if (values.isNullOrEmpty()) {
            throw RequiredParameterException(parameterName)
        }
    }

    @JvmStatic
    fun notNullNotEmpty(values: Collection<*>?, parameterName: String) {
        if (values.isNullOrEmpty()) {
            throw RequiredParameterException(parameterName)
        }
    }

    @JvmStatic
    fun atLeastOneIsNotNull(values: Array<Long>?, parameterName: String) {
        if (values.isNullOrEmpty()) {
            throw PrcoRuntimeException("At least one parameter $parameterName is required")
        }
    }

    @JvmStatic
    fun notNegativeAndNotZeroValue(value: Int, parameterName: String) {
        if (value < ONE) {
            throw PrcoRuntimeException("Value is zero or negative for $parameterName .")
        }
    }


}
