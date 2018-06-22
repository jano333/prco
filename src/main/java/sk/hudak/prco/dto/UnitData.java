package sk.hudak.prco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import sk.hudak.prco.api.Unit;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
public class UnitData {
    private Unit unit;
    private BigDecimal unitValue;
    private Integer unitPackageCount;
}
