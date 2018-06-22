package sk.hudak.prco.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ProductFilterUIDto implements Serializable {
    private EshopUuid eshopUuid;
    private Boolean onlyInAction;

    public ProductFilterUIDto() {
    }

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


