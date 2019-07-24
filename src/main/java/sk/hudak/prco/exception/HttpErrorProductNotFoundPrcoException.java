package sk.hudak.prco.exception;

public class HttpErrorProductNotFoundPrcoException extends HttpErrorPrcoException {

    public HttpErrorProductNotFoundPrcoException(String message, Throwable cause) {
        super(404, message, cause);
    }
}
