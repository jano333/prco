package sk.hudak.prco.manager;

import java.math.BigDecimal;

public interface WatchDogManager {

    void startWatching(String productUrl, BigDecimal maxPriceToBeInterestedIn);

    void collectAllUpdateAndSendEmail();
}
