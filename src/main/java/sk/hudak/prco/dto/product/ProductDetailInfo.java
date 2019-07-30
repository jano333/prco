package sk.hudak.prco.dto.product;

import sk.hudak.prco.api.EshopUuid;

public class ProductDetailInfo {

    private Long id;
    private String url;
    private EshopUuid eshopUuid;

    public ProductDetailInfo() {
    }

    public ProductDetailInfo(Long id, String url, EshopUuid eshopUuid) {
        this.id = id;
        this.url = url;
        this.eshopUuid = eshopUuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public EshopUuid getEshopUuid() {
        return eshopUuid;
    }

    public void setEshopUuid(EshopUuid eshopUuid) {
        this.eshopUuid = eshopUuid;
    }
}
