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
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductNewData implements InternalDto {
    //TODO poprehadzovat na optional tie ktore mozu byt null

    private EshopUuid eshopUuid;
    private String url;
    private Optional<String> name;

    private Unit unit;
    private BigDecimal unitValue;
    private Integer unitPackageCount;

    private Optional<String> pictureUrl;

    /**
     * @return true, ak sa podarilo vsetko uspesne vyparsovat(vsetky parametre), inak false
     */
    public boolean isValid() {
        if (url == null || url.trim().length() < 1) {
            return false;
        }
        if (!name.isPresent() || (name.isPresent() && name.get().trim().length() < 1)) {
            return false;
        }
        return eshopUuid != null && unit != null && unitValue != null && unitPackageCount != null;
    }
}
