package sk.hudak.prco.exception;

public class PrcoRuntimeException extends RuntimeException {

    public PrcoRuntimeException(String message) {
        super(message);
    }

    public PrcoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
