package sk.hudak.prco.task.update

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.task.add.BasicErrorEvent
import java.util.*

data class UpdateProductsInEshopEvent(val eshopUuid: EshopUuid,
                                      val identifier: String = UUID.randomUUID().toString()) : CoreEvent()

data class LoadNextProductToBeUpdatedErrorEvent(override val event: UpdateProductsInEshopEvent,
                                                override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class ProductDetailInfoForUpdateEvent(val productDetailInfo: ProductDetailInfo,
                                           val identifier: String) : CoreEvent()

data class UpdateProductDocumentEvent(val document: Document,
                                      val productForUpdate: ProductDetailInfo,
                                      val identifier: String) : CoreEvent()

data class RetrieveUpdateDocumentForUrlErrorEvent(override val event: ProductDetailInfoForUpdateEvent,
                                                  override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class ProductUpdateDataEvent(val productUpdateData: ProductUpdateData,
                                  val productForUpdateData: ProductDetailInfo,
                                  val identifier: String) : CoreEvent()

data class ParseProductUpdateDataErrorEvent(override val event: UpdateProductDocumentEvent,
                                            override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class ProcessProductUpdateDataEvent(val productUpdateData: ProductUpdateData,
                                         val productForUpdateData: ProductDetailInfo,
                                         val identifier: String) : CoreEvent()

data class ProcessProductUpdateDataErrorEvent(override val event: ProcessProductUpdateDataEvent,
                                              override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class MarkProductAsUnavailableEvent(val productForUpdateData: ProductDetailInfo,
                                         val identifier: String) : CoreEvent()

data class MarkProductAsUnavailableErrorEvent(override val event: MarkProductAsUnavailableEvent,
                                              override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class ProductUpdateDataRedirectEvent(val productUpdateData: ProductUpdateData,
                                          val productForUpdate: ProductDetailInfo,
                                          val identifier: String) : CoreEvent()

data class ProductUpdateDataRedirectNotYetExistEvent(val productUpdateData: ProductUpdateData,
                                                     val productForUpdate: ProductDetailInfo,
                                                     val identifier: String) : CoreEvent()

data class ProductUpdateDataRedirectAlreadyExistEvent(val newProductForUpdate: ProductDetailInfo,
                                                      val productUpdateData: ProductUpdateData,
                                                      val productForUpdate: ProductDetailInfo,
                                                      val identifier: String) : CoreEvent()

data class FindRedirectProductByUrlErrorEvent(override val event: ProductUpdateDataRedirectEvent,
                                              override val error: Throwable) : CoreEvent(), BasicErrorEvent



