package sk.hudak.prco.utils

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.management.ManagementFactory
import java.lang.management.ThreadInfo
import java.lang.management.ThreadMXBean
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Base on: https://dzone.com/articles/how-detect-java-deadlocks
 */
private interface DeadlockHandler {
    fun handleDeadlock(deadlockedThreads: Array<ThreadInfo?>)
}

private class DeadlockConsoleHandlerImpl : DeadlockHandler {

    companion object {
        private val LOG = LoggerFactory.getLogger(DeadlockConsoleHandlerImpl::class.java)!!
    }

    override fun handleDeadlock(deadlockedThreads: Array<ThreadInfo?>) {
        if (deadlockedThreads == null) {
            LOG.debug("none deadlock detected")
            return
        }
        LOG.error("Deadlock detected!")
        deadlockedThreads.filterNotNull().forEach { threadInfo ->
            Thread.getAllStackTraces().keys.forEach { thread ->
                if (thread.id == threadInfo.threadId) {
                    LOG.error(threadInfo.toString().trim { it <= ' ' })
                    thread.stackTrace.forEach { ste ->
                        LOG.error("\t" + ste.toString().trim { it <= ' ' })
                    }
                }
            }
        }
    }
}

@Component
class DeadlockedThreadDetector {

    companion object {
        private val LOG = LoggerFactory.getLogger(DeadlockedThreadDetector::class.java)!!
    }

    private val mbean: ThreadMXBean = ManagementFactory.getThreadMXBean()
    private val scheduler = Executors.newScheduledThreadPool(1)
    private val deadlockHandler: DeadlockHandler = DeadlockConsoleHandlerImpl()
    private val deadlockCheck = Runnable {
        val deadlockedThreadIds: LongArray? = mbean.findDeadlockedThreads()
        if (deadlockedThreadIds != null) {
            LOG.warn("deadlocked threads detected")
            deadlockHandler.handleDeadlock(mbean.getThreadInfo(deadlockedThreadIds))
        } else {
            LOG.debug("no deadlocked threads detected")
        }
    }

    @PostConstruct
    fun start() {
        scheduler.scheduleAtFixedRate(deadlockCheck, 3, 3, TimeUnit.SECONDS)
        LOG.debug("scheduling stated")
    }

    @PreDestroy
    fun shutdown() {
        scheduler.shutdownNow()
        LOG.debug("shutdownNow completed")
    }
}