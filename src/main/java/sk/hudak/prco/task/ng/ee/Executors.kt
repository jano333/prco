package sk.hudak.prco.task.ng.ee

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.task.PrcoUncaughtExceptionHandler
import sk.hudak.prco.task.ng.EshopScheduledExecutor
import sk.hudak.prco.task.ng.InternalThreadFactory
import java.util.*
import java.util.concurrent.*

@Component
class Executors {

    val internalServiceExecutor: ExecutorService = createInternalThreadExecutor("db-service", 20)
    val searchUrlBuilderExecutor: ExecutorService = createInternalThreadExecutor("search-url", 2)
    val eshopDocumentExecutor = EnumMap<EshopUuid, ScheduledExecutorService>(EshopUuid::class.java)
    val htmlParserExecutor: ExecutorService = createInternalThreadExecutor("html-parser", 10)

    init {
        //TODO should down of executor
        EshopUuid.values().forEach {
            eshopDocumentExecutor[it] = createEshopThreadExecutor(it)
        }

    }

    fun getEshopExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return eshopDocumentExecutor[eshopUuid]!!
    }

    private fun createInternalThreadExecutor(prefix: String, nThreads: Int): ExecutorService {
        return ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                InternalThreadFactory(prefix))
    }

    private fun createEshopThreadExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return EshopScheduledExecutor(eshopUuid, ThreadFactory {
            val thread = Thread(it, "${eshopUuid.name}-thread")
            thread.uncaughtExceptionHandler = PrcoUncaughtExceptionHandler(eshopUuid)
            thread
        })
    }


}