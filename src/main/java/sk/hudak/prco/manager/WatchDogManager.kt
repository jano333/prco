package sk.hudak.prco.manager

import java.math.BigDecimal

interface WatchDogManager {

    fun startWatching(productUrl: String, maxPriceToBeInterestedIn: BigDecimal)

    fun collectAllUpdateAndSendEmail()
}
