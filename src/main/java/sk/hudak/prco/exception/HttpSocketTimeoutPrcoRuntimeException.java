package sk.hudak.prco.exception;

import java.net.SocketTimeoutException;

public class HttpSocketTimeoutPrcoRuntimeException extends PrcoRuntimeException {

    public HttpSocketTimeoutPrcoRuntimeException(SocketTimeoutException e) {
        super("timeout", e);
    }

}
