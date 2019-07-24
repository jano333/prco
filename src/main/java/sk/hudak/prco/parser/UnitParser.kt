package sk.hudak.prco.parser

import sk.hudak.prco.dto.UnitTypeValueCount
import java.util.*

/**
 * Created by jan.hudak on 9/21/2017.
 */
@FunctionalInterface
interface UnitParser {

    /**
     * Parse unit data base on product name.
     *
     * @param productName name of product
     * @return parsed unit data from product name
     */
    fun parseUnitTypeValueCount(productName: String): Optional<UnitTypeValueCount>
}
