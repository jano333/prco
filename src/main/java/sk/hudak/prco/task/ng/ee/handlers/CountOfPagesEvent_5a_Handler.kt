package sk.hudak.prco.task.ng.ee.handlers

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.task.ng.ee.CountOfPagesEvent
import sk.hudak.prco.task.ng.ee.Executors
import java.util.*

@Component
class CountOfPagesEvent_5a_Handler(private val prcoObservable: PrcoObservable,
                                   private val executors: Executors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(CountOfPagesEvent_5a_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    private fun handle(event: CountOfPagesEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")
        LOG.debug(event.countOfPages.toString())
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is CountOfPagesEvent -> executors.handlerTaskExecutor.submit { handle(event) }
        }
    }

}
