package sk.hudak.prco.events

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class PrcoObservable : Observable() {

    companion object {
        private val LOG = LoggerFactory.getLogger(PrcoObservable::class.java)!!
    }

    // toto tu musi byt lebo sa stavalo ze niektore hadlery vobed neboli zavolane lebo prva metoda to nastavila v jednom vlakne a druha to vypla v inom...
    @Synchronized
    fun notify(event: CoreEvent) {
        LOG.trace(">> notify")
        // nastavim ze nastala zmena
        setChanged()
        // notifigujem vsetkych zaregistrovanych pozorovatelov (observerov)
        notifyObservers(event)
        LOG.trace("<< notify")
    }
}