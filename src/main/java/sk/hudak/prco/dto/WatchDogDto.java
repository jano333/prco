package sk.hudak.prco.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class WatchDogDto {
    private Long id;
    private String productUrl;
    private EshopUuid eshopUuid;
    private BigDecimal maxPriceToBeInterestedIn;
}
