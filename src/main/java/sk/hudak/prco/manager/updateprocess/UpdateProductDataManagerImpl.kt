package sk.hudak.prco.manager.updateprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ParsingDataResponse
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.dto.ProductUpdateDataDto
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.dto.product.ProductFullDto
import sk.hudak.prco.manager.error.ErrorHandler
import sk.hudak.prco.manager.updateprocess.UpdateProcessResult.*
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe
import sk.hudak.prco.utils.ThreadUtils.sleepSafe
import java.util.*
import kotlin.collections.ArrayList

@Component
class UpdateProductDataManagerImpl(
        private val htmlParser: HtmlParser,
        private val internalTxService: InternalTxService,
        private val eshopTaskManager: EshopTaskManager,
        private val mapper: PrcoOrikaMapper,
        private val errorHandler: ErrorHandler

) : UpdateProductDataManager {

    companion object {
        val log = LoggerFactory.getLogger(UpdateProductDataManagerImpl::class.java)!!
    }

    override fun updateProductData(productId: Long) {
        // vyhladam product na update v DB na zaklade id
        val productForUpdate = getProductForUpdate(productId) ?:
        // nedavat tu ziadel log !! pozri getProductForUpdate
        return

        val eshopUuid = productForUpdate.eshopUuid!!

        eshopTaskManager.submitTask(eshopUuid, Runnable {

            //FIXME spojit tie dve volania do jedneho
            // ak je to volane hned po sebe tak sleepnem
            eshopTaskManager.sleepIfNeeded(eshopUuid)
            eshopTaskManager.markTaskAsRunning(eshopUuid)

            val updateProcessResult = internalParseAndUpdate(productForUpdate, UpdateProductDataListenerAdapter.LOG_INSTANCE)

            eshopTaskManager.markTaskAsFinished(eshopUuid, OK == updateProcessResult)

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

        eshopTaskManager.submitTask(eshopUuid, Runnable {
            // ak je to volane hned po sebe tak sleepnem
            eshopTaskManager.sleepIfNeeded(eshopUuid)
            eshopTaskManager.markTaskAsRunning(eshopUuid)

            var productForUpdate = getProductForUpdate(eshopUuid)
            while (productForUpdate!=null) {

                notifyUpdateListener(eshopUuid, listener)

                val updateProcessResult = internalParseAndUpdate(productForUpdate, UpdateProductDataListenerAdapter.EMPTY_INSTANCE)

                if (shouldContinueWithNexProduct(updateProcessResult)) {
                    sleepRandomSafe()

                    productForUpdate = getProductForUpdate(eshopUuid)

                } else {
                    eshopTaskManager.markTaskAsFinished(eshopUuid, true)
                    return@Runnable
                }

                if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
                    eshopTaskManager.markTaskAsStopped(eshopUuid)
                    break
                }
            }

            eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            log.debug("none product found for update for eshop {}", eshopUuid)
        })
    }

    override fun updateProductDataForEachProductInGroup(groupId: Long?, listener: UpdateProductDataListener) {
        //TODO bug !!!!!! nech nevracia len tie,  ktore uz boli updatnute
        val productsInGroup = convert(internalTxService.findProductsInGroup(groupId, true))
        if (productsInGroup.isEmpty()) {
            log.debug("none product found for update in group with id {} ", groupId)
            return
        }
        updateProductData(productsInGroup, listener)
    }

    private fun convert(productsForUpdate: List<ProductFullDto>): Map<EshopUuid, MutableList<ProductDetailInfo>> {

        val productsInEshop = EnumMap<EshopUuid, MutableList<ProductDetailInfo>>(EshopUuid::class.java)
        productsForUpdate.forEach { productFullDto -> productsInEshop[productFullDto.eshopUuid] = ArrayList() }
        productsForUpdate.forEach { productFullDto ->
            productsInEshop[productFullDto.eshopUuid]!!.add(
                    ProductDetailInfo(productFullDto.id, productFullDto.url, productFullDto.eshopUuid))
        }
        return productsInEshop
    }

    override fun updateProductDataForEachProductNotInAnyGroup(listener: UpdateProductDataListener) {

        val productsNotInAnyGroup = convert(internalTxService.findProductsNotInAnyGroup())

        if (productsNotInAnyGroup.isEmpty()) {
            log.debug("none product found for update which is not in any group")
            return
        }
        updateProductData(productsNotInAnyGroup, listener)
    }

    private fun shouldContinueWithNexProduct(updateProcessResult: UpdateProcessResult): Boolean {
        if (OK == updateProcessResult) {
            return true
        }

        if (ERR_UPDATE_ERROR_PRODUCT_IS_UNAVAILABLE == updateProcessResult) {
            return true
        }

        return ERR_PARSING_ERROR_HTTP_STATUS_404 == updateProcessResult

        //TODO impl pre ktore typy chyb sa ma process zastavit(teda dalsi product z daneho ehopu uz nebude spracovany)
    }

    private fun internalParseAndUpdate(productDetailInfo: ProductDetailInfo, listener: UpdateProductDataListener): UpdateProcessResult {
        // parsing
        val parsingDataResponse = parseOneProductUpdateData(productDetailInfo)

        // response processing and updating if ok
        return processParsingDataResponse(parsingDataResponse, productDetailInfo, listener)
    }

    private fun parseOneProductUpdateData(productDetailInfo: ProductDetailInfo): ParsingDataResponse {
        try {
            return ParsingDataResponse(htmlParser.parseProductUpdateData(productDetailInfo.url!!))

        } catch (e: Exception) {
            return ParsingDataResponse(e)
        }

    }

    // FIXME lepsie nazvy zvolit pre vstupne parametre

    private fun processParsingDataResponse(parsingDataResponse: ParsingDataResponse,
                                           parsingDataRequest: ProductDetailInfo,
                                           listener: UpdateProductDataListener): UpdateProcessResult {
        return if (parsingDataResponse.isError) {
            // spracovanie parsing chyby
            errorHandler.processParsingError(parsingDataResponse.error!!, parsingDataRequest)

            // spracovanie vyparsovanych dat
        } else processParsedData(parsingDataResponse.productUpdateData!!, parsingDataRequest, listener)

    }

    /**
     * @param updateData         vyparsovane data z eshopu
     * @param parsingDataRequest
     * @param listener
     */
    private fun processParsedData(updateData: ProductUpdateData, parsingDataRequest: ProductDetailInfo, listener: UpdateProductDataListener): UpdateProcessResult {
        // if not available log error and finish
        if (!updateData.isProductAvailable) {
            internalTxService.markProductAsUnavailable(parsingDataRequest.id)
            return ERR_UPDATE_ERROR_PRODUCT_IS_UNAVAILABLE
        }

        val existSameProductIdOpt = internalTxService.getProductWithUrl(updateData.url, parsingDataRequest.id)
        if (existSameProductIdOpt.isPresent) {
            log.debug("exist another product {} with url {}", existSameProductIdOpt.get(), updateData.url)
            log.debug("product {} will be removed, url {} ", parsingDataRequest.id, parsingDataRequest.url)
        }

        val productIdToBeUpdated = if (existSameProductIdOpt.isPresent) existSameProductIdOpt.get() else parsingDataRequest.id


        //FIXME premapovanie cez sk.hudak.prco mapper nie takto rucne, nech mam na jednom mieste tie preklapacky...
        internalTxService.updateProduct(ProductUpdateDataDto(
                productIdToBeUpdated,
                updateData.url,
                updateData.name,
                updateData.priceForPackage,
                updateData.productAction,
                updateData.actionValidity,
                updateData.pictureUrl))

        // remove product with old URL
        if (existSameProductIdOpt.isPresent) {
            internalTxService.removeProduct(parsingDataRequest.id)
        }

        // po dokonceni nech vola:
        notifyUpdateListener(updateData.eshopUuid, listener)

        return OK
    }


    private fun getProductForUpdate(productId: Long): ProductDetailInfo? {
        try {
            return internalTxService.getProductForUpdate(productId)

        } catch (e: Exception) {
            log.error("error while getting information for product with id $productId")
            return null
        }

    }

    private fun getProductForUpdate(eshopUuid: EshopUuid): ProductDetailInfo? {
        //TODO toto je zla metoda lebo ked je vinimka alebo sa nenajde dany product tak vystup je stale null co je zle !!!!
        val olderThanInHours = eshopUuid.olderThanInHours
        return try {
            internalTxService.getProductForUpdate(eshopUuid, olderThanInHours)

        } catch (e: Exception) {
            log.error("error while getting first product for update for eshop $eshopUuid older than $olderThanInHours hours")
            null
        }
    }

    private fun updateProductData(productsForUpdate: Map<EshopUuid, List<ProductDetailInfo>>, listener: UpdateProductDataListener) {


        for ((eshopUuid, productForUpdateList) in productsForUpdate) {

            eshopTaskManager.submitTask(eshopUuid, Runnable {
                // ak je to volane hned po sebe tak sleepnem
                eshopTaskManager.sleepIfNeeded(eshopUuid)
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                var countOfProductsAlreadyUpdated: Long = 0
                var countOfProductsWaitingToBeUpdated = productForUpdateList.size.toLong()

                for (productForUpdate in productForUpdateList) {

                    listener.onUpdateStatus(UpdateStatusInfo(eshopUuid, countOfProductsWaitingToBeUpdated, countOfProductsAlreadyUpdated))

                    val updateProcessResult = internalParseAndUpdate(productForUpdate, UpdateProductDataListenerAdapter.EMPTY_INSTANCE)

                    countOfProductsAlreadyUpdated++
                    countOfProductsWaitingToBeUpdated--

                    if (shouldContinueWithNexProduct(updateProcessResult)) {
                        if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
                            eshopTaskManager.markTaskAsStopped(eshopUuid)
                            break
                        }

                        sleepRandomSafe()
                        continue
                    }
                    eshopTaskManager.markTaskAsFinished(eshopUuid, true)
                    break
                }

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)

            })

            // kazdy dalsi task pre eshop spusti s 2 sekundovym oneskorenim
            sleepSafe(2)
        }
    }

    private fun notifyUpdateListener(eshopUuid: EshopUuid, listener: UpdateProductDataListener) {
        listener.onUpdateStatus(
                mapper.map(internalTxService.getStatisticForUpdateForEshop(eshopUuid, eshopUuid.olderThanInHours),
                        UpdateStatusInfo::class.java)
        )
    }
}
