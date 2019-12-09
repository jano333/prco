package sk.hudak.prco.task.add

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.task.old.InternalThreadFactory
import sk.hudak.prco.task.old.PrcoUncaughtExceptionHandler
import sk.hudak.prco.utils.ThreadUtils
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.PreDestroy

@Component
class AddProductExecutors {

    val handlerTaskExecutor: ExecutorService = createInternalThreadExecutor("add-handler-task", 10)
    val searchUrlBuilderExecutor: ExecutorService = createInternalThreadExecutor("add-search-url", 5)
    val internalServiceExecutor: ExecutorService = createInternalThreadExecutor("add-internal-service", 20)
    private val eshopDocumentExecutor = EnumMap<EshopUuid, ScheduledExecutorService>(EshopUuid::class.java)
    val htmlParserExecutor: ExecutorService = createInternalThreadExecutor("add-html-parser", 10)
    val eshopUuidParserExecutor: ExecutorService = createInternalThreadExecutor("add-eshop-uuid-parser", 5)

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProductExecutors::class.java)!!
    }

    init {
        EshopUuid.values().forEach {
            eshopDocumentExecutor[it] = createEshopThreadExecutor(it)
        }
    }

    @PreDestroy
    fun shutdownNowAllExecutors() {
        LOG.trace("start shutting down of all executors")
        handlerTaskExecutor.shutdownNow()
        internalServiceExecutor.shutdownNow()
        searchUrlBuilderExecutor.shutdownNow()
        htmlParserExecutor.shutdownNow()
        eshopDocumentExecutor.values.forEach {
            it.shutdownNow()
        }
        eshopUuidParserExecutor.shutdownNow()
        LOG.debug("shutting down of all executors completed")
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
            val thread = Thread(it, "${eshopUuid.name}-add-process-thread")
            thread.uncaughtExceptionHandler = PrcoUncaughtExceptionHandler(eshopUuid)
            thread
        })
    }
}

class EshopScheduledExecutor(val eshopUuid: EshopUuid, threadFactory: ThreadFactory)
    : ScheduledThreadPoolExecutor(1, threadFactory) {

    companion object {
        val LOG = LoggerFactory.getLogger(EshopScheduledExecutor::class.java)!!
    }

    private val myLock = ReentrantLock()

    private var lastRunDate: Date? = null

    override fun execute(command: Runnable) {
        var countOfSecond: Long? = null

        LOG.debug("-> requesting lock for ${Thread.currentThread().name}")
        myLock.lock()
        LOG.debug("<- received lock for ${Thread.currentThread().name}")
        try {
            if (lastRunDate == null) {
                // spusti to hned
                LOG.debug("scheduling command for $eshopUuid to be run now")
                // nastavim novy cas...
                lastRunDate = Date()
            } else {
                // vygenerum interval medzi <5,20> v sekundach
                val randomSecInterval = ThreadUtils.generateRandomSecondInInterval(5, 20).toLong()

                var dateTimeToRun = calculateDateTimeToRun(lastRunDate!!, randomSecInterval)
                countOfSecond = ChronoUnit.SECONDS.between(
                        Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        dateTimeToRun.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())

                LOG.debug("scheduling command for $eshopUuid to be run after $countOfSecond at $dateTimeToRun")
                // nastavim novy cas...
                lastRunDate = dateTimeToRun
            }
        } finally {
            myLock.unlock()
            LOG.debug("received unlock for ${Thread.currentThread().name}")
        }

        if (countOfSecond != null) {
            LOG.debug("-> scheduling command")
            val schedule = super.schedule(command, countOfSecond, TimeUnit.SECONDS)
            LOG.debug("<- scheduling command")

        } else {
            LOG.debug("-> running command")
            super.execute(command)
            LOG.debug("<- running command")
        }
    }

    private fun calculateDateTimeToRun(lastRunDate: Date, countOfSecondToRun: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.time = lastRunDate
        calendar.add(Calendar.SECOND, countOfSecondToRun.toInt())
        return calendar.time
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        throw PrcoRuntimeException("Not supported")
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        throw PrcoRuntimeException("Not supported")
    }

    override fun submit(task: Runnable): Future<*> {
        throw PrcoRuntimeException("Not suppoerted")
    }
}