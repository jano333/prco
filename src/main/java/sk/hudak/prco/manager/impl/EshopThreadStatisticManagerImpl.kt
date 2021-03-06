package sk.hudak.prco.manager.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.manager.EshopThreadStatisticManager
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.TaskStatus
import java.util.*

// TODO prerobit cez spring scheduled bean v priavidelnych intervaloch nech zobrazuje statistiku

@Component
class EshopThreadStatisticManagerImpl(
        private val eshopTaskManager: EshopTaskManager,
        private val internalTxService: InternalTxService)
    : EshopThreadStatisticManager {

    companion object {
        val log = LoggerFactory.getLogger(EshopThreadStatisticManagerImpl::class.java)!!
    }

    override fun startShowingStatistics() {
        val shoudDownStatistic = false
        val thread = Thread {
            val aha = true
            while (aha) {
                doInOneLoop()
                try {
                    Thread.sleep((10 * 1000).toLong())
                } catch (e: InterruptedException) {
                    log.error("thread interrupted " + e.message)
                    Thread.currentThread().interrupt()
                }

                if (shoudDownStatistic) {
                    log.debug("shunting down statistics")
                    Thread.currentThread().interrupt()
                }
            }
        }
        thread.name = "thread-statistic-mng"
        thread.isDaemon = true
        thread.start()
    }

    private fun doInOneLoop() {
        val tasks = eshopTaskManager.tasks

        val running = ArrayList<EshopUuid>(EshopUuid.values().size)
        val finishedOk = ArrayList<EshopUuid>(EshopUuid.values().size)
        val finishedNotOk = ArrayList<EshopUuid>(EshopUuid.values().size)

        for ((eshopUuidKey, value) in tasks) {
            val value = value.status
            if (value == TaskStatus.RUNNING) {
                running.add(eshopUuidKey)
            }
            if (value == TaskStatus.FINISHED_OK) {
                finishedOk.add(eshopUuidKey)
            }
            if (value == TaskStatus.FINISHED_WITH_ERROR) {
                finishedNotOk.add(eshopUuidKey)
            }
        }

        log.debug("all tasks: {}  running: {}, finished(ok/error): {}/{}{}", tasks.size, running.size, finishedOk.size, finishedNotOk.size, finishedNotOk)
        log.debug("error statistic {}", internalTxService.statisticForErrors)
    }
}
