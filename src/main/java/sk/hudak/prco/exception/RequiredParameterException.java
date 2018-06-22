package sk.hudak.prco.exception;

public class RequiredParameterException extends PrcoRuntimeException {

    public RequiredParameterException(String parameterName) {
        super("parameter " + parameterName + " is null/empty");
    }
}
