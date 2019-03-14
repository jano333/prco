package sk.hudak.prco.dto.product;

import lombok.Getter;
import lombok.Setter;
import sk.hudak.prco.api.GroupProductKeywords;

@Getter
@Setter
public class ProductNotInAnyGroupDto extends ProductFullDto {
    private GroupProductKeywords keywords;
}
