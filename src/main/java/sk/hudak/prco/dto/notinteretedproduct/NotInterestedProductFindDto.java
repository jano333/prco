package sk.hudak.prco.dto.notinteretedproduct;

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
public class NotInterestedProductFindDto implements DtoAble {
    private EshopUuid eshopUuid;
}
