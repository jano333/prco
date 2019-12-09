package sk.hudak.prco.task.add

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent
import java.util.*

/**
 * basic error event
 */
interface BasicErrorEvent {
    val event: CoreEvent
    val error: Throwable
}

// TODO pridat identifier do error eventov pre vsetky...
// TODO toString nezobrazuje createOn pre danu event...
// FIXME identifier dat do CoreEvent..  pozor NewKeywordIdEvent toto nema identifier
/**
 *  1. searchKeywordId -> searchKeyword
 */
data class NewEshopKeywordIdEvent(val eshopUuid: EshopUuid,
                                  val searchKeywordId: Long,
                                  val identifier: String = UUID.randomUUID().toString()) : CoreEvent()

/**
 * Error while retrieving 'keyword' base on it's id.
 */
data class RetrieveKeywordBaseOnKeywordIdErrorEvent(override val event: NewEshopKeywordIdEvent,
                                                    override val error: Throwable) : CoreEvent(), BasicErrorEvent
// -----------

/**
 * 2. searchKeyword -> searchKeywordURL
 */
data class NewKeywordEvent(val searchKeyword: String,
                           val eshopUuid: EshopUuid,
                           val searchKeywordId: Long,
                           val identifier: String) : CoreEvent()

/**
 * Error while building search url base on 'searchKeyword' for given eshop.
 */
data class BuildSearchUrlForKeywordErrorEvent(override val event: NewKeywordEvent,
                                              override val error: Throwable) : CoreEvent(), BasicErrorEvent

// -----------

/**
 * 3. searchKeywordURL -> Document
 */
data class SearchKeywordUrlEvent(val searchUrl: String,
                                 val pageNumber: Int,
                                 val searchKeyword: String,
                                 val eshopUuid: EshopUuid,
                                 val identifier: String) : CoreEvent()

data class RetrieveDocumentForSearchUrlErrorEvent(override val event: SearchKeywordUrlEvent,
                                                  override val error: Throwable) : CoreEvent(), BasicErrorEvent

// -------------
/**
 * Parallel
 * 4.a Document -> countOfPages
 * 4.b Document -> firstPageProductURLs[]
 */
data class SearchPageDocumentEvent(val searchDocument: Document,
                                   val pageNumber: Int,
                                   val eshopUuid: EshopUuid,
                                   val searchKeyWord: String,
                                   val searchUrl: String,
                                   val identifier: String) : CoreEvent() {
    override fun toString(): String {
        return "SearchPageDocumentEvent(" +
                "searchDocument=${searchDocument.location()}, " +
                "pageNumber=$pageNumber, " +
                "eshopUuid=$eshopUuid, " +
                "searchKeyWord='$searchKeyWord', " +
                "searchUrl='$searchUrl', " +
                "identifier='$identifier')"
    }
}

data class ParseCountOfPagesErrorEvent(override val event: SearchPageDocumentEvent,
                                       override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class ParseProductListURLsErrorEvent(override val event: SearchPageDocumentEvent,
                                          val pageNumber: Int,
                                          override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class CountOfPagesEvent(val countOfPages: Int,
                             val searchUrl: String,
                             val searchKeyWord: String,
                             val eshopUuid: EshopUuid,
                             val identifier: String) : CoreEvent()

data class NewProductEshopUrlsEvent(val pageProductURLs: List<String>,
                                    val searchPageDocument: Document,
                                    val pageNumber: Int,
                                    val eshopUuid: EshopUuid,
                                    val searchKeyWord: String,
                                    val searchUrl: String,
                                    val identifier: String) : CoreEvent() {
    override fun toString(): String {
        return "NewProductEshopUrlsEvent(" +
                "pageProductURLs size=${pageProductURLs.size}, " +
                "searchPageDocument=${searchPageDocument.location()}, " +
                "pageNumber=$pageNumber, " +
                "eshopUuid=$eshopUuid, " +
                "searchKeyWord='$searchKeyWord', " +
                "searchUrl='$searchUrl', " +
                "identifier='$identifier')"
    }
}

// -------------
data class NewProductUrlWithEshopEvent(val newProductUrl: String,
                                       val eshopUuid: EshopUuid,
                                       val identifier: String) : CoreEvent()

data class FilterDuplicityErrorEvent(override val event: NewProductEshopUrlsEvent,
                                     override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class FilterNotExistingErrorEvent(override val event: NewProductEshopUrlsEvent,
                                       override val error: Throwable) : CoreEvent(), BasicErrorEvent

// -------
data class NewProductDocumentEvent(val document: Document,
                                   val newProductUrl: String,
                                   val eshopUuid: EshopUuid,
                                   val identifier: String) : CoreEvent() {
    override fun toString(): String {
        return "NewProductDocumentEvent(" +
                "document=${document.location()}, " +
                "newProductUrl='$newProductUrl', " +
                "eshopUuid=$eshopUuid, " +
                "identifier='$identifier')"
    }
}

data class RetrieveDocumentForUrlErrorEvent(override val event: NewProductUrlWithEshopEvent,
                                            override val error: Throwable) : CoreEvent(), BasicErrorEvent


// ---------
data class ProductNewDataEvent(val productNewData: ProductNewData,
                               val identifier: String) : CoreEvent()

data class ParseProductNewDataErrorEvent(override val event: NewProductDocumentEvent,
                                         override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class SaveProductNewDataErrorEvent(override val event: ProductNewDataEvent,
                                        override val error: Throwable) : CoreEvent(), BasicErrorEvent

// --------
data class BuildNextSearchPageUrlEvent(val currentPageNumber: Int,
                                       val searchKeyWord: String,
                                       val eshopUuid: EshopUuid,
                                       val identifier: String) : CoreEvent()

data class BuildNextPageSearchUrlErrorEvent(override val event: BuildNextSearchPageUrlEvent,
                                            override val error: Throwable) : CoreEvent(), BasicErrorEvent

// **************
data class NewKeywordIdEvent(val searchKeyWordId: Long) : CoreEvent()

// ************
data class NewProductUrlsEvent(val pageProductURLs: Set<String>,
                               val identifier: String = UUID.randomUUID().toString()) : CoreEvent() {

    override fun toString(): String {
        return "NewProductUrlsEvent(" +
                "pageProductURLs size=${pageProductURLs.size}, " +
                "identifier='$identifier')"
    }
}

data class NewProductUrlEvent(val productUrl: String,
                              val identifier: String) : CoreEvent()

data class FilterNotExistingProductErrorEvent(override val event: NewProductUrlsEvent,
                                              override val error: Throwable) : CoreEvent(), BasicErrorEvent

data class ParseEshopUuidErrorEvent(override val event: NewProductUrlEvent,
                                    override val error: Throwable) : CoreEvent(), BasicErrorEvent

