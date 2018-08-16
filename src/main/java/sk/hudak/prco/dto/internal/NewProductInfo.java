package sk.hudak.prco.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.Unit;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class NewProductInfo implements InternalDto {

    private String url;
    private String name;
    private EshopUuid eshopUuid;

    private Unit unit;
    private BigDecimal unitValue;
    private Integer unitPackageCount;

    private String pictureUrl;

    /**
     * @return true, ak sa podarilo vsetko uspesne vyparsovat(vsetky parametre), inak false
     */
    public boolean isValid() {
        if (url == null || url.trim().length() < 1) {
            return false;
        }
        if (name == null || name.trim().length() < 1) {
            return false;
        }
        return eshopUuid != null && unit != null && unitValue != null && unitPackageCount != null;
    }
}
