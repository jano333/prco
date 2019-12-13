package sk.hudak.prco.task.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PreDestroy

@Component
class UpdateProductExecutors {

    val handlerTaskExecutor: ExecutorService = createInternalThreadExecutor("update-handler-task", 10)
    val internalServiceExecutor: ExecutorService = createInternalThreadExecutor("update-internal-service", 20)
    val htmlParserExecutor: ExecutorService = createInternalThreadExecutor("update-html-parser", 10)

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProductExecutors::class.java)!!
    }

    @PreDestroy
    fun shutdownNowAllExecutors() {
        LOG.trace("start shutting down of all executors")
        handlerTaskExecutor.shutdownNow()
        internalServiceExecutor.shutdownNow()
        htmlParserExecutor.shutdownNow()
        LOG.debug("shutting down of all executors completed")
    }

    //FIXME spolocna metoda aj pre add executor
    private fun createInternalThreadExecutor(prefix: String, nThreads: Int): ExecutorService {
        return ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                InternalThreadFactory(prefix))
    }
}

private class InternalThreadFactory(prefix: String) : ThreadFactory {

    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String

    companion object {
        private val poolNumber = AtomicInteger(1)
        private val LOG = LoggerFactory.getLogger(InternalThreadFactory::class.java)!!
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

        t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->
            LOG.error("error in thread ${t.name}", e)
        }
        return t
    }


}
