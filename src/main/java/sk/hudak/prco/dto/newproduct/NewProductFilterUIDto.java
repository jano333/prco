package sk.hudak.prco.dto.newproduct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.DtoAble;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewProductFilterUIDto implements DtoAble {

    private EshopUuid eshopUuid;
    private long maxCount = 10;
}
