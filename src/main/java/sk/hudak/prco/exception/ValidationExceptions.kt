package sk.hudak.prco.exception

import sk.hudak.prco.api.EshopUuid
import java.math.BigDecimal

class StringToNumberConvertPrcoException(val value: String, cause: Throwable) :
        PrcoRuntimeException("error while converting value $value to ${BigDecimal::class.java.name}", cause)

class EshopNotFoundParserException(val productUrl: String) :
        PrcoRuntimeException(EshopUuid::class.java.simpleName + " for $productUrl not found.")

class EshopParserNotFoundException(val eshopUuid: EshopUuid) :
        PrcoRuntimeException("Parser implementation for eshop $eshopUuid not found.")

class RequiredParameterException(parameterName: String) :
        PrcoRuntimeException("parameter $parameterName is null/empty")
