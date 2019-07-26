package sk.hudak.prco.task

import sk.hudak.prco.api.EshopUuid
import java.util.concurrent.Future

interface EshopTaskManager {

    val tasks: Map<EshopUuid, TaskContext>

    val isAnyTaskRunning: Boolean

    fun submitTask(eshopUuid: EshopUuid, task: Runnable): Future<*>

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
