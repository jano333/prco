package sk.hudak.prco.manager.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.manager.EshopThreadStatisticManager
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopScheduledExecutor
import sk.hudak.prco.task.add.AddProductExecutors
import sk.hudak.prco.task.old.EshopTaskManager
import sk.hudak.prco.task.old.TaskStatus
import java.util.*
import java.util.concurrent.ScheduledExecutorService
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

        //TODO
        log.trace("hadlers info:")
        //TODO vsetky?
        EshopUuid.values()
//        Arrays.asList(EshopUuid.FEEDO, EshopUuid.MALL)
                .forEach { eshopUuid ->
                    val eshopExecutor: ScheduledExecutorService = addExecutors.getEshopExecutor(eshopUuid)
                    eshopExecutor as EshopScheduledExecutor
                    if (eshopExecutor.activeCount != 0 ||
                            eshopExecutor.completedTaskCount != 0L ||
                            eshopExecutor.taskCount != 0L) {

                        val running: Boolean = eshopExecutor.activeCount != 0
                        var state: String
                        if (running) {
                            state = "running"
                        }
                        if (eshopExecutor.completedTaskCount != eshopExecutor.taskCount) {
                            state = "waiting"
                        } else {
                            state = "completed"
                        }

                        log.debug("$eshopUuid executor: $state, ${eshopExecutor.completedTaskCount} of ${eshopExecutor.taskCount} are completed ")

                    }
                }

        addExecutors.handlerTaskExecutor as ThreadPoolExecutor
        log.trace("handlerTaskExecutor active/max cout: ${addExecutors.handlerTaskExecutor.activeCount}/${addExecutors.handlerTaskExecutor.maximumPoolSize} ")
        addExecutors.searchUrlBuilderExecutor as ThreadPoolExecutor
        log.trace("searchUrlBuilderExecutor active/max cout: ${addExecutors.searchUrlBuilderExecutor.activeCount}/${addExecutors.searchUrlBuilderExecutor.maximumPoolSize} ")
        addExecutors.internalServiceExecutor as ThreadPoolExecutor
        log.trace("internalServiceExecutor active/max cout: ${addExecutors.internalServiceExecutor.activeCount}/${addExecutors.internalServiceExecutor.maximumPoolSize} ")
        addExecutors.eshopUuidParserExecutor as ThreadPoolExecutor
        log.trace("eshopUuidParserExecutor active/max cout: ${addExecutors.eshopUuidParserExecutor.activeCount}/${addExecutors.eshopUuidParserExecutor.maximumPoolSize} ")
        addExecutors.htmlParserExecutor as ThreadPoolExecutor
        log.trace("htmlParserExecutor active/max cout: ${addExecutors.htmlParserExecutor.activeCount}/${addExecutors.htmlParserExecutor.maximumPoolSize} ")


    }
}
