package sk.hudak.prco.utils;

import sk.hudak.prco.exception.PrcoRuntimeException;

import java.math.BigDecimal;

import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

public class ConvertUtils {

    public static final String COMMA = ",";
    public static final String DOT = ".";

    private ConvertUtils() {
    }

    public static BigDecimal convertToBigDecimal(String value) {
        notNullNotEmpty(value, "value");
        try {
            return new BigDecimal(value.replace(COMMA, DOT));

        } catch (Exception e) {
            throw new PrcoRuntimeException("error while converting value " + value + " to " + BigDecimal.class.getName(), e);
        }
    }
}
