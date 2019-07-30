package sk.hudak.prco.dto.group;

import lombok.NonNull;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.DtoAble;

public class GroupFilterDto implements DtoAble {
    private Long[] ids;
    private String name;
    private EshopUuid eshopOnly;
    private EshopUuid[] eshopsToSkip;

    public GroupFilterDto() {
    }

    public GroupFilterDto(Long id) {
        this((String) null, id);
    }

    public GroupFilterDto(String name) {
        this(name, null);
    }

    public GroupFilterDto(String name, Long... ids) {
        this.name = name;
        this.ids = ids;
    }

    public GroupFilterDto(EshopUuid eshopOnly, Long... ids) {
        this.eshopOnly = eshopOnly;
        this.ids = ids;
    }

    public GroupFilterDto(@NonNull Long id, EshopUuid... eshopsToSkip) {
        this.ids = new Long[]{id};
        this.eshopsToSkip = eshopsToSkip;
    }

    public Long[] getIds() {
        return ids;
    }

    public String getName() {
        return name;
    }

    public EshopUuid getEshopOnly() {
        return eshopOnly;
    }

    public EshopUuid[] getEshopsToSkip() {
        return eshopsToSkip;
    }
}
