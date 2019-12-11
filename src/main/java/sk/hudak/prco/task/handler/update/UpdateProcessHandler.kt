package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.handler.BasicHandler
import sk.hudak.prco.task.update.UpdateProductExecutors
import java.util.*

abstract class UpdateProcessHandler<E : CoreEvent>(prcoObservable: PrcoObservable,
                                                   val updateProductExecutors: UpdateProductExecutors)
    : BasicHandler(prcoObservable) {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProcessHandler::class.java)!!
    }

    protected abstract fun isSpecificType(event: CoreEvent): Boolean

    protected abstract fun handle(event: E)

    protected abstract fun getEshopUuid(event: E): EshopUuid?

    //TODO presunut do CoreEvent !!!
    protected abstract fun getIdentifier(event: E): String

    override fun update(source: Observable?, event: CoreEvent) {
        if (isSpecificType(event)) {
            event as E

            LOG.trace(">> update ${event.javaClass.simpleName}")
            updateProductExecutors.handlerTaskExecutor.submit {
                val eshopUuid = getEshopUuid(event)
                if (eshopUuid != null) {
                    MDC.put("eshop", eshopUuid.toString())
                }
                MDC.put("identifier", getIdentifier(event))

                handle(event)

                if (eshopUuid != null) {
                    MDC.remove("eshop")
                }
                MDC.remove("identifier")
            }
            LOG.trace("<< update ${event.javaClass.simpleName}")
        }
    }
}
