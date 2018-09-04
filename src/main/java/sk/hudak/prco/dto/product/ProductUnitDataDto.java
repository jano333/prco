package sk.hudak.prco.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.DtoAble;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ProductUnitDataDto implements DtoAble {
    private Long id;

    private String name;
    private Unit unit;
    private BigDecimal unitValue;
    private Integer unitPackageCount;
}
