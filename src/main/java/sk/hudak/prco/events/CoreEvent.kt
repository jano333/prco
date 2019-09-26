package sk.hudak.prco.events

import java.util.*

abstract class CoreEvent {
    private val created: Date = Date()

    override fun toString(): String {
        return "CoreEvent(created=$created)"
    }
}

interface PrcoObserver : Observer {

    override fun update(source: Observable?, event: Any?) {
        if (event is CoreEvent) {
            update(source, event)
        }
    }

    fun update(source: Observable?, event: CoreEvent)
}


