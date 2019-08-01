package sk.hudak.prco.model

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.model.core.DbEntity
import java.math.BigDecimal
import javax.persistence.*

@Entity(name = "WATCH_DOG")
class WatchDogEntity : DbEntity() {

    @Id
    @GeneratedValue(generator = "WATCH_DOG_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "WATCH_DOG_SEC", sequenceName = "WATCH_DOG_SEC", allocationSize = 1)
    override var id: Long? = null

    @Column(nullable = false, unique = true)
    var productUrl: String? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var eshopUuid: EshopUuid? = null

    @Column(nullable = false, precision = 11, scale = 5)
    var maxPriceToBeInterestedIn: BigDecimal? = null
}
