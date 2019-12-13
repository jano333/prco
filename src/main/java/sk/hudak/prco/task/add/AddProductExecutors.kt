package sk.hudak.prco.task.add

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.task.ProductEshopExecutors
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PreDestroy

@Component
class AddProductExecutors(val eshopDocumentExecutor: ProductEshopExecutors) {

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

class InternalThreadFactory(prefix: String) : ThreadFactory {

    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String

    companion object {
        private val poolNumber = AtomicInteger(1)
    }

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix = "$prefix-pool-" + poolNumber.getAndIncrement() + "-thread-"
    }

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group,
                r,
                namePrefix + threadNumber.getAndIncrement(),
                0)

        if (t.isDaemon)
            t.isDaemon = false
        if (t.priority != Thread.NORM_PRIORITY)
            t.priority = Thread.NORM_PRIORITY
        return t
    }
}
