package sk.hudak.prco.manager.updateprocess

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.dto.ProductUpdateDataDto
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.EventType
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.manager.updateprocess.UpdateProductDataListenerAdapter.Companion.EMPTY_INSTANCE
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.ExceptionHandlingRunnable
import sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe
import sk.hudak.prco.utils.ThreadUtils.sleepSafe
import java.util.*
import javax.annotation.PostConstruct

@Primary
@Component
class NgUpdateProductDataManagerImpl(private val htmlParser: HtmlParser,
                                     private val internalTxService: InternalTxService,
                                     private val eshopTaskManager: EshopTaskManager,
                                     private val mapper: PrcoOrikaMapper,
                                     private val updateProductErrorHandler: UpdateProductErrorHandler,
                                     private val prcoObservable: PrcoObservable)
    : UpdateProductDataManager, PrcoObserver {

    companion object {
        val log = LoggerFactory.getLogger(NgUpdateProductDataManagerImpl::class.java)!!
    }

    @PostConstruct
    fun init() {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        log.debug("updateObservable.update ${source?.javaClass?.simpleName} - event $event")
    }

    override fun updateProductData(productId: Long) {
        // vyhladam product na update v DB na zaklade id
        val productForUpdate: ProductDetailInfo = internalTxService.findProductForUpdate(productId)

        val eshopUuid = productForUpdate.eshopUuid

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable() {
                log.debug(">> updateProductData eshop $eshopUuid, productId $productId")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                updateProductDataErrorWrapper(productForUpdate, UpdateProductDataListenerAdapter.LOG_INSTANCE)
                //ignorujem response lebo je to len jeden

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(e: Exception) {
                handleExceptionUpdateProducts(e, eshopUuid)
            }

            override fun doInFinally() {
                log.debug("<< updateProductData eshop $eshopUuid, productId $productId")
                prcoObservable.notify(object : CoreEvent(EventType.UPDATE_PRODUCT) {})
            }
        })
    }

    override fun updateProductDataForEachProductInEachEshop(listener: UpdateProductDataListener) {
        EshopUuid.values().forEach {
            updateProductDataForEachProductInEshop(it, listener)
            // kazdy dalsi spusti s 1 sekundovym oneskorenim
            sleepSafe(1)
        }
    }

    override fun updateProductDataForEachProductInEshop(eshopUuid: EshopUuid, listener: UpdateProductDataListener) {

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable() {
                log.debug(">> updateProductDataForEachProductInEshop eshop $eshopUuid")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                var productForUpdate = internalTxService.findProductForUpdate(eshopUuid, eshopUuid.olderThanInHours)
                // until we have anything for update
                loop@
                while (productForUpdate != null) {

                    notifyUpdateListener(eshopUuid, listener)

                    val continueStatus = updateProductDataErrorWrapper(productForUpdate, EMPTY_INSTANCE)

                    when (continueStatus) {
                        ContinueStatus.STOP_PROCESSING_NEXT_ONE -> {
                            break@loop
                        }
                        ContinueStatus.CONTINUE_TO_NEXT_ONE_OK,
                        ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR -> {
                            sleepRandomSafe()
                            productForUpdate = internalTxService.findProductForUpdate(eshopUuid, eshopUuid.olderThanInHours)
                        }
                    }
                }

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(e: Exception) {
                handleExceptionUpdateProducts(e, eshopUuid)
            }

            override fun doInFinally() {
                log.debug("<< updateProductDataForEachProductInEshop eshop $eshopUuid")
                prcoObservable.notify(object : CoreEvent(EventType.UPDATE_PRODUCT) {})
            }
        })
    }

    override fun updateProductDataForEachProductInGroup(groupId: Long, listener: UpdateProductDataListener) {
        internalTxService.findProductForUpdateInGroup(groupId)
                .forEach { eshopUuid, productIds ->
                    // spusti update danych produktov v ehope
                    updateProductData(eshopUuid, productIds, listener)
                    // pre kazdy dalsi eshop pockaj so spustenim 2 sekundy
                    sleepSafe(2)
                }
    }

    override fun updateProductDataForEachProductNotInAnyGroup(listener: UpdateProductDataListener) {
        internalTxService.findProductsForUpdateWhichAreNotInAnyGroup()
                .forEach { eshopUuid, productIds ->
                    // spusti update danych produktov v ehope
                    updateProductData(eshopUuid, productIds, listener)
                    // pre kazdy dalsi eshop pockaj so spustenim 2 sekundy
                    sleepSafe(2)
                }
    }

    private fun updateProductData(eshopUuid: EshopUuid, productForUpdateIds: List<Long>, listener: UpdateProductDataListener) {
        if (productForUpdateIds.isEmpty()) {
            log.debug("none product ids for eshop $eshopUuid")
            return
        }

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable() {
                log.debug(">> updateProductData eshop $eshopUuid, product for update ids count ${productForUpdateIds.size}")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                var countOfProductsAlreadyUpdated: Long = 0
                var countOfProductsWaitingToBeUpdated = productForUpdateIds.size.toLong()

                listener.onUpdateStatus(UpdateStatusInfo(eshopUuid, countOfProductsWaitingToBeUpdated, countOfProductsAlreadyUpdated))

                loop@
                for (productForUpdateId in productForUpdateIds) {
                    // read detail about product
                    var productForUpdate = internalTxService.findProductForUpdate(productForUpdateId)

                    // handle update proces
                    val continueStatus = updateProductDataErrorWrapper(productForUpdate, EMPTY_INSTANCE)

                    when (continueStatus) {
                        ContinueStatus.STOP_PROCESSING_NEXT_ONE -> {
                            break@loop
                        }

                        ContinueStatus.CONTINUE_TO_NEXT_ONE_OK -> {
                            countOfProductsAlreadyUpdated++
                            countOfProductsWaitingToBeUpdated--
                            //TODO iba ak nie je posledny tak toto rob:
                            sleepRandomSafe()
                        }

                        ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR -> {
                            countOfProductsWaitingToBeUpdated--
                            //TODO iba ak nie je posledny tak toto rob:
                            sleepRandomSafe()
                        }
                    }

                    listener.onUpdateStatus(UpdateStatusInfo(eshopUuid, countOfProductsWaitingToBeUpdated, countOfProductsAlreadyUpdated))

                }// koniec for cyklu

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(e: Exception) {
                handleExceptionUpdateProducts(e, eshopUuid)
            }

            override fun doInFinally() {
                log.debug("<< updateProductData eshop $eshopUuid, productForUpdateIds count ${productForUpdateIds.size}")
            }
        })
    }

    private fun handleExceptionUpdateProducts(e: Exception, eshopUuid: EshopUuid) {
        when (e) {
            is UpdateProductException -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(eshopUuid, true)
                //TODO impl
//                logErrorParsingProductUrls(e.eshopUuid, e.searchKeyWord, e)
            }

            is GettingProductForUpdateException -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(eshopUuid, true)
                // FIXME log to DB? ide o internu chybu
            }
            else -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(eshopUuid, true)
            }
        }
    }


    private fun updateProductDataErrorWrapper(productForUpdate: ProductDetailInfo, listener: UpdateProductDataListener): ContinueStatus {
        return try {
            updateProductData(productForUpdate, listener)

        } catch (e: Exception) {
            throw UpdateProductException(productForUpdate, e)
        }
    }

    private fun updateProductData(productForUpdate: ProductDetailInfo, listener: UpdateProductDataListener): ContinueStatus {
        val eshopUuid = productForUpdate.eshopUuid
        val productId = productForUpdate.id
        val productUrl = productForUpdate.url

        eshopTaskManager.sleepIfNeeded(eshopUuid)

        if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
            eshopTaskManager.markTaskAsStopped(eshopUuid)
            return ContinueStatus.STOP_PROCESSING_NEXT_ONE
        }

        // parsujem data
        val updateData: ProductUpdateData = try {
            htmlParser.parseProductUpdateData(productUrl)

        } catch (e: Exception) {
            //TODO premenovat na updateParserErrorHandler
            updateProductErrorHandler.processParsingError(e, productForUpdate)
            return ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
        }

        // if not available -> continue to next one
        if (!updateData.isProductAvailable) {
            internalTxService.markProductAsUnavailable(productId)
            return ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
        }

        // check duplicity
        //FIXME zrusit optional
        val existingProductIdOpt = internalTxService.getProductWithUrl(productUrl, productId)
        if (existingProductIdOpt.isPresent) {
            log.debug("exist another product ${existingProductIdOpt.get()} with url $productUrl")
            log.debug("product $productId will be removed, url $productUrl")
        }

        val productIdToBeUpdated = if (existingProductIdOpt.isPresent) existingProductIdOpt.get() else productId

        //FIXME premapovanie cez sk.hudak.prco mapper nie takto rucne, nech mam na jednom mieste tie preklapacky...
        internalTxService.updateProduct(ProductUpdateDataDto(
                productIdToBeUpdated,
                updateData.url,
                updateData.name,
                updateData.priceForPackage,
                updateData.productAction,
                updateData.actionValidity,
                updateData.pictureUrl))

        // remove duplicity product with old URL
        if (existingProductIdOpt.isPresent) {
            internalTxService.removeProduct(productId)
        }
        // notify listener
        notifyUpdateListener(eshopUuid, listener)

        return ContinueStatus.CONTINUE_TO_NEXT_ONE_OK
    }

    private enum class ContinueStatus {
        CONTINUE_TO_NEXT_ONE_OK,
        CONTINUE_TO_NEXT_ONE_ERROR,
        STOP_PROCESSING_NEXT_ONE
    }

    private fun notifyUpdateListener(eshopUuid: EshopUuid, listener: UpdateProductDataListener) {
        //TODO volat len ak to nie je empty implementacia

        when (listener) {
            //TODO overit ci to je ok...
            EMPTY_INSTANCE -> {

            }
            else -> {
                listener.onUpdateStatus(
                        mapper.map(internalTxService.getStatisticForUpdateForEshop(eshopUuid, eshopUuid.olderThanInHours),
                                UpdateStatusInfo::class.java)
                )
            }
        }
    }
}

class UpdateProductException(productForUpdate: ProductDetailInfo, e: Exception) :
        PrcoRuntimeException("error while updating product id ${productForUpdate.id} URL ${productForUpdate.url}", e)

class GettingProductForUpdateException(eshopUuid: EshopUuid, e: Exception) :
        PrcoRuntimeException("error while retrieving next product from DB to update for eshop $eshopUuid", e)
