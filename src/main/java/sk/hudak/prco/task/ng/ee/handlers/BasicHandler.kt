package sk.hudak.prco.task.ng.ee.handlers

import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver

abstract class BasicHandler(val prcoObservable: PrcoObservable)
    : PrcoObserver {

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

}