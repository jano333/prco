package sk.hudak.prco.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.DtoAble;
import sk.hudak.prco.dto.group.GroupIdNameDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductFullDto implements DtoAble {
    private Date created;
    private Date updated;
    private Long id;
    private String url;
    private String name;
    private EshopUuid eshopUuid;
    private Unit unit;
    private BigDecimal unitValue;
    private Integer unitPackageCount;
    private BigDecimal priceForPackage;
    private BigDecimal priceForOneItemInPackage;
    private BigDecimal priceForUnit;
    private BigDecimal commonPrice;
    private Date lastTimeDataUpdated;
    private ProductAction productAction;
    private Date actionValidTo;
    private String productPictureUrl;
    private List<GroupIdNameDto> groupList = new ArrayList<>(1);
}
