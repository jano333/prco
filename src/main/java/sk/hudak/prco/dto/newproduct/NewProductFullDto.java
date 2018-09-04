package sk.hudak.prco.dto.newproduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.DtoAble;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NewProductFullDto implements DtoAble {

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
    private Boolean confirmValidity;
    private String pictureUrl;
}
