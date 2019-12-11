package sk.hudak.prco.task.old

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.utils.ThreadUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.*
import javax.annotation.PreDestroy

enum class TaskStatus {
    RUNNING,
    SHOUD_STOP, // pomocny interny stav
    STOPPED,
    FINISHED_OK,
    FINISHED_WITH_ERROR
}

data class TaskContext constructor(
        val status: TaskStatus,
        val lastChanged: Date = Date())

@Deprecated("dont use")
interface EshopTaskManager {

//    fun dajmiho(eshopUuid: EshopUuid): Executor

    val tasks: Map<EshopUuid, TaskContext>

    val isAnyTaskRunning: Boolean

    fun scheduleTask(eshopUuid: EshopUuid, task: Runnable): Future<*>

    fun submitTask(eshopUuid: EshopUuid, task: Runnable): Future<*>

//    fun <T> submitTask(eshopUuid: EshopUuid, task: Callable<T>): Future<T>

    fun isTaskRunning(eshopUuid: EshopUuid): Boolean

    fun markTaskAsRunning(eshopUuid: EshopUuid)

    fun isTaskStopped(eshopUuid: EshopUuid): Boolean

    fun markTaskAsStopped(eshopUuid: EshopUuid)

    fun isTaskFinished(eshopUuid: EshopUuid): Boolean

    fun markTaskAsFinished(eshopUuid: EshopUuid, finishedWithError: Boolean)

    fun isTaskShouldStopped(eshopUuid: EshopUuid): Boolean

    fun markTaskAsShouldStopped(eshopUuid: EshopUuid)

    fun sleepIfNeeded(eshopUuid: EshopUuid)
}

@Component
class EshopTaskManagerImpl : EshopTaskManager {

    companion object {
        val LOG = LoggerFactory.getLogger(EshopTaskManagerImpl::class.java)!!
    }

    private val executors = EnumMap<EshopUuid, ScheduledExecutorService>(EshopUuid::class.java)
    private val internalTask = ConcurrentHashMap<EshopUuid, TaskContext>(EshopUuid.values().size)

    //FIXME KT ci sa neda neako inak aby nebola internalTask(pozri povodnu java o co islo)
    //TODO skusit init {} pozri PrcoCustomHostnameVerifier a porovnaj java -> kotlin
    override val tasks: Map<EshopUuid, TaskContext> = internalTask

    override val isAnyTaskRunning: Boolean
        get() {
            for (eshopUuid in EshopUuid.values()) {
                if (isTaskRunning(eshopUuid)) {
                    return true
                }
            }
            return false
        }


    init {
        EshopUuid.values().forEach {
            executors[it] = createExecutorServiceForEshop(it)
            internalTask[it] = TaskContext(TaskStatus.STOPPED)
        }
    }

    @PreDestroy
    fun tearDown() {
        for (eshopUuid in executors.keys) {
            executors[eshopUuid]?.shutdownNow()
        }
    }

    private fun createExecutorServiceForEshop(eshopUuid: EshopUuid): ScheduledExecutorService {
        return Executors.newSingleThreadScheduledExecutor {
            val thread = Thread(it, "${eshopUuid.name}-thread")
            thread.uncaughtExceptionHandler = PrcoUncaughtExceptionHandler(eshopUuid)
            thread
        }
    }

    override fun submitTask(eshopUuid: EshopUuid, task: Runnable): Future<*> {
        LOG.debug("submitting new task for eshop $eshopUuid")
        return executors[eshopUuid]!!.submit(task)
    }

    override fun scheduleTask(eshopUuid: EshopUuid, task: Runnable): Future<*> {
//        return executors[eshopUuid]!!.submit(task)
        val scheduledAfterSec = ThreadUtils.generateRandomSecondInInterval(5, 20).toLong()

        LOG.debug("scheduling new task for eshop $eshopUuid to start at ")

        return executors[eshopUuid]!!.schedule(task, scheduledAfterSec, TimeUnit.SECONDS)
    }

