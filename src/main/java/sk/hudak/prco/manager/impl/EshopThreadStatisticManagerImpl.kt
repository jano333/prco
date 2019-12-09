package sk.hudak.prco.manager.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.manager.EshopThreadStatisticManager
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.TaskStatus
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import java.util.*
import java.util.concurrent.ThreadPoolExecutor

// TODO prerobit cez spring scheduled bean v priavidelnych intervaloch nech zobrazuje statistiku

@Component
class EshopThreadStatisticManagerImpl(
        private val eshopTaskManager: EshopTaskManager,
        private val internalTxService: InternalTxService,
        private val addExecutors: AddProductExecutors)
    : EshopThreadStatisticManager {

    companion object {
        val log = LoggerFactory.getLogger(EshopThreadStatisticManagerImpl::class.java)!!
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

        log.debug("hadlers info:")

        addExecutors.handlerTaskExecutor as ThreadPoolExecutor
        log.debug("handlerTaskExecutor active/max cout: ${addExecutors.handlerTaskExecutor.activeCount}/${addExecutors.handlerTaskExecutor.maximumPoolSize} ")
        addExecutors.searchUrlBuilderExecutor as ThreadPoolExecutor
        log.debug("searchUrlBuilderExecutor active/max cout: ${addExecutors.searchUrlBuilderExecutor.activeCount}/${addExecutors.searchUrlBuilderExecutor.maximumPoolSize} ")
        addExecutors.internalServiceExecutor as ThreadPoolExecutor
        log.debug("internalServiceExecutor active/max cout: ${addExecutors.internalServiceExecutor.activeCount}/${addExecutors.internalServiceExecutor.maximumPoolSize} ")
        addExecutors.eshopUuidParserExecutor as ThreadPoolExecutor
        log.debug("eshopUuidParserExecutor active/max cout: ${addExecutors.eshopUuidParserExecutor.activeCount}/${addExecutors.eshopUuidParserExecutor.maximumPoolSize} ")
        addExecutors.htmlParserExecutor as ThreadPoolExecutor
        log.debug("htmlParserExecutor active/max cout: ${addExecutors.htmlParserExecutor.activeCount}/${addExecutors.htmlParserExecutor.maximumPoolSize} ")
    }
}
