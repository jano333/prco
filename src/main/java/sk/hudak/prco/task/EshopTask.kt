package sk.hudak.prco.task

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.utils.ThreadUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

enum class TaskStatus {
    RUNNING,
    SHOUD_STOP, // pomocny interny stav
    STOPPED,
    FINISHED_OK,
    FINISHED_WITH_ERROR
}

data class TaskContext @JvmOverloads constructor(
        val status: TaskStatus,
        val lastChanged: Date = Date())

interface EshopTaskManager {

    fun dajmiho(eshopUuid: EshopUuid): Executor

    val tasks: Map<EshopUuid, TaskContext>

    val isAnyTaskRunning: Boolean

    fun submitTask(eshopUuid: EshopUuid, task: Runnable): Future<*>

    fun <T> submitTask(eshopUuid: EshopUuid, task: Callable<T>): Future<T>

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
        val log = LoggerFactory.getLogger(EshopTaskManagerImpl::class.java)!!
    }

    private val executors = EnumMap<EshopUuid, ExecutorService>(EshopUuid::class.java)
    private val internalTask = ConcurrentHashMap<EshopUuid, TaskContext>(EshopUuid.values().size)

    //FIXME KT ci sa neda neako inak aby nebola internalTask(pozri povodnu java o co islo)
    //TODO skusit init {} pozri PrcoCustomHostnameVerifier a porovnaj java -> kotlin
    override val tasks: Map<EshopUuid, TaskContext>
        get() {
            return internalTask
        }

    override val isAnyTaskRunning: Boolean
        get() {
            for (eshopUuid in EshopUuid.values()) {
                if (isTaskRunning(eshopUuid)) {
                    return true
                }
            }
            return false
        }

    @PostConstruct
    fun init() {
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

    private fun createExecutorServiceForEshop(value: EshopUuid): ExecutorService {
        return Executors.newSingleThreadExecutor {
            Thread(it, "${value.name}-thread")
        }
    }

    override fun dajmiho(eshopUuid: EshopUuid): Executor {
        return executors[eshopUuid]!!
    }


    override fun submitTask(eshopUuid: EshopUuid, task: Runnable): Future<*> {
        log.debug("submitting new task for eshop $eshopUuid")
        return executors[eshopUuid]!!.submit(task)
    }

    override fun <T> submitTask(eshopUuid: EshopUuid, task: Callable<T>): Future<T> {
        return executors[eshopUuid]!!.submit(task)
    }


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
        log.debug("marking task for eshop $eshopUuid as ${TaskStatus.RUNNING}")
        internalTask[eshopUuid] = TaskContext(TaskStatus.RUNNING)
    }

    override fun markTaskAsFinished(eshopUuid: EshopUuid, finishedWithError: Boolean) {
        val newTaskStatus = if (finishedWithError) TaskStatus.FINISHED_WITH_ERROR else TaskStatus.FINISHED_OK
        log.debug("marking task for eshop $eshopUuid as $newTaskStatus")
        internalTask[eshopUuid] = TaskContext(newTaskStatus)
    }

    override fun markTaskAsStopped(eshopUuid: EshopUuid) {
        log.debug("marking task for eshop $eshopUuid as ${TaskStatus.STOPPED}")
        internalTask[eshopUuid] = TaskContext(TaskStatus.STOPPED)
    }

    override fun sleepIfNeeded(eshopUuid: EshopUuid) {
        val currentContext = internalTask[eshopUuid]!!
        val currentStatus = currentContext.status

        log.debug("task status $currentContext")
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

/**
 * @param T return type of [doInRunnable] method
 */
abstract class ExceptionHandlingRunnable<T>(prcoObservable: PrcoObservable) : Runnable {

    final override fun run() {
        var result: T? = null
        var error = false
        try {
            result = doInRunnable()

        } catch (e: Exception) {
            handleException(e)
            error = true

        } finally {
            doInFinally(result, error)
        }
    }

    abstract fun doInRunnable(): T

    abstract fun handleException(e: Exception)

    open fun doInFinally(result: T?, error: Boolean) {
        // nothing, can be use for custom logic
    }
}




