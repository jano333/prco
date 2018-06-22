package sk.hudak.prco.exception;

import lombok.Getter;

public class HttpErrorPrcoRuntimeException extends PrcoRuntimeException {

    @Getter
    private int httpStatus;

    public HttpErrorPrcoRuntimeException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpErrorPrcoRuntimeException(int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
