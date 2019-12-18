package sk.hudak.prco.z.old

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.manager.todo.RunningThreadStatisticInfo
import sk.hudak.prco.service.InternalTxService
import java.util.*

// TODO prerobit cez spring scheduled bean v priavidelnych intervaloch nech zobrazuje statistiku

@Component
class EshopThreadStatisticManagerImpl(
        private val eshopTaskManager: EshopTaskManager,
        private val internalTxService: InternalTxService,
        private val statisticThreadRunningInfo: RunningThreadStatisticInfo)
    : EshopThreadStatisticManager {

    companion object {
        private val LOG = LoggerFactory.getLogger(EshopThreadStatisticManagerImpl::class.java)!!
    }

    override fun start() {
        val shoudDownStatistic = false
        val thread = Thread {
            val aha = true
            while (aha) {
                doInOneLoop()
                try {
                    Thread.sleep((10 * 1000).toLong())
                } catch (e: InterruptedException) {
                    LOG.error("thread interrupted " + e.message)
                    Thread.currentThread().interrupt()
                }

                if (shoudDownStatistic) {
                    LOG.debug("shunting down statistics")
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

        LOG.debug("all tasks: {}  running: {}, finished(ok/error): {}/{}{}", tasks.size, running.size, finishedOk.size, finishedNotOk.size, finishedNotOk)
        LOG.debug("error statistic {}", internalTxService.statisticForErrors)
    }
}
