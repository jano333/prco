package sk.hudak.prco.utils;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.exception.RequiredParameterException;

import java.util.Collection;

public class Validate {

    private static final int ONE = 1;

    private Validate() {
        //no instance
    }

    public static void notNull(Object value, @NonNull String parameterName) {
        if (value == null) {
            throw new RequiredParameterException(parameterName);
        }
    }

    public static void notNullNotEmpty(String value, @NonNull String parameterName) {
        notNull(value, parameterName);
        if (StringUtils.isBlank(value)) {
            throw new RequiredParameterException(parameterName);
        }
    }

    public static void notNullNotEmpty(String[] value, @NonNull String parameterName) {
        notNull(value, parameterName);
        if (value.length == 0) {
            throw new RequiredParameterException(parameterName);
        }
    }

    public static void notNullNotEmpty(Collection<?> value, @NonNull String parameterName) {
        notNull(value, parameterName);
        if (value.isEmpty()) {
            throw new RequiredParameterException(parameterName);
        }
    }

    public static void atLeastOneIsNotNull(Long[] values, @NonNull String parameterName) {
        if (values == null || values.length == 0) {
            throw new PrcoRuntimeException("At least one parameter " + parameterName + " is required");
        }
    }

    public static void notNegativeAndNotZeroValue(int value, @NonNull String parameterName) {
        if (value < ONE) {
            throw new PrcoRuntimeException("Value is zero or negative for " + parameterName + " .");
        }
    }


}
