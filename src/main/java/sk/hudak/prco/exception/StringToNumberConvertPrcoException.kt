package sk.hudak.prco.exception;

import lombok.Getter;

import java.math.BigDecimal;

public class StringToNumberConvertPrcoException extends PrcoRuntimeException {

    @Getter
    private String value;

    public StringToNumberConvertPrcoException(String value, Throwable cause) {
        super("error while converting value " + value + " to " + BigDecimal.class.getName(), cause);
        this.value = value;
    }
}
