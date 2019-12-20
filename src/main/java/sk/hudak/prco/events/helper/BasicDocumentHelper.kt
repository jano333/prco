package sk.hudak.prco.events.helper

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.executors.EshopExecutors
import sk.hudak.prco.events.handler.EshopLogSupplier
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class BasicDocumentHelper(private val eshopProductsParserHelper: EshopProductsParserHelper,
                          private val eshopDocumentExecutor: EshopExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(BasicDocumentHelper::class.java)!!
    }

    fun retrieveDocumentForUrl(productUrl: String, eshopUuid: EshopUuid, identifier: String): CompletableFuture<Document> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("retrieveDocumentForUrl")
                    eshopProductsParserHelper.findParserForEshop(eshopUuid).retrieveDocument(productUrl)
                }),
                eshopDocumentExecutor.getEshopExecutor(eshopUuid))
    }
}