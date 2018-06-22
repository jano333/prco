package sk.hudak.prco.dto.group;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.DtoAble;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GroupListExtendedDto implements DtoAble {
    private Long id;
    private String name;
    private Long countOfProduct;
    private Map<EshopUuid, Long> countOfProductInEshop = new EnumMap<>(EshopUuid.class);
}
