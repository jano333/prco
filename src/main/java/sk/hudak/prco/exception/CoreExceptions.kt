package sk.hudak.prco.exception

open class PrcoRuntimeException : RuntimeException {

    constructor(message: String) :
            super(message)

    constructor(message: String, cause: Throwable) :
            super(message, cause)
}