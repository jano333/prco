package sk.hudak.prco.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.Unit;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class NotInterestedProductFullDto {
    private Date created;
    private Date updated;
    private Long id;
    private String url;
    private String name;
    private EshopUuid eshopUuid;
    private Unit unit;
    private BigDecimal unitValue;
    private Integer unitPackageCount;
}
