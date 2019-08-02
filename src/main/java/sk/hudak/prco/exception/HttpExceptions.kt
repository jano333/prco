package sk.hudak.prco.exception

import java.net.SocketTimeoutException


open class HttpStatusErrorPrcoException : PrcoRuntimeException {

    val httpStatus: Int

    constructor(httpStatus: Int, message: String) : super(message) {
        this.httpStatus = httpStatus
    }

    constructor(httpStatus: Int, message: String, cause: Throwable) :
            super(message, cause) {
        this.httpStatus = httpStatus
    }
}

class HttpErrorProductNotFoundPrcoException(message: String, cause: Throwable) :
        HttpStatusErrorPrcoException(404, message, cause)

class HttpSocketTimeoutPrcoRuntimeException(e: SocketTimeoutException) :
        PrcoRuntimeException("timeout", e)