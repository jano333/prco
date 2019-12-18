package sk.hudak.prco.manager.watchdog

import java.math.BigDecimal

interface WatchDogManager {

    fun startWatching(productUrl: String, maxPriceToBeInterestedIn: BigDecimal)

    fun collectAllUpdateAndSendEmail()
}
