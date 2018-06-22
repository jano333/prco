package sk.hudak.prco.dto.newproduct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.Unit;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
public class NewProductInfoDetail {

    private Long id;
    private Date created;
    private Date updated;
    private String url;
    private String name;
    private EshopUuid eshopUuid;

    private Unit unit;
    private BigDecimal unitValue;
    private Integer unitPackageCount;
    private Boolean valid;
}
