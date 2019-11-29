package sk.hudak.prco.task.ng.ee

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.parser.eshop.EshopProductsParser

//TODO pre error eventy bazovu class alebo marker interface

/**
 *  1. searchKeywordId -> searchKeyword
 */
data class NewKeyWordIdEvent(val eshopUuid: EshopUuid,
                             val searchKeyWordId: Long) : CoreEvent()

data class RetrieveKeywordBaseOnKeywordIdErrorEvent(val event: NewKeyWordIdEvent,
                                                    val error: Throwable) : CoreEvent()
// -----------

/**
 * 2. searchKeyword -> searchKeywordURL
 */
data class NewKeyWordEvent(val eshopUuid: EshopUuid,
                           val searchKeyWordId: Long,
                           val searchKeyWord: String) : CoreEvent()

data class BuildSearchUrlForKeywordErrorEvent(val event: NewKeyWordEvent,
                                              val error: Throwable) : CoreEvent()

// -----------

/**
 * 3. searchKeywordURL -> Document
 */
data class NewKeyWordUrlEvent(val eshopUuid: EshopUuid,
                              val searchKeyWord: String,
                              val searchUrl: String) : CoreEvent()

data class RetrieveDocumentForSearchUrlErrorEvent(val event: NewKeyWordUrlEvent,
                                                  val error: Throwable) : CoreEvent()

// -------------
/**
 * Parallel
 * 4.a Document -> countOfPages
 * 4.b Document -> firstPageProductURLs[]
 */
data class FirstDocumentEvent(val document: Document,
                              val searchKeyWord: String,
                              val searchUrl: String,
                              val eshopParser: EshopProductsParser) : CoreEvent()

data class ParseCountOfPagesErrorEvent(val event: FirstDocumentEvent,
                                       val error: Throwable) : CoreEvent()

data class ParseProductListURLsErrorEvent(val event: FirstDocumentEvent,
                                          val pageNumber: Int,
                                          val error: Throwable) : CoreEvent()

data class CountOfPagesEvent(val countOfPages: Int,
                             val document: Document,
                             val searchUrl: String) : CoreEvent()

data class FirstPageProductURLsEvent(val pageProductURLs: List<String>,
                                     val document: Document,
                                     val eshopUuid: EshopUuid,
                                     val searchKeyWord: String,
                                     val searchUrl: String) : CoreEvent()

// -------------
data class NewProductUrlEvent(val newProductUrl: String,
                              val eshopUuid: EshopUuid) : CoreEvent()

data class FilterDuplicityErrorEvent(val event: FirstPageProductURLsEvent,
                                     val error: Throwable) : CoreEvent()

data class FilterNotExistingErrorEvent(val event: FirstPageProductURLsEvent,
                                       val error: Throwable) : CoreEvent()

// -------
data class NewProductDocumentEvent(val document: Document,
                                   val newProductUrl: String,
                                   val eshopParser: EshopProductsParser) : CoreEvent()

data class RetrieveDocumentForUrlErrorEvent(val event: NewProductUrlEvent,
                                            val error: Throwable) : CoreEvent()

// ---------
data class ProductNewDataEvent(val productNewData: ProductNewData) : CoreEvent()

data class ParseProductNewDataErrorEvent(val event: NewProductDocumentEvent,
                                         val error: Throwable) : CoreEvent()

data class SaveProductNewDataErrorEvent(val event: ProductNewDataEvent,
                                        val error: Throwable) : CoreEvent()

