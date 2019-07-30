package sk.hudak.prco.dto.newproduct;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.DtoAble;

@NoArgsConstructor
@AllArgsConstructor
public class NewProductFilterUIDto implements DtoAble {

    private EshopUuid eshopUuid;
    private long maxCount = 10;

    public EshopUuid getEshopUuid() {
        return eshopUuid;
    }

    public void setEshopUuid(EshopUuid eshopUuid) {
        this.eshopUuid = eshopUuid;
    }

    public long getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(long maxCount) {
        this.maxCount = maxCount;
    }
}
