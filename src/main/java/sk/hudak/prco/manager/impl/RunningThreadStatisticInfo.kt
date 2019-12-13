package sk.hudak.prco.manager.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.task.EshopScheduledExecutor
import sk.hudak.prco.task.ProductEshopExecutors
import sk.hudak.prco.task.add.AddProductExecutors
import sk.hudak.prco.task.update.UpdateProductExecutors
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Component
class RunningThreadStatisticInfo(private val addProductExecutors: AddProductExecutors,
                                 private val updateProductExecutors: UpdateProductExecutors,
                                 private val productEshopExecutors: ProductEshopExecutors) {

    companion object {
        val LOG = LoggerFactory.getLogger(RunningThreadStatisticInfo::class.java)!!
    }

    init {
        start(3, TimeUnit.SECONDS)
    }

    private fun start(period: Long, unit: TimeUnit) {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({
            //            showAddProcess()
            showUpdateProcess()
        }, period, period, unit)
    }

    private fun showUpdateProcess() {
        LOG.trace("update handlers info:")
        EshopUuid.values().forEach { eshopUuid ->
            val eshopExecutor: ScheduledExecutorService = productEshopExecutors.getEshopExecutor(eshopUuid)
            eshopExecutor as EshopScheduledExecutor
            if (eshopExecutor.activeCount != 0 ||
                    eshopExecutor.completedTaskCount != 0L ||
                    eshopExecutor.taskCount != 0L) {

                val running: Boolean = eshopExecutor.activeCount != 0
                var state: String
                if (running) {
                    state = "running"
                } else if (eshopExecutor.completedTaskCount != eshopExecutor.taskCount) {
                    state = "waiting"
                } else {
                    state = "completed"
                }

                LOG.debug("$eshopUuid executor: $state, ${eshopExecutor.completedTaskCount} of ${eshopExecutor.taskCount} are completed ")
            }
        }

        updateProductExecutors.handlerTaskExecutor as ThreadPoolExecutor
        LOG.trace("add handlerTaskExecutor active/max cout: ${updateProductExecutors.handlerTaskExecutor.activeCount}/${updateProductExecutors.handlerTaskExecutor.maximumPoolSize} ")
        updateProductExecutors.internalServiceExecutor as ThreadPoolExecutor
        LOG.trace("add internalServiceExecutor active/max cout: ${updateProductExecutors.internalServiceExecutor.activeCount}/${updateProductExecutors.internalServiceExecutor.maximumPoolSize} ")
        updateProductExecutors.htmlParserExecutor as ThreadPoolExecutor
        LOG.trace("add htmlParserExecutor active/max cout: ${updateProductExecutors.htmlParserExecutor.activeCount}/${updateProductExecutors.htmlParserExecutor.maximumPoolSize} ")
    }

    private fun showAddProcess() {
        LOG.trace("add handlers info:")
        EshopUuid.values().forEach { eshopUuid ->
            val eshopExecutor: ScheduledExecutorService = addProductExecutors.getEshopExecutor(eshopUuid)
            eshopExecutor as EshopScheduledExecutor
            if (eshopExecutor.activeCount != 0 ||
                    eshopExecutor.completedTaskCount != 0L ||
                    eshopExecutor.taskCount != 0L) {

                val running: Boolean = eshopExecutor.activeCount != 0
                var state: String
                if (running) {
                    state = "running"
                } else if (eshopExecutor.completedTaskCount != eshopExecutor.taskCount) {
                    state = "waiting"
                } else {
                    state = "completed"
                }
                LOG.debug("$eshopUuid executor: $state, ${eshopExecutor.completedTaskCount} of ${eshopExecutor.taskCount} are completed ")
            }
        }

        addProductExecutors.handlerTaskExecutor as ThreadPoolExecutor
        LOG.trace("add handlerTaskExecutor active/max cout: ${addProductExecutors.handlerTaskExecutor.activeCount}/${addProductExecutors.handlerTaskExecutor.maximumPoolSize} ")
        addProductExecutors.searchUrlBuilderExecutor as ThreadPoolExecutor
        LOG.trace("add searchUrlBuilderExecutor active/max cout: ${addProductExecutors.searchUrlBuilderExecutor.activeCount}/${addProductExecutors.searchUrlBuilderExecutor.maximumPoolSize} ")
        addProductExecutors.internalServiceExecutor as ThreadPoolExecutor
        LOG.trace("add internalServiceExecutor active/max cout: ${addProductExecutors.internalServiceExecutor.activeCount}/${addProductExecutors.internalServiceExecutor.maximumPoolSize} ")
        addProductExecutors.eshopUuidParserExecutor as ThreadPoolExecutor
        LOG.trace("add eshopUuidParserExecutor active/max cout: ${addProductExecutors.eshopUuidParserExecutor.activeCount}/${addProductExecutors.eshopUuidParserExecutor.maximumPoolSize} ")
        addProductExecutors.htmlParserExecutor as ThreadPoolExecutor
        LOG.trace("add htmlParserExecutor active/max cout: ${addProductExecutors.htmlParserExecutor.activeCount}/${addProductExecutors.htmlParserExecutor.maximumPoolSize} ")
    }
}