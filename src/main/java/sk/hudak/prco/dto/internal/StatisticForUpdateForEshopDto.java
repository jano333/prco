package sk.hudak.prco.dto.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;

@Getter
@Setter
@ToString
@Builder
public class StatisticForUpdateForEshopDto implements InternalDto {
    private long countOfProductsWaitingToBeUpdated;
    private long countOfProductsAlreadyUpdated;
    private EshopUuid eshopUuid;
}
