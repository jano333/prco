package sk.hudak.prco.events

import org.springframework.stereotype.Component
import java.util.*

@Component
class PrcoObservable : Observable() {

    fun notify(event: CoreEvent) {
        // nastavim ze nastala zmena
        setChanged()
        // notifigujem vsetkych zaregistrovanych pozorovatelov (observerov)
        notifyObservers(event)
    }
}