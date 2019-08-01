package sk.hudak.prco.model

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.model.core.DbEntity
import java.math.BigDecimal
import javax.persistence.*

/**
 * Produkty, o ktore nemam zaujem.
 */
@Entity(name = "NOT_ITERESTED_PRODUCT")
class NotInterestedProductEntity : DbEntity() {

    @Id
    @GeneratedValue(generator = "NOT_ITERESTED_PRODUCT_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "NOT_ITERESTED_PRODUCT_SEC", sequenceName = "NOT_ITERESTED_PRODUCT_SEC", allocationSize = 1)
    override var id: Long? = null

    @Column(nullable = false, unique = true)
    var url: String? = null

    /**
     * Nazov produktu
     */
    @Column(nullable = false)
    var name: String? = null

    /**
     * Unikatny identifikator eshopu ku ktoremu patri dany produkt
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var eshopUuid: EshopUuid? = null

    /**
     * Typ meratelnej jednotky(GRAM, MILILITER, KUS...)
     */
    @Enumerated(EnumType.STRING)
    var unit: Unit? = null

    /**
     * Hodnota jednotky, napr. 100, 20, ...
     */
    @Column(precision = 11, scale = 5)
    var unitValue: BigDecimal? = null

    /**
     * Pocet kusov v baleni
     */
    var unitPackageCount: Int? = null
}
