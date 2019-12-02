package sk.hudak.prco.task.ng.ee

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent

//TODO pre error eventy bazovu class alebo marker interface

/**
 * basic error event
 */
interface BasicErrorEvent {
    val event: CoreEvent
    val error: Throwable
}

/**
 *  1. searchKeywordId -> searchKeyword
 */
data class NewKeyWordIdEvent(val eshopUuid: EshopUuid,
                             val searchKeyWordId: Long) : CoreEvent()

/**
 * Error while retrieving 'keyword' base on it's id.
 */
data class RetrieveKeywordBaseOnKeywordIdErrorEvent(override val event: NewKeyWordIdEvent,
                                                    override val error: Throwable) : CoreEvent(), BasicErrorEvent
// -----------

/**
 * 2. searchKeyword -> searchKeywordURL
 */
data class NewKeyWordEvent(val searchKeyWord: String,
                           val eshopUuid: EshopUuid,
                           val searchKeyWordId: Long) : CoreEvent()

/**
 * Error while building search url base on 'searchKeyword' for given eshop.
 */
data class BuildSearchUrlForKeywordErrorEvent(override val event: NewKeyWordEvent,
                                              override val error: Throwable) : CoreEvent(), BasicErrorEvent

// -----------

/**
 * 3. searchKeywordURL -> Document
 */
data class NewKeyWordUrlEvent(val searchUrl: String,
                              val eshopUuid: EshopUuid,
                              val searchKeyWord: String) : CoreEvent()

data class RetrieveDocumentForSearchUrlErrorEvent(override val event: NewKeyWordUrlEvent,
                                                  override val error: Throwable) : CoreEvent(), BasicErrorEvent

// -------------
/**
 * Parallel
 * 4.a Document -> countOfPages
 * 4.b Document -> firstPageProductURLs[]
 */
data class FirstDocumentEvent(val document: Document,
                              val eshopUuid: EshopUuid,
                              val searchKeyWord: String,
                              val searchUrl: String) : CoreEvent()

data class ParseCountOfPagesErrorEvent(override val event: FirstDocumentEvent,
                                       override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class ParseProductListURLsErrorEvent(override val event: FirstDocumentEvent,
                                          val pageNumber: Int,
                                          override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class CountOfPagesEvent(val countOfPages: Int,
                             val searchKeyWord: String,
                             val eshopUuid: EshopUuid) : CoreEvent()

data class FirstPageProductURLsEvent(val pageProductURLs: List<String>,
                                     val document: Document,
                                     val eshopUuid: EshopUuid,
                                     val searchKeyWord: String,
                                     val searchUrl: String) : CoreEvent()

// -------------
data class NewProductUrlEvent(val newProductUrl: String,
                              val eshopUuid: EshopUuid) : CoreEvent()

data class FilterDuplicityErrorEvent(override val event: FirstPageProductURLsEvent,
                                     override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class FilterNotExistingErrorEvent(override val event: FirstPageProductURLsEvent,
                                       override val error: Throwable) : CoreEvent(), BasicErrorEvent

// -------
data class NewProductDocumentEvent(val document: Document,
                                   val newProductUrl: String,
                                   val eshopUuid: EshopUuid) : CoreEvent()

data class RetrieveDocumentForUrlErrorEvent(override val event: NewProductUrlEvent,
                                            override val error: Throwable) : CoreEvent(), BasicErrorEvent

// ---------
data class ProductNewDataEvent(val productNewData: ProductNewData) : CoreEvent()

data class ParseProductNewDataErrorEvent(override val event: NewProductDocumentEvent,
                                         override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class SaveProductNewDataErrorEvent(override val event: ProductNewDataEvent,
                                        override val error: Throwable) : CoreEvent(), BasicErrorEvent

// --------
data class BuildNextSearchPageUrl(val currentPageNumber: Int,
                                  val searchKeyWord: String,
                                  val eshopUuid: EshopUuid) : CoreEvent()
