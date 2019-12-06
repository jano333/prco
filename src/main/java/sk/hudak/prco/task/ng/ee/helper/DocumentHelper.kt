package sk.hudak.prco.task.ng.ee.helper

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.handlers.EshopLogSupplier
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class DocumentHelper(private val eshopProductsParserHelper: EshopProductsParserHelper,
                     private val addProductExecutors: AddProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(DocumentHelper::class.java)!!
    }

    fun retrieveDocumentForUrl(productUrl: String, eshopUuid: EshopUuid, identifier: String): CompletableFuture<Document> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("retrieveDocumentForUrl")
                    eshopProductsParserHelper.findParserForEshop(eshopUuid).retrieveDocument(productUrl)
                }),
                addProductExecutors.getEshopExecutor(eshopUuid))
    }
}