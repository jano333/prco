package sk.hudak.prco.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.ProductAction;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@Builder
public class ProductUpdateDataDto {
    private Long id;

    private String url;
    private String name;
    private BigDecimal priceForPackage;

    private ProductAction productAction;
    private Date actionValidity;

    private String pictureUrl;
}
