package sk.hudak.prco.parser;

import sk.hudak.prco.dto.UnitTypeValueCount;

import java.util.Optional;

/**
 * Created by jan.hudak on 9/21/2017.
 */
@FunctionalInterface
public interface UnitParser {

    /**
     * Parse unit data base on product name.
     *
     * @param productName name of product
     * @return parsed unit data from product name
     */
    Optional<UnitTypeValueCount> parseUnitTypeValueCount(String productName);
}
