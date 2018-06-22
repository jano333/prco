package sk.hudak.prco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import sk.hudak.prco.api.Unit;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
public class UnitTypeValueCount {

    private Unit unit;
    private BigDecimal value;
    private Integer packageCount;
}
