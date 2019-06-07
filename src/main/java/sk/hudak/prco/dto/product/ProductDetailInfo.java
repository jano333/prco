package sk.hudak.prco.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sk.hudak.prco.api.EshopUuid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailInfo {

    private Long id;
    private String url;
    private EshopUuid eshopUuid;

}
