package sk.hudak.prco.task.ng.ee.handlers

import org.slf4j.MDC
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import java.util.function.Supplier

abstract class BasicHandler(val prcoObservable: PrcoObservable)
    : PrcoObserver {

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

}

class EshopLogSupplier<T>(val eshopUuid: EshopUuid, private val identifier: String, private val original: Supplier<T>) : Supplier<T> {
    override fun get(): T {
        MDC.put("eshop", eshopUuid.toString())
        MDC.put("identifier", identifier)
        val result = original.get()
        MDC.remove("eshop")
        MDC.remove("identifier")
        return result
    }
}

class NoEshopLogSupplier<T>(private val identifier: String, private val original: Supplier<T>) : Supplier<T> {
    override fun get(): T {
        MDC.put("eshop", "not-defined")
        MDC.put("identifier", identifier)
        val result = original.get()
        MDC.remove("eshop")
        MDC.remove("identifier")
        return result
    }
}
