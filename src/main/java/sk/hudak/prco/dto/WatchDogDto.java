package sk.hudak.prco.dto;

import sk.hudak.prco.api.EshopUuid;

import java.math.BigDecimal;

public class WatchDogDto {

    private Long id;
    private String productUrl;
    private EshopUuid eshopUuid;
    private BigDecimal maxPriceToBeInterestedIn;

    @Override
    public String toString() {
        return "WatchDogDto{" +
                "id=" + id +
                ", productUrl='" + productUrl + '\'' +
                ", eshopUuid=" + eshopUuid +
                ", maxPriceToBeInterestedIn=" + maxPriceToBeInterestedIn +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public EshopUuid getEshopUuid() {
        return eshopUuid;
    }

    public void setEshopUuid(EshopUuid eshopUuid) {
        this.eshopUuid = eshopUuid;
    }

    public BigDecimal getMaxPriceToBeInterestedIn() {
        return maxPriceToBeInterestedIn;
    }

    public void setMaxPriceToBeInterestedIn(BigDecimal maxPriceToBeInterestedIn) {
        this.maxPriceToBeInterestedIn = maxPriceToBeInterestedIn;
    }
}
