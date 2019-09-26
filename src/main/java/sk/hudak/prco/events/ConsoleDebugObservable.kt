package sk.hudak.prco.events

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.manager.addprocess.AddProductsToEshopByKeywordFinishedEvent
import sk.hudak.prco.utils.ConsoleColor
import sk.hudak.prco.utils.ConsoleWithColor
import java.util.*

@Component
class ConsoleDebugObservable(prcoObservable: PrcoObservable) : PrcoObserver {

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    companion object {
        val log = LoggerFactory.getLogger(ConsoleDebugObservable::class.java)!!
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is AddProductsToEshopByKeywordFinishedEvent -> {
                processEshopKeywordFinishEvent(event)
            }
        }
    }

    private fun processEshopKeywordFinishEvent(event: AddProductsToEshopByKeywordFinishedEvent) {
        if (event.error) {
            log.debug(ConsoleWithColor.wrapWithColor(AddProductsToEshopByKeywordFinishedEvent::class.java.simpleName + "(" + event.eshopUuid + ")", ConsoleColor.RED))
            log.debug(ConsoleWithColor.wrapWithColor("err msg: " + event.errMsg, ConsoleColor.RED))
            log.debug(ConsoleWithColor.wrapWithColor("count of found: ${event.countOfFound}", ConsoleColor.RED))
            log.debug(ConsoleWithColor.wrapWithColor("count of added: ${event.countOfAdded}", ConsoleColor.RED))
        } else {
            log.debug(ConsoleWithColor.wrapWithColor(AddProductsToEshopByKeywordFinishedEvent::class.java.simpleName + "(" + event.eshopUuid + ")", ConsoleColor.GREEN))
            log.debug(ConsoleWithColor.wrapWithColor("count of found: ${event.countOfFound}", ConsoleColor.YELLOW))
            log.debug(ConsoleWithColor.wrapWithColor("count of added: ${event.countOfAdded}", ConsoleColor.YELLOW))
        }
    }

}