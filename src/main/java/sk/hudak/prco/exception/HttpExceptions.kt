package sk.hudak.prco.exception;

import lombok.Getter;

public class HttpErrorPrcoException extends PrcoRuntimeException {

    @Getter
    private final int httpStatus;

    public HttpErrorPrcoException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpErrorPrcoException(int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
