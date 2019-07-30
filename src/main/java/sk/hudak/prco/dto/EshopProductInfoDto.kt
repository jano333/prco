package sk.hudak.prco.dto;

public class EshopProductInfoDto implements DtoAble {

    private long countOfAllProduct;
    private long countOfAlreadyUpdated;

    public EshopProductInfoDto(long countOfAllProduct, long countOfAlreadyUpdated) {
        this.countOfAllProduct = countOfAllProduct;
        this.countOfAlreadyUpdated = countOfAlreadyUpdated;
    }

    @Override
    public String toString() {
        return "EshopProductInfoDto{" +
                "countOfAllProduct=" + countOfAllProduct +
                ", countOfAlreadyUpdated=" + countOfAlreadyUpdated +
                '}';
    }

    public long getCountOfAllProduct() {
        return countOfAllProduct;
    }

    public long getCountOfAlreadyUpdated() {
        return countOfAlreadyUpdated;
    }
}
