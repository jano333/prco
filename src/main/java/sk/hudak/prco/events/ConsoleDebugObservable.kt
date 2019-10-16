package sk.hudak.prco.events

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.kotlin.color
import sk.hudak.prco.manager.addprocess.AddProductsToEshopByKeywordFinishedEvent
import sk.hudak.prco.utils.ConsoleColor
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
            val color = if (event.countOfFound == 0) {
                ConsoleColor.RED
            } else {
                ConsoleColor.GREEN
            }
            log.debug("${AddProductsToEshopByKeywordFinishedEvent::class.java.simpleName}(${event.eshopUuid})".color(color))
            log.debug("keyword: ${event.keyword.color(color)}")
            log.debug("err msg: ${event.errMsg}".color(color))
            log.debug("count of found: ${event.countOfFound}".color(color))
            log.debug("count of added: ${event.countOfAdded}".color(color))
        } else {
            log.debug("${AddProductsToEshopByKeywordFinishedEvent::class.java.simpleName}(${event.eshopUuid})".color(ConsoleColor.GREEN))
            log.debug("keyword: " + event.keyword.color(ConsoleColor.GREEN))
            log.debug("count of found: ${event.countOfFound}".color(ConsoleColor.YELLOW))
            log.debug("count of added: ${event.countOfAdded}".color(ConsoleColor.YELLOW))
        }
    }

}