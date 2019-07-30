package sk.hudak.prco.dto;

import sk.hudak.prco.api.EshopUuid;

import java.util.Map;

public class ProductStatisticInfoDto {

    // only interested in
    long countOfAllProducts = -1;

    long countOfProductsNotInAnyGroup = -1;

    // key is group name, value is count of products
    private Map<String, Long> countProductInGroup;

    private Map<EshopUuid, EshopProductInfoDto> eshopProductInfo;

    public long getCountOfAllProducts() {
        return countOfAllProducts;
    }

    public void setCountOfAllProducts(long countOfAllProducts) {
        this.countOfAllProducts = countOfAllProducts;
    }

    public long getCountOfProductsNotInAnyGroup() {
        return countOfProductsNotInAnyGroup;
    }

    public void setCountOfProductsNotInAnyGroup(long countOfProductsNotInAnyGroup) {
        this.countOfProductsNotInAnyGroup = countOfProductsNotInAnyGroup;
    }

    public Map<String, Long> getCountProductInGroup() {
        return countProductInGroup;
    }

    public void setCountProductInGroup(Map<String, Long> countProductInGroup) {
        this.countProductInGroup = countProductInGroup;
    }

    public Map<EshopUuid, EshopProductInfoDto> getEshopProductInfo() {
        return eshopProductInfo;
    }

    public void setEshopProductInfo(Map<EshopUuid, EshopProductInfoDto> eshopProductInfo) {
        this.eshopProductInfo = eshopProductInfo;
    }

    @Override
    public String toString() {
        return "ProductStatisticInfoDto{" +
                "countOfAllProducts=" + countOfAllProducts +
                ", countOfProductsNotInAnyGroup=" + countOfProductsNotInAnyGroup +
                ", countProductInGroup=" + countProductInGroup +
                ", eshopProductInfo=" + eshopProductInfo +
                '}';
    }
}
