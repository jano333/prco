package sk.hudak.prco.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.DtoAble;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductFilterUIDto implements DtoAble {

    private EshopUuid eshopUuid;
    private Boolean onlyInAction;

    public ProductFilterUIDto(Boolean onlyInAction) {
        this.onlyInAction = onlyInAction;
    }

    public ProductFilterUIDto(EshopUuid eshopUuid) {
        this.eshopUuid = eshopUuid;
    }

    public ProductFilterUIDto(EshopUuid eshopUuid, Boolean onlyInAction) {
        this.eshopUuid = eshopUuid;
        this.onlyInAction = onlyInAction;
    }
}


