package sk.hudak.prco.dto.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class ProductForUpdateData implements InternalDto {

    // povinne
    private String url;
    private EshopUuid eshopUuid;

    // nepovinne:
    private String name;
    private BigDecimal priceForPackage;

    private ProductAction productAction;
    private Date actionValidity;

    private String pictureUrl;

    public boolean isProductAvailable() {
        return name != null && priceForPackage != null;
    }
}