    // TODO to co za nazov
//    override fun dajmiho(eshopUuid: EshopUuid): Executor {
//        return executors[eshopUuid]!!
//    }

//    override fun <T> submitTask(eshopUuid: EshopUuid, task: Callable<T>): Future<T> {
//        return executors[eshopUuid]!!.submit(task)
//    }


    override fun isTaskRunning(eshopUuid: EshopUuid): Boolean {
        return TaskStatus.RUNNING == internalTask[eshopUuid]?.status
    }

    override fun isTaskStopped(eshopUuid: EshopUuid): Boolean {
        return TaskStatus.STOPPED == internalTask[eshopUuid]?.status
    }

    override fun isTaskFinished(eshopUuid: EshopUuid): Boolean {
        val other = internalTask[eshopUuid]?.status
        return TaskStatus.FINISHED_OK == other || TaskStatus.FINISHED_WITH_ERROR == other
    }

    override fun isTaskShouldStopped(eshopUuid: EshopUuid): Boolean {
        return TaskStatus.SHOUD_STOP == internalTask[eshopUuid]?.status
    }

    override fun markTaskAsShouldStopped(eshopUuid: EshopUuid) {
        internalTask[eshopUuid] = TaskContext(TaskStatus.SHOUD_STOP)
    }

    override fun markTaskAsRunning(eshopUuid: EshopUuid) {
        LOG.debug("marking task for eshop $eshopUuid as ${TaskStatus.RUNNING}")
        internalTask[eshopUuid] = TaskContext(TaskStatus.RUNNING)
    }

    override fun markTaskAsFinished(eshopUuid: EshopUuid, finishedWithError: Boolean) {
        val newTaskStatus = if (finishedWithError) TaskStatus.FINISHED_WITH_ERROR else TaskStatus.FINISHED_OK
        LOG.debug("marking task for eshop $eshopUuid as $newTaskStatus")
        internalTask[eshopUuid] = TaskContext(newTaskStatus)
    }

    override fun markTaskAsStopped(eshopUuid: EshopUuid) {
        LOG.debug("marking task for eshop $eshopUuid as ${TaskStatus.STOPPED}")
        internalTask[eshopUuid] = TaskContext(TaskStatus.STOPPED)
    }

    override fun sleepIfNeeded(eshopUuid: EshopUuid) {
        val currentContext = internalTask[eshopUuid]!!
        val currentStatus = currentContext.status

        LOG.debug("task status $currentContext")
        if (TaskStatus.STOPPED == currentStatus) {
            return
        }
        if (isTaskFinished(eshopUuid)) {
            val lastChanged = currentContext.lastChanged
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

            val secondsBetween = ChronoUnit.SECONDS.between(lastChanged, LocalDateTime.now())

            val secondInInterval = ThreadUtils.generateRandomSecondInInterval()

            if (secondsBetween < secondInInterval) {
                ThreadUtils.sleepSafe(secondInInterval)
            }
        }
    }
}

data class PrcoUncaughtExceptionHandler(val eshopUuid: EshopUuid)
    : Thread.UncaughtExceptionHandler {

    companion object {
        val LOG = LoggerFactory.getLogger(PrcoUncaughtExceptionHandler::class.java)!!
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        LOG.error("eshop: $eshopUuid", e)
    }
}

class SingleContext {
    var error = false
    var errMsg: String? = null
    var values: MutableMap<String, Any> = mutableMapOf()

    fun addValue(key: String, value: Any) {
        values[key] = value
    }

    fun existValueForKey(key: String): Boolean = values[key] != null

    override fun toString(): String {
        return "SingleContext(error=$error, values=$values)"
    }
}

abstract class ExceptionHandlingRunnable : Runnable {

    final override fun run() {
        var context = SingleContext()
        try {
            doInRunnable(context)

        } catch (e: Exception) {
            context.error = true
            context.errMsg = e.message
            handleException(context, e)

        } finally {
            doInFinally(context)
        }
    }

    abstract fun doInRunnable(context: SingleContext)

    abstract fun handleException(context: SingleContext, e: Exception)

    open fun doInFinally(context: SingleContext) {
        // nothing, can be use for custom logic
    }
}




