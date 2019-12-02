package sk.hudak.prco.task.ng.ee

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.task.PrcoUncaughtExceptionHandler
import sk.hudak.prco.task.ng.EshopScheduledExecutor
import sk.hudak.prco.task.ng.InternalThreadFactory
import java.util.*
import java.util.concurrent.*
import javax.annotation.PreDestroy

@Component
class AddProductExecutors {

    val handlerTaskExecutor: ExecutorService = createInternalThreadExecutor("add-handler-task", 10)
    val searchUrlBuilderExecutor: ExecutorService = createInternalThreadExecutor("add-search-url", 2)
    val internalServiceExecutor: ExecutorService = createInternalThreadExecutor("add-db-service", 20)
    private val eshopDocumentExecutor = EnumMap<EshopUuid, ScheduledExecutorService>(EshopUuid::class.java)
    val htmlParserExecutor: ExecutorService = createInternalThreadExecutor("add-html-parser", 10)

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProductExecutors::class.java)!!
    }

    init {
        //TODO should down of executor
        EshopUuid.values().forEach {
            eshopDocumentExecutor[it] = createEshopThreadExecutor(it)
        }
    }

    @PreDestroy
    fun shutdownNowAllExecutors() {
        LOG.debug("start shutting down of all executors")
        handlerTaskExecutor.shutdownNow()
        internalServiceExecutor.shutdownNow()
        searchUrlBuilderExecutor.shutdownNow()
        htmlParserExecutor.shutdownNow()
        eshopDocumentExecutor.values.forEach {
            it.shutdownNow()
        }
        LOG.debug("shutting down of all executors completed")
    }

    fun getEshopExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return eshopDocumentExecutor[eshopUuid]!!
    }

    private fun createInternalThreadExecutor(prefix: String, nThreads: Int): ExecutorService {
        val threadPoolExecutor = ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                InternalThreadFactory(prefix))

        return threadPoolExecutor
    }

    private fun createEshopThreadExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return EshopScheduledExecutor(eshopUuid, ThreadFactory {
            val thread = Thread(it, "${eshopUuid.name}-add-process-thread")
            thread.uncaughtExceptionHandler = PrcoUncaughtExceptionHandler(eshopUuid)
            thread
        })
    }


}