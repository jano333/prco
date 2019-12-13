package sk.hudak.prco.task.handler.update.error

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.exception.ProductPageNotFoundHttpParserException
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.add.AddProductExecutors
import sk.hudak.prco.task.update.RetrieveUpdateDocumentForUrlErrorEvent
import java.util.*
import java.util.concurrent.CompletionException

/**
 *
 * <code>
 * 2019-12-13 14:35:06,466 ERROR [DR_MAX-process-thread] s.h.p.t.h.ErrorEventProductHandler[ErrorEventProductHandler.kt:95][DR_MAX-a242e9e2-4fe7-4fbc-acc7-c176afb27f52] error event: RetrieveUpdateDocumentForUrlErrorEvent
 * 2019-12-13 14:35:06,474 ERROR [DR_MAX-process-thread] s.h.p.t.h.ErrorEventProductHandler[ErrorEventProductHandler.kt:96][DR_MAX-a242e9e2-4fe7-4fbc-acc7-c176afb27f52] error while processing source event: ProductDetailInfoForUpdateEvent(productDetailInfo=ProductDetailInfo(id=4169, url=https://www.drmax.sk/pampers-pro-care-premium-1-2-5kg-38ks/, eshopUuid=DR_MAX), identifier=a242e9e2-4fe7-4fbc-acc7-c176afb27f52)
 * 2019-12-13 14:35:06,477 ERROR [DR_MAX-process-thread] s.h.p.t.h.ErrorEventProductHandler[ErrorEventProductHandler.kt:97][DR_MAX-a242e9e2-4fe7-4fbc-acc7-c176afb27f52] sk.hudak.prco.exception.ProductPageNotFoundHttpParserException: error creating document for url 'https://www.drmax.sk/pampers-pro-care-premium-1-2-5kg-38ks/':  org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=https://www.drmax.sk/pampers-pro-care-premium-1-2-5kg-38ks/
 *java.util.concurrent.CompletionException: sk.hudak.prco.exception.ProductPageNotFoundHttpParserException: error creating document for url 'https://www.drmax.sk/pampers-pro-care-premium-1-2-5kg-38ks/':  org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=https://www.drmax.sk/pampers-pro-care-premium-1-2-5kg-38ks/
 *at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
 *at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
 *at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1592)
 *at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
 *at java.util.concurrent.FutureTask.run(FutureTask.java:266)
 *at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:180)
 *at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:293)
 *at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
 *at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
 *at java.lang.Thread.run(Thread.java:745)
 *Caused by: sk.hudak.prco.exception.ProductPageNotFoundHttpParserException: error creating document for url 'https://www.drmax.sk/pampers-pro-care-premium-1-2-5kg-38ks/':  org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=https://www.drmax.sk/pampers-pro-care-premium-1-2-5kg-38ks/
 *at sk.hudak.prco.parser.eshop.JSoupProductParser.convertToParserException(JSoupProductParser.kt:287)
 *at sk.hudak.prco.parser.eshop.JSoupProductParser.retrieveDocument(JSoupProductParser.kt:277)
 *at sk.hudak.prco.task.helper.BasicDocumentHelper$retrieveDocumentForUrl$1.get(BasicDocumentHelper.kt:24)
 *at sk.hudak.prco.task.helper.BasicDocumentHelper$retrieveDocumentForUrl$1.get(BasicDocumentHelper.kt:13)
 *at sk.hudak.prco.task.handler.EshopLogSupplier.get(BasicHandler.kt:23)
 *at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1590)
 *... 7 common frames omitted
 *aused by: org.jsoup.HttpStatusException: HTTP error fetching URL
 *at org.jsoup.helper.HttpConnection$Response.execute(HttpConnection.java:682)
 *at org.jsoup.helper.HttpConnection$Response.execute(HttpConnection.java:629)
 *at org.jsoup.helper.HttpConnection.execute(HttpConnection.java:261)
 *at sk.hudak.prco.parser.eshop.JSoupProductParser.retrieveDocument(JSoupProductParser.kt:269)
 *... 11 common frames omitted
 *</code>
 */
//TODO osobitne vo vlakne lebo sa tu odmazava z db produkt
@Component
class RetrieveUpdateDocumentForUrlErrorEvent_Handler(prcoObservable: PrcoObservable,
                                                     private val addProductExecutors: AddProductExecutors,
                                                     private val internalTxService: InternalTxService)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(RetrieveUpdateDocumentForUrlErrorEvent_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, errorEvent: CoreEvent) {
        if (errorEvent !is RetrieveUpdateDocumentForUrlErrorEvent) {
            return
        }
        when (errorEvent.error) {
            !is CompletionException -> {
                //TODO log nie je to CompletionException
                return
            }
            else -> {
                if (errorEvent.error.cause == null) {
                    //TODO log
                    return
                }
                when (errorEvent.error.cause) {
                    is ProductPageNotFoundHttpParserException -> {
                        LOG.warn("getting 404 for ${errorEvent.event.productDetailInfo.url}")
                        internalTxService.removeProduct(errorEvent.event.productDetailInfo.id)
                    }
                }
            }
        }

    }


}