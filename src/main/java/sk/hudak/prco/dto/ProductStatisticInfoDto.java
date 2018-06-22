package sk.hudak.prco.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;

import java.util.Map;

@Getter
@Setter
@ToString
public class ProductStatisticInfoDto {

    // only interested in
    long countOfAllProducts = -1;

    long countOfProductsNotInAnyGroup = -1;

    // key is group name, value is count of products
    private Map<String, Long> countProductInGroup;

    private Map<EshopUuid, EshopProductInfoDto> eshopProductInfo;

}
