package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.BasicHandler
import sk.hudak.prco.manager.add.event.AddProductExecutors
import java.util.*

abstract class AddProcessHandler<E : CoreEvent>(prcoObservable: PrcoObservable,
                                                val addProductExecutors: AddProductExecutors)
    : BasicHandler(prcoObservable) {

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProcessHandler::class.java)!!
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
            addProductExecutors.handlerTaskExecutor.submit {
                val eshopUuid = getEshopUuid(event)
                if (eshopUuid != null) {
                    MDC.put("eshop", eshopUuid.toString())
                }
                MDC.put("identifier", getIdentifier(event))

                LOG.trace("handle $event")
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