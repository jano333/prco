package sk.hudak.prco.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.DtoAble;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductAddingToGroupDto implements DtoAble {
    private Long id;
    private String url;
    private String name;
    private EshopUuid eshopUuid;
    private String productPictureUrl;

    private Long groupId;
}
