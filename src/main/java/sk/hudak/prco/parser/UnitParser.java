package sk.hudak.prco.parser;

import sk.hudak.prco.dto.UnitTypeValueCount;

import java.util.Optional;

/**
 * Created by jan.hudak on 9/21/2017.
 */
public interface UnitParser {

    Optional<UnitTypeValueCount> parseUnitTypeValueCount(String productName);
}
