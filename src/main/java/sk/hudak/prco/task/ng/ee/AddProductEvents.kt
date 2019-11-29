package sk.hudak.prco.task.ng.ee

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.parser.eshop.EshopProductsParser

/**
 *  1. searchKeywordId -> searchKeyword
 */
data class NewKeyWordIdEvent(val eshopUuid: EshopUuid,
                             val searchKeyWordId: Long) : CoreEvent()

data class NewKeyWordIdErrorEvent(val event: NewKeyWordIdEvent,
                                  val error: Throwable) : CoreEvent()
// -----------

/**
 * 2. searchKeyword -> searchKeywordURL
 */
data class NewKeyWordEvent(val eshopUuid: EshopUuid,
                           val searchKeyWordId: Long,
                           val searchKeyWord: String) : CoreEvent()

data class NewKeyWordErrorEvent(val event: NewKeyWordEvent,
                                val error: Throwable) : CoreEvent()

// -----------

/**
 * 3. searchKeywordURL -> Document
 */
data class NewKeyWordUrlEvent(val eshopUuid: EshopUuid,
                              val searchKeyWord: String,
                              val searchUrl: String) : CoreEvent()

data class NewKeyWordUrlErrorEvent(val event: NewKeyWordUrlEvent,
                                   val error: Throwable) : CoreEvent()

// -------------
/**
 * Parallel
 * 4.a Document -> countOfPages
 * 4.b Document -> firstPageProductURLs[]
 */
data class FirstDocumentEvent(val document: Document,
                              val searchUrl: String,
                              val eshopParser: EshopProductsParser) : CoreEvent()

data class FirstDocumentCountOfPageErrorEvent(val event: FirstDocumentEvent,
                                              val error: Throwable) : CoreEvent()

data class FirstDocumentPageProductUrlsErrorEvent(val event: FirstDocumentEvent,
                                                  val error: Throwable) : CoreEvent()

data class CountOfPagesEvent(val countOfPages: Int,
                             val document: Document,
                             val searchUrl: String) : CoreEvent()

data class FirstPageProductURLsEvent(val pageProductURLs: List<String>,
                                     val document: Document,
                                     val searchUrl: String) : CoreEvent()
// -------------
