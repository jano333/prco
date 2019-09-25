package sk.hudak.prco.manager.updateprocess

import org.slf4j.LoggerFactory
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
import sk.hudak.prco.task.SingleContext
import sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe
import sk.hudak.prco.utils.ThreadUtils.sleepSafe
import java.util.*

@Component
class UpdateProductDataManagerImpl(private val htmlParser: HtmlParser,
                                   private val internalTxService: InternalTxService,
                                   private val eshopTaskManager: EshopTaskManager,
                                   private val mapper: PrcoOrikaMapper,
                                   private val updateProductErrorHandler: UpdateProductErrorHandler,
                                   private val prcoObservable: PrcoObservable)
    : UpdateProductDataManager, PrcoObserver {

    companion object {
        val log = LoggerFactory.getLogger(UpdateProductDataManagerImpl::class.java)!!
    }

    init {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        //TODO vypis?
        log.debug("updateObservable.update ${source?.javaClass?.simpleName} - event $event")
    }

    override fun updateProductDataForEachProductInEachEshop(listener: UpdateProductDataListener) {
        EshopUuid.values().forEach {
            updateProductDataForEachProductInEshop(it, listener)
            // kazdy dalsi spusti s 1 sekundovym oneskorenim
            sleepSafe(1)
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

    override fun updateProductDataForEachProductInGroup(groupId: Long, listener: UpdateProductDataListener) {
        internalTxService.findProductForUpdateInGroup(groupId)
                .forEach { eshopUuid, productIds ->
                    // spusti update danych produktov v ehope
                    updateProductData(eshopUuid, productIds, listener)
                    // pre kazdy dalsi eshop pockaj so spustenim 2 sekundy
                    sleepSafe(2)
                }
    }

    override fun updateProductData(productId: Long) {
        // vyhladam product na update v DB na zaklade id
        val productForUpdate: ProductDetailInfo = internalTxService.findProductForUpdate(productId)

        val eshopUuid = productForUpdate.eshopUuid

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable(context: SingleContext) {
                log.debug(">> updateProductData eshop $eshopUuid, productId $productId")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                updateProductDataErrorWrapper(productForUpdate, UpdateProductDataListenerAdapter.LOG_INSTANCE)
                //ignorujem response lebo je to len jeden

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(context: SingleContext, e: Exception) {
                handleExceptionUpdateProducts(e, eshopUuid)
            }

            override fun doInFinally(context: SingleContext) {
                log.debug("<< updateProductData eshop $eshopUuid, productId $productId")
                //TODO event poriesit
                prcoObservable.notify(object : CoreEvent(EventType.UPDATE_PRODUCT) {})
            }
        })
    }

    override fun updateProductDataForEachProductInEshop(eshopUuid: EshopUuid, listener: UpdateProductDataListener) {

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable(context: SingleContext) {
                log.debug(">> updateProductDataForEachProductInEshop eshop $eshopUuid")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                var productForUpdate: ProductDetailInfo? = internalTxService.findProductForUpdate(eshopUuid, eshopUuid.olderThanInHours)
                var finishedWithError = false
                // until we have anything for update
                loop@
                while (productForUpdate != null) {

                    notifyUpdateListener(eshopUuid, listener)

                    val continueStatus = updateProductDataErrorWrapper(productForUpdate, EMPTY_INSTANCE)

                    when (continueStatus) {
                        ContinueUpdateStatus.STOP_PROCESSING_NEXT_ONE_OK -> {
                            break@loop
                        }
                        ContinueUpdateStatus.STOP_PROCESSING_NEXT_ONE_ERROR -> {
                            finishedWithError = true
                            break@loop
                        }
                        ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_OK,
                        ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_ERROR -> {
                            sleepRandomSafe()
                            productForUpdate = internalTxService.findProductForUpdate(eshopUuid, eshopUuid.olderThanInHours)
                        }
                    }
                }

                eshopTaskManager.markTaskAsFinished(eshopUuid, finishedWithError)
            }

            override fun handleException(context: SingleContext, e: Exception) {
                handleExceptionUpdateProducts(e, eshopUuid)
            }

            override fun doInFinally(context: SingleContext) {
                log.debug("<< updateProductDataForEachProductInEshop eshop $eshopUuid")
                //TODO event
                prcoObservable.notify(object : CoreEvent(EventType.UPDATE_PRODUCT) {})
            }
        })
    }

    private fun updateProductData(eshopUuid: EshopUuid, productForUpdateIds: List<Long>, listener: UpdateProductDataListener) {
        if (productForUpdateIds.isEmpty()) {
            log.debug("none product ids for eshop $eshopUuid")
            return
        }

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable(context: SingleContext) {
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
                        ContinueUpdateStatus.STOP_PROCESSING_NEXT_ONE_OK -> {
                            break@loop
                        }

                        ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_OK -> {
                            countOfProductsAlreadyUpdated++
                            countOfProductsWaitingToBeUpdated--
                            //TODO iba ak nie je posledny tak toto rob:
                            sleepRandomSafe()
                        }

                        ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_ERROR -> {
                            countOfProductsWaitingToBeUpdated--
                            //TODO iba ak nie je posledny tak toto rob:
                            sleepRandomSafe()
                        }
                    }

                    //TODO zrusit a dat cez observable....
                    listener.onUpdateStatus(UpdateStatusInfo(eshopUuid, countOfProductsWaitingToBeUpdated, countOfProductsAlreadyUpdated))

                }// koniec for cyklu

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(context: SingleContext, e: Exception) {
                handleExceptionUpdateProducts(e, eshopUuid)
            }

            override fun doInFinally(context: SingleContext) {
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


    private fun updateProductDataErrorWrapper(productForUpdate: ProductDetailInfo, listener: UpdateProductDataListener): ContinueUpdateStatus {
        return try {
            updateProductData(productForUpdate, listener)

        } catch (e: Exception) {
            throw UpdateProductException(productForUpdate, e)
        }
    }

    private fun updateProductData(productForUpdate: ProductDetailInfo, listener: UpdateProductDataListener): ContinueUpdateStatus {
        eshopTaskManager.sleepIfNeeded(productForUpdate.eshopUuid)

        if (eshopTaskManager.isTaskShouldStopped(productForUpdate.eshopUuid)) {
            eshopTaskManager.markTaskAsStopped(productForUpdate.eshopUuid)
            return ContinueUpdateStatus.STOP_PROCESSING_NEXT_ONE_OK
        }

        // parsujem update data pre danu URL
        val updateData: ProductUpdateData = try {
            htmlParser.parseProductUpdateData(productForUpdate.url)

        } catch (e: Exception) {
            return updateProductErrorHandler.processParsingError(e, productForUpdate)
        }

        // no redirect -> product url was not changed
        if (!updateData.redirect) {
            // if not available -> continue to next one
            if (!updateData.isProductAvailable) {
                // mark it as unavailable
                internalTxService.markProductAsUnavailable(productForUpdate.id)
                return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_ERROR
            }
            // update product data
            internalTxService.updateProduct(updateData.toProductUpdateDataDto(productForUpdate.id))
            return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_OK
        }

        // redirect -> url was changed, try to find product with new URL
        var newProductForUpdate: ProductDetailInfo? = internalTxService.getProductForUpdateByUrl(updateData.url)

        // product with new URL don't exist
        if (newProductForUpdate == null) {
            log.debug("product with redirect URL ${updateData.url} not exist")
            // if not available -> update only URL
            if (!updateData.isProductAvailable) {
                // update only URL of product
                internalTxService.updateProductUrl(productForUpdate.id, updateData.url)
                // mark it as unavailable
                internalTxService.markProductAsUnavailable(productForUpdate.id)
                return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_ERROR
            }

            // update product data
            internalTxService.updateProduct(updateData.toProductUpdateDataDto(productForUpdate.id))
            return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_OK
        }

        // product with new URL exist
        log.debug("product with redirect URL: ${updateData.url} exist, id: ${newProductForUpdate.id}")

        // remove product with old URL
        internalTxService.removeProduct(productForUpdate.id)

        // if not available -> continue to next one
        if (!updateData.isProductAvailable) {
            // mark it as unavailable
            internalTxService.markProductAsUnavailable(newProductForUpdate.id)
            return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_ERROR
        }

        // update product data
        internalTxService.updateProduct(updateData.toProductUpdateDataDto(newProductForUpdate.id))

        //-------
        // TODO zrusit a prerobit cez observable
        //  notify listener
        notifyUpdateListener(productForUpdate.eshopUuid, listener)

        return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_OK
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

enum class ContinueUpdateStatus {
    CONTINUE_TO_NEXT_ONE_OK,
    CONTINUE_TO_NEXT_ONE_ERROR,
    STOP_PROCESSING_NEXT_ONE_OK,
    STOP_PROCESSING_NEXT_ONE_ERROR
}

class UpdateProductException(productForUpdate: ProductDetailInfo, e: Exception) :
        PrcoRuntimeException("error while updating product id ${productForUpdate.id} URL ${productForUpdate.url}", e)

class GettingProductForUpdateException(eshopUuid: EshopUuid, e: Exception) :
        PrcoRuntimeException("error while retrieving next product from DB to update for eshop $eshopUuid", e)

fun ProductUpdateData.toProductUpdateDataDto(productId: Long): ProductUpdateDataDto =
        ProductUpdateDataDto(
                productId,
                this.url,
                this.name,
                this.priceForPackage,
                this.productAction,
                this.actionValidity,
                this.pictureUrl)

