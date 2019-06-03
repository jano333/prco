package sk.hudak.prco.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusInfo {
    private EshopUuid eshopUuid;
    private long countOfProductsWaitingToBeUpdated;
    private long countOfProductsAlreadyUpdated;
}
