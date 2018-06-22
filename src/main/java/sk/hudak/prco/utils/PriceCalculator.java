package sk.hudak.prco.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.exception.PrcoRuntimeException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceCalculator {

    //TODO z property configu
    @Value("${prco.server.rouding.count}")
    private int roundingScale;

    /**
     * Cenu za balenie podelime poctom kusov v baleni.
     *
     * @param priceForPackage
     * @return
     */
    public BigDecimal calculatePriceForOneItemInPackage(BigDecimal priceForPackage, Integer countOfItemInPackage) {
        //TODO pocit iba ak je rozdny od jedna, lebo inak to je zbytocne teda je to rovnake ako cena za balenie
        return priceForPackage.divide(new BigDecimal(countOfItemInPackage),
                roundingScale,
                RoundingMode.HALF_UP);
    }

    public BigDecimal calculatePriceForUnit(Unit unit, BigDecimal countOfUnit, BigDecimal priceForOneItemInPackage) {
        switch (unit) {
            case KUS:
                return calculatePriceForUnit_KUS(countOfUnit, priceForOneItemInPackage);
            case LITER:
                return calculatePriceForUnit_LITER(countOfUnit, priceForOneItemInPackage);
            case KILOGRAM:
                return calculatePriceForUnit_KILOGRAM(countOfUnit, priceForOneItemInPackage);

            //TODO doriesit
            case METER:
                return calculatePriceForUnit_METER(countOfUnit, priceForOneItemInPackage);
            case DAVKA:
                return calculatePriceForUnit_DAVKA(countOfUnit, priceForOneItemInPackage);
            default:
                throw new PrcoRuntimeException("Not defined type " + unit);
        }
    }

    /**
     * balenie (4x52ks)
     *
     * @param countOfUnit
     * @param priceForOneItemInPackage
     * @return
     */
    private BigDecimal calculatePriceForUnit_KUS(BigDecimal countOfUnit, BigDecimal priceForOneItemInPackage) {
        // napr.: 52
        // cena za jedno balenie(52 kusov)
        // kolko stoji 1 kus?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePriceForUnit_LITER(BigDecimal countOfUnit, BigDecimal priceForOneItemInPackage) {
        // napr.: 0.7 litra
        // to znamena ze 0.7 litra stoji 'priceForOneItemInPackage'
        // kolko stoje 1 liter?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePriceForUnit_METER(BigDecimal countOfUnit, BigDecimal priceForOneItemInPackage) {
        // napr.: 68 metrov
        // to znamena ze 68 metrov stoji 'priceForOneItemInPackage'
        // kolko stoje 1 meter ?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePriceForUnit_KILOGRAM(BigDecimal countOfUnit, BigDecimal priceForOneItemInPackage) {
        // napr.: 0.75 kg
        // to znamena ze 0.75kg stoji 'priceForOneItemInPackage'
        // kolko stoje 1 kilo ?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePriceForUnit_DAVKA(BigDecimal countOfUnit, BigDecimal priceForOneItemInPackage) {
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP);
    }

}
