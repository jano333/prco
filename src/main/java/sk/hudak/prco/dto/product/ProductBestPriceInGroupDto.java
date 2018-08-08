package sk.hudak.prco.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.dto.DtoAble;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ToString
public class ProductBestPriceInGroupDto implements DtoAble {

    private Long id;
    private String url;
    private String name;
    private EshopUuid eshopUuid;
    private BigDecimal priceForPackage;
    private BigDecimal priceForOneItemInPackage;
    private BigDecimal priceForUnit;
    private BigDecimal commonPrice;

    // action info
    private ProductAction productAction;
    private Date actionValidTo;

    private int actionInPercentage;

}
