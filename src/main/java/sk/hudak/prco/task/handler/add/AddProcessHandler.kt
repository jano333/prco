package sk.hudak.prco.task.handler.add

import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.add.AddProductExecutors
import sk.hudak.prco.task.handler.BasicHandler

abstract class AddProcessHandler(prcoObservable: PrcoObservable,
                                  val addProductExecutors: AddProductExecutors)
    : BasicHandler(prcoObservable)