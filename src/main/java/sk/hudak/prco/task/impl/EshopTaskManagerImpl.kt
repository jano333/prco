package sk.hudak.prco.task.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.TaskContext
import sk.hudak.prco.task.TaskStatus
import sk.hudak.prco.utils.ThreadUtils
import sk.hudak.prco.utils.ThreadUtils.generateRandomSecondInInterval
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class EshopTaskManagerImpl : EshopTaskManager {

    companion object {
        val log = LoggerFactory.getLogger(EshopTaskManagerImpl::class.java)
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
        Arrays.stream(EshopUuid.values()).forEach { eshopUuid ->
            executors[eshopUuid] = createExecutorServiceForEshop(eshopUuid)
            internalTask[eshopUuid] = TaskContext(TaskStatus.STOPPED)
        }
    }

    @PreDestroy
    fun tearDown() {
        for (eshopUuid in executors.keys) {
            executors[eshopUuid]?.shutdownNow()
        }
    }

    private fun createExecutorServiceForEshop(value: EshopUuid): ExecutorService {
        return Executors.newSingleThreadExecutor { r -> Thread(r, value.name) }
    }

    override fun submitTask(eshopUuid: EshopUuid, task: Runnable): Future<*> {
        log.debug("submitting new task for eshop $eshopUuid")
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

            val secondInInterval = generateRandomSecondInInterval()

            if (secondsBetween < secondInInterval) {
                ThreadUtils.sleepSafe(secondInInterval)
            }
        }
    }
}
