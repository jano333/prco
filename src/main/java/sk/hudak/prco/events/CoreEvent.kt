package sk.hudak.prco.events

import java.util.*

/**
 * Marker for start event
 */
interface StartEvent

/**
 * Marker for final event
 */
interface FinalEvent

abstract class CoreEvent {
    private val created: Date = Date()

    override fun toString(): String {
        return "CoreEvent(created=$created)"
    }
}

/**
 * basic error event
 */
//FIXME prerobit na class ktory extenduje CoreEvent !!!
interface ErrorEvent {
    val event: CoreEvent
    val error: Throwable
}

interface UpdateErrorEvent : ErrorEvent


interface PrcoObserver : Observer {

    override fun update(source: Observable?, event: Any?) {
        if (event is CoreEvent) {
            update(source, event)
        }
    }

    fun update(source: Observable?, event: CoreEvent)
}


