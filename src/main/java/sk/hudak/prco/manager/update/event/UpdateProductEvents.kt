package sk.hudak.prco.manager.update.event

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.ErrorEvent
import sk.hudak.prco.events.FinalEvent
import sk.hudak.prco.events.StartEvent
import java.util.*

data class UpdateOneProductInEshopEvent(val eshopUuid: EshopUuid,
                                        val identifier: String = UUID.randomUUID().toString()) : CoreEvent(), StartEvent

data class UpdateAllProductsInEshopEvent(val eshopUuid: EshopUuid,
                                         val identifier: String = UUID.randomUUID().toString()) : CoreEvent(), StartEvent

data class LoadNextProductToBeUpdatedErrorEvent(override val event: UpdateOneProductInEshopEvent,
                                                override val error: Throwable) : CoreEvent(), ErrorEvent

data class ProductDetailInfoForUpdateEvent(val productDetailInfo: ProductDetailInfo,
                                           val identifier: String) : CoreEvent()

data class UpdateProductDocumentEvent(val document: Document,
                                      val productForUpdate: ProductDetailInfo,
                                      val identifier: String) : CoreEvent() {
    override fun toString(): String {
        return "UpdateProductDocumentEvent(" +
                "document=${document.location()}, " +
                "productForUpdate=$productForUpdate, " +
                "identifier='$identifier')"
    }
}

data class RetrieveUpdateDocumentForUrlErrorEvent(override val event: ProductDetailInfoForUpdateEvent,
                                                  override val error: Throwable) : CoreEvent(), ErrorEvent

data class ProductUpdateDataEvent(val productUpdateData: ProductUpdateData,
                                  val productForUpdateData: ProductDetailInfo,
                                  val identifier: String) : CoreEvent()

data class ParseProductUpdateDataErrorEvent(override val event: UpdateProductDocumentEvent,
                                            override val error: Throwable) : CoreEvent(), ErrorEvent

data class ProcessProductUpdateDataFinalEvent(val productUpdateData: ProductUpdateData,
                                              val productForUpdateData: ProductDetailInfo,
                                              val identifier: String) : CoreEvent(), FinalEvent

data class ProcessProductUpdateDataErrorEvent(override val event: ProcessProductUpdateDataFinalEvent,
                                              override val error: Throwable) : CoreEvent(), ErrorEvent

data class MarkProductAsUnavailableFinalEvent(val productForUpdateData: ProductDetailInfo,
                                              val identifier: String) : CoreEvent(), FinalEvent

data class MarkProductAsUnavailableErrorEvent(override val event: MarkProductAsUnavailableFinalEvent,
                                              override val error: Throwable) : CoreEvent(), ErrorEvent

data class ProductUpdateDataRedirectEvent(val productUpdateData: ProductUpdateData,
                                          val productForUpdate: ProductDetailInfo,
                                          val identifier: String) : CoreEvent()

data class ProductUpdateDataRedirectNotYetExistEvent(val productUpdateData: ProductUpdateData,
                                                     val productForUpdateData: ProductDetailInfo,
                                                     val identifier: String) : CoreEvent()

data class ProductUpdateDataRedirectAlreadyExistEvent(val newProductForUpdateData: ProductDetailInfo,
                                                      val productUpdateData: ProductUpdateData,
                                                      val productForUpdateData: ProductDetailInfo,
                                                      val identifier: String) : CoreEvent()

data class FindRedirectProductByUrlErrorEvent(override val event: ProductUpdateDataRedirectEvent,
                                              override val error: Throwable) : CoreEvent(), ErrorEvent

data class ProcessProductUpdateUrlFinalEvent(val productUpdateData: ProductUpdateData,
                                             val productForUpdateData: ProductDetailInfo,
                                             val identifier: String) : CoreEvent(), FinalEvent

data class UpdateProductWithNewUrlErrorEvent(override val event: ProcessProductUpdateUrlFinalEvent,
                                             override val error: Throwable) : CoreEvent(), ErrorEvent

data class ProcessRemoveOldProductFinalEvent(val productUpdateData: ProductUpdateData,
                                             val productForUpdateData: ProductDetailInfo,
                                             val identifier: String) : CoreEvent(), FinalEvent

data class ProcessProductUpdateDataForRedirectFinalEvent(val newProductForUpdateData: ProductDetailInfo,
                                                         val productUpdateData: ProductUpdateData,
                                                         val identifier: String) : CoreEvent()

data class RemoveProductWithOldUrlErrorEvent(override val event: ProcessRemoveOldProductFinalEvent,
                                             override val error: Throwable) : CoreEvent(), ErrorEvent

data class ProcessProductUpdateDataForRedirectErrorEvent(override val event: ProcessProductUpdateDataForRedirectFinalEvent,
                                                         override val error: Throwable) : CoreEvent(), ErrorEvent

data class LoadProductsToBeUpdatedErrorEvent(override val event: UpdateAllProductsInEshopEvent,
                                             override val error: Throwable) : CoreEvent(), ErrorEvent


