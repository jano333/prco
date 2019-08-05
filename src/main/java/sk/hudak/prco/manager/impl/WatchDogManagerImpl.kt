package sk.hudak.prco.manager.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.dto.WatchDogAddCustomDto
import sk.hudak.prco.dto.WatchDogDto
import sk.hudak.prco.dto.WatchDogNotifyUpdateDto
import sk.hudak.prco.manager.WatchDogManager
import sk.hudak.prco.parser.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe
import sk.hudak.prco.utils.ThreadUtils.sleepSafe
import java.math.BigDecimal
import java.util.*

@Component
class WatchDogManagerImpl(private val internalTxService: InternalTxService,
                          private val eshopTaskManager: EshopTaskManager,
                          private val parser: HtmlParser)
    : WatchDogManager {

    companion object {
        val log = LoggerFactory.getLogger(WatchDogManagerImpl::class.java)!!
    }

    override fun startWatching(productUrl: String, maxPriceToBeInterestedIn: BigDecimal) {
        internalTxService.addNewProductToWatch(WatchDogAddCustomDto(productUrl, maxPriceToBeInterestedIn))
    }

    override fun collectAllUpdateAndSendEmail() {
        val notificationList = internalTxService.findProductsForWatchDog()
        if (notificationList.isEmpty()) {
            log.debug("nothing to found ")
            return
        }

        val productIdToBeNotified = ArrayList<WatchDogNotifyUpdateDto>()
        for ((eshopUuid, watchDogProductsInEshop) in notificationList) {

            collect(productIdToBeNotified, eshopUuid, watchDogProductsInEshop)
        }

        // wait util all task are finished
        var isAnyTakRunning = eshopTaskManager.isAnyTaskRunning
        log.debug("is any task running: {}", isAnyTakRunning)

        while (isAnyTakRunning) {
            sleepSafe(5)
            isAnyTakRunning = eshopTaskManager.isAnyTaskRunning
            log.debug("is any task running: {}", isAnyTakRunning)
        }

        if (!productIdToBeNotified.isEmpty()) {
            log.debug("count of product {}", productIdToBeNotified.size)
            internalTxService.notifyByEmail(productIdToBeNotified)
        } else {
            log.debug("none product to be notified")
        }
    }

    private fun collect(productIdToBeNotified: MutableList<WatchDogNotifyUpdateDto>,
                        eshopUuid: EshopUuid,
                        products: List<WatchDogDto>) {

        eshopTaskManager.markTaskAsRunning(eshopUuid)

        eshopTaskManager.submitTask(eshopUuid, Runnable {
            var finishedWithError = false
            try {
                for (watchDogDto in products) {
                    val result = Optional.of(parser.parseProductUpdateData(watchDogDto.productUrl!!))
                    sleepRandomSafe()
                    if (!result.isPresent) {
                        continue
                    }
                    val productUpdateData = result.get()
                    // compare price
                    val currentPrice = productUpdateData.priceForPackage
                    val watchDogDtoMaxPriceToBeInterestedIn = watchDogDto.maxPriceToBeInterestedIn
                    val i = currentPrice!!.compareTo(watchDogDtoMaxPriceToBeInterestedIn!!)
                    if (i < 0) {
                        productIdToBeNotified.add(createWatchDogNotifyUpdateDto(watchDogDto, productUpdateData))
                        //TODO msg
                        log.debug("adding product ")
                    } else {
                        log.debug("product is not needed to be notify, current/watchdog: {}/{}", currentPrice, watchDogDtoMaxPriceToBeInterestedIn)
                    }
                }


            } catch (e: Exception) {
                //TODO error
                log.error("error while updating product data", e)
                finishedWithError = true

            } finally {
                eshopTaskManager.markTaskAsFinished(eshopUuid, finishedWithError)
            }
        });
    }


    private fun createWatchDogNotifyUpdateDto(watchDogDto: WatchDogDto, productUpdateData: ProductUpdateData): WatchDogNotifyUpdateDto {
        val result = WatchDogNotifyUpdateDto()
        result.id = watchDogDto.id
        result.eshopUuid = watchDogDto.eshopUuid
        result.productUrl = watchDogDto.productUrl
        result.maxPriceToBeInterestedIn = watchDogDto.maxPriceToBeInterestedIn
        result.productName = productUpdateData.name
        result.currentPrice = productUpdateData.priceForPackage
        return result
    }
}
