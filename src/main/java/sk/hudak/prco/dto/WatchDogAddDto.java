package sk.hudak.prco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WatchDogAddDto {
    private String productUrl;
    private BigDecimal maxPriceToBeInterestedIn;
}
