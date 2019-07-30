package sk.hudak.prco.dto.internal;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;

import java.math.BigDecimal;
import java.util.Date;

public class ProductUpdateData implements InternallDto {

    // povinne
    private String url;
    private EshopUuid eshopUuid;

    // nepovinne:
    private String name;
    private BigDecimal priceForPackage;

    private ProductAction productAction;
    private Date actionValidity;

    private String pictureUrl;

    public ProductUpdateData(String url, EshopUuid eshopUuid) {
        this.url = url;
        this.eshopUuid = eshopUuid;
    }

    public ProductUpdateData(String url, EshopUuid eshopUuid, String name, BigDecimal priceForPackage, ProductAction productAction, Date actionValidity, String pictureUrl) {
        this.url = url;
        this.eshopUuid = eshopUuid;
        this.name = name;
        this.priceForPackage = priceForPackage;
        this.productAction = productAction;
        this.actionValidity = actionValidity;
        this.pictureUrl = pictureUrl;
    }

    public boolean isProductAvailable() {
        return name != null && priceForPackage != null;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceForPackage() {
        return priceForPackage;
    }

    public void setPriceForPackage(BigDecimal priceForPackage) {
        this.priceForPackage = priceForPackage;
    }

    public ProductAction getProductAction() {
        return productAction;
    }

    public void setProductAction(ProductAction productAction) {
        this.productAction = productAction;
    }

    public Date getActionValidity() {
        return actionValidity;
    }

    public void setActionValidity(Date actionValidity) {
        this.actionValidity = actionValidity;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public String toString() {
        return "ProductUpdateData{" +
                "url='" + url + '\'' +
                ", eshopUuid=" + eshopUuid +
                ", name='" + name + '\'' +
                ", priceForPackage=" + priceForPackage +
                ", productAction=" + productAction +
                ", actionValidity=" + actionValidity +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }
}
