package sk.hudak.prco.utils


import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import sk.hudak.prco.api.Unit
import sk.hudak.prco.exception.PrcoRuntimeException

import java.math.BigDecimal
import java.math.RoundingMode

@Component
class PriceCalculator {

    @Value("\${prco.server.rouding.count}")
    private val roundingScale: Int = 0

    /**
     * Cenu za balenie podelime poctom kusov v baleni.
     *
     * @param priceForPackage
     * @return
     */
    fun calculatePriceForOneItemInPackage(priceForPackage: BigDecimal, countOfItemInPackage: Int): BigDecimal {
        //TODO pocit iba ak je rozdny od jedna, lebo inak to je zbytocne teda je to rovnake ako cena za balenie
        return priceForPackage.divide(BigDecimal(countOfItemInPackage),
                roundingScale,
                RoundingMode.HALF_UP)
    }

    fun calculatePriceForUnit(unit: Unit, countOfUnit: BigDecimal, priceForOneItemInPackage: BigDecimal): BigDecimal {
        when (unit) {
            Unit.KUS -> return calculatePriceForUnit_KUS(countOfUnit, priceForOneItemInPackage)
            Unit.LITER -> return calculatePriceForUnit_LITER(countOfUnit, priceForOneItemInPackage)
            Unit.KILOGRAM -> return calculatePriceForUnit_KILOGRAM(countOfUnit, priceForOneItemInPackage)

            //TODO doriesit
            Unit.METER -> return calculatePriceForUnit_METER(countOfUnit, priceForOneItemInPackage)
            Unit.DAVKA -> return calculatePriceForUnit_DAVKA(countOfUnit, priceForOneItemInPackage)
            else -> throw PrcoRuntimeException("Not defined type $unit")
        }
    }

    /**
     * balenie (4x52ks)
     *
     * @param countOfUnit
     * @param priceForOneItemInPackage
     * @return
     */
    private fun calculatePriceForUnit_KUS(countOfUnit: BigDecimal, priceForOneItemInPackage: BigDecimal): BigDecimal {
        // napr.: 52
        // cena za jedno balenie(52 kusov)
        // kolko stoji 1 kus?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP)
    }

    private fun calculatePriceForUnit_LITER(countOfUnit: BigDecimal, priceForOneItemInPackage: BigDecimal): BigDecimal {
        // napr.: 0.7 litra
        // to znamena ze 0.7 litra stoji 'priceForOneItemInPackage'
        // kolko stoje 1 liter?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP)
    }

    private fun calculatePriceForUnit_METER(countOfUnit: BigDecimal, priceForOneItemInPackage: BigDecimal): BigDecimal {
        // napr.: 68 metrov
        // to znamena ze 68 metrov stoji 'priceForOneItemInPackage'
        // kolko stoje 1 meter ?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP)
    }

    private fun calculatePriceForUnit_KILOGRAM(countOfUnit: BigDecimal, priceForOneItemInPackage: BigDecimal): BigDecimal {
        // napr.: 0.75 kg
        // to znamena ze 0.75kg stoji 'priceForOneItemInPackage'
        // kolko stoje 1 kilo ?
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP)
    }

    private fun calculatePriceForUnit_DAVKA(countOfUnit: BigDecimal, priceForOneItemInPackage: BigDecimal): BigDecimal {
        return priceForOneItemInPackage.divide(countOfUnit,
                roundingScale,
                RoundingMode.HALF_UP)
    }

}
