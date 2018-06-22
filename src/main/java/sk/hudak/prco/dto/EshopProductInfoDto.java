package sk.hudak.prco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class EshopProductInfoDto {
    private long countOfAllProduct;
    private long countOfAlreadyUpdated;
}
