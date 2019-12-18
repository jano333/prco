package sk.hudak.prco.manager.add.event

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.InternalThreadFactory
import sk.hudak.prco.events.executors.EshopExecutors
import java.util.concurrent.*
import javax.annotation.PreDestroy

@Component
class AddProductExecutors(private val eshopDocumentExecutor: EshopExecutors) {

    val handlerTaskExecutor: ExecutorService = createInternalThreadExecutor("add-handler-task", 10)
    val searchUrlBuilderExecutor: ExecutorService = createInternalThreadExecutor("add-search-url", 5)
    val internalServiceExecutor: ExecutorService = createInternalThreadExecutor("add-internal-service", 20)
    val htmlParserExecutor: ExecutorService = createInternalThreadExecutor("add-html-parser", 10)
    val eshopUuidParserExecutor: ExecutorService = createInternalThreadExecutor("add-eshop-uuid-parser", 5)

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProductExecutors::class.java)!!
    }

    @PreDestroy
    fun shutdownNowAllExecutors() {
        LOG.trace("start shutting down of all executors")
        handlerTaskExecutor.shutdownNow()
        internalServiceExecutor.shutdownNow()
        searchUrlBuilderExecutor.shutdownNow()
        htmlParserExecutor.shutdownNow()
        eshopDocumentExecutor.shutdownAllNow()
        eshopUuidParserExecutor.shutdownNow()
        LOG.debug("shutting down of all executors completed")
    }

    fun getEshopExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return eshopDocumentExecutor.getEshopExecutor(eshopUuid)
    }

    private fun createInternalThreadExecutor(prefix: String, nThreads: Int): ExecutorService {
        return ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                InternalThreadFactory(prefix))
    }
}

