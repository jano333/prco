package sk.hudak.prco.task.ng.ee.handlers.addprocess

import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.handlers.BasicHandler

abstract class AddProcessHandler(prcoObservable: PrcoObservable,
                                  val addProductExecutors: AddProductExecutors)
    : BasicHandler(prcoObservable)