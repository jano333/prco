package sk.hudak.prco.events

import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class InternalThreadFactory(prefix: String) : ThreadFactory {

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
