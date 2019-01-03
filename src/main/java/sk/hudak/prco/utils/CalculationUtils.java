package sk.hudak.prco.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static sk.hudak.prco.utils.Validate.notNull;

public class CalculationUtils {

    private CalculationUtils() {
    }

    public static final int SCALE = 5;

    private static final BigDecimal VALUE_1000 = new BigDecimal(1000);


    public static BigDecimal recalculateToKilograms(BigDecimal grams) {
        notNull(grams, "grams");

        return grams.divide(VALUE_1000, SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal recalculateToLites(BigDecimal militers) {
        notNull(militers, "militers");

        return militers.divide(VALUE_1000, SCALE, RoundingMode.HALF_UP);
    }

    public static int calculatePercetage(BigDecimal actionValue, BigDecimal defaultValue) {
        notNull(actionValue, "actionValue");
        notNull(defaultValue, "defaultValue");

        BigDecimal divide = actionValue
                .multiply(BigDecimal.valueOf(100))
                .divide(defaultValue, SCALE, RoundingMode.HALF_UP);
        int i = divide.intValue();
        return 100 - i;
    }
}
