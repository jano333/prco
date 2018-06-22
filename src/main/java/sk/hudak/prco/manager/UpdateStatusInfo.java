package sk.hudak.prco.manager;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;

@Getter
@Setter
@Builder
@ToString
public class UpdateStatusInfo {
    private long countOfProductsWaitingToBeUpdated;
    private long countOfProductsAlreadyUpdated;
    private EshopUuid eshopUuid;
}
