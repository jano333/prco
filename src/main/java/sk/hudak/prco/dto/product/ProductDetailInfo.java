package sk.hudak.prco.dto.product;

import lombok.Getter;
import lombok.Setter;
import sk.hudak.prco.api.EshopUuid;

@Getter
@Setter
public class ProductDetailInfo {

    private Long id;
    private String url;
    private EshopUuid eshopUuid;

}
