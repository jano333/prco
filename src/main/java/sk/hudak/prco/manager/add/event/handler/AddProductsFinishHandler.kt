package sk.hudak.prco.manager.add.event.handler

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.BasicHandler
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashMap
import kotlin.collections.HashSet

//@Component
// not use yet, will be?
class AddProductsFinishHandler(prcoObservable: PrcoObservable)
    : BasicHandler(prcoObservable) {

    private val eshopProductUrl = HashMap<EshopUuid, MutableSet<String>>()

    private val myLock = ReentrantLock()

    init {
        EshopUuid.values().forEach {
            if (eshopProductUrl[it] == null) {
                eshopProductUrl[it] = HashSet()
            }
        }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
//            is SearchPageDocumentEvent -> {
//                println("SearchPageDocumentEvent")
//                addNewProductUrl(event.eshopUuid, event.searchUrl)
//            }

            //---------
//            is SaveProductNewDataEvent -> {
//                removeProductURL(event.eshopUuid, event.newProductUrl)
//            }
//            // error eventy
//            is RetrieveDocumentForUrlErrorEvent -> {
//                removeProductURL(event.event.eshopUuid, event.event.newProductUrl)
//            }
//            is ParseProductNewDataErrorEvent -> {
//                removeProductURL(event.event.eshopUuid, event.event.newProductUrl)
//            }
//            is SaveProductNewDataErrorEvent -> {
//                removeProductURL(event.event.productNewData.eshopUuid, event.event.productNewData.url)
//            }
        }
    }

    private fun addNewProductUrl(eshopUuid: EshopUuid, productUrl: String) {
        myLock.lock()
        try {
            eshopProductUrl[eshopUuid]?.add(productUrl)
            countHasChange()
        } finally {
            myLock.unlock()
        }
    }

    private fun removeProductURL(eshopUuid: EshopUuid, productUrl: String) {
        myLock.lock()
        try {
            eshopProductUrl[eshopUuid]?.remove(productUrl)
            countHasChange()
        } finally {
            myLock.unlock()
        }
    }

    private fun doInLock(){
        //TODO
    }

    // volane ak sa zmeni pocet
    private fun countHasChange() {
        println("status")
        println(eshopProductUrl[EshopUuid.FEEDO]?.size)
    }

}