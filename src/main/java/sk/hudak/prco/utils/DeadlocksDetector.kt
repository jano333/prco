package sk.hudak.prco.utils

import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import java.lang.management.ThreadInfo
import java.lang.management.ThreadMXBean
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

class DeadlockedThreadDetector(private val period: Long,
                               private val unit: TimeUnit) {

    companion object {
        private val LOG = LoggerFactory.getLogger(DeadlockedThreadDetector::class.java)!!
    }

    private val mbean: ThreadMXBean = ManagementFactory.getThreadMXBean()
    private val scheduler = Executors.newScheduledThreadPool(1)
    private val deadlockHandler: DeadlockHandler = DeadlockConsoleHandlerImpl()
    private val deadlockCheck = Runnable {
        val deadlockedThreadIds: LongArray? = mbean.findDeadlockedThreads()
        if (deadlockedThreadIds != null) {
            LOG.trace("deadlocked threads detected")
            deadlockHandler.handleDeadlock(mbean.getThreadInfo(deadlockedThreadIds))
        } else {
            LOG.trace("no deadlocked threads detected")
        }
    }

    fun start() {
        scheduler.scheduleAtFixedRate(deadlockCheck, period, period, unit)
    }
}