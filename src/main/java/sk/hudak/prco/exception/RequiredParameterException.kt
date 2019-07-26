package sk.hudak.prco.exception

class RequiredParameterException(parameterName: String) :
        PrcoRuntimeException("parameter $parameterName is null/empty")
