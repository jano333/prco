package sk.hudak.prco.dto;

import java.math.BigDecimal;

public class WatchDogAddDto {

    private String productUrl;
    private BigDecimal maxPriceToBeInterestedIn;

    public WatchDogAddDto() {
    }

    public WatchDogAddDto(String productUrl, BigDecimal maxPriceToBeInterestedIn) {
        this.productUrl = productUrl;
        this.maxPriceToBeInterestedIn = maxPriceToBeInterestedIn;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public BigDecimal getMaxPriceToBeInterestedIn() {
        return maxPriceToBeInterestedIn;
    }

    public void setMaxPriceToBeInterestedIn(BigDecimal maxPriceToBeInterestedIn) {
        this.maxPriceToBeInterestedIn = maxPriceToBeInterestedIn;
    }

    @Override
    public String toString() {
        return "WatchDogAddDto{" +
                "productUrl='" + productUrl + '\'' +
                ", maxPriceToBeInterestedIn=" + maxPriceToBeInterestedIn +
                '}';
    }
}
