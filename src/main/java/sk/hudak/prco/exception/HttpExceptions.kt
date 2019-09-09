package sk.hudak.prco.exception

import java.net.SocketTimeoutException

open class HttpStatusParserException : PrcoRuntimeException {

    val httpStatus: Int

    constructor(httpStatus: Int, message: String, cause: Throwable) :
            super(message, cause) {
        this.httpStatus = httpStatus
    }
}

class ProductNotFoundHttpParserException(message: String, cause: Throwable) :
        HttpStatusParserException(404, message, cause)

class HttpSocketTimeoutParserException(e: SocketTimeoutException) :
        PrcoRuntimeException("timeout", e)

class DefaultHttpParserException(message: String, cause: Throwable):
    PrcoRuntimeException(message, cause)