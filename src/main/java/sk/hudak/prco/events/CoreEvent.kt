package sk.hudak.prco.events

import sk.hudak.prco.api.EshopUuid
import java.util.*

enum class EventType {
    ADD_NEW_PRODUCT,
    UPDATE_PRODUCT,
    ESHOP_KEYWORD_FINISH
}

abstract class CoreEvent(val eventType: EventType) {
    private val created: Date = Date()

    override fun toString(): String {
        return "CoreEvent(eventType=$eventType, created=$created)"
    }
}

data class EshopKeywordFinish(val eshopUuid: EshopUuid,
                         val keyword: String,
                         val countOfFound: Int,
                         val countOfAdded: Int)
    : CoreEvent(EventType.ESHOP_KEYWORD_FINISH)

interface PrcoObserver : Observer {

    override fun update(source: Observable?, event: Any?) {
        if (event is CoreEvent) {
            update(source, event)
        }
    }

    fun update(source: Observable?, event: CoreEvent)
}


