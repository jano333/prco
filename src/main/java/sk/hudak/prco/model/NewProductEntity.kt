package sk.hudak.prco.model

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.model.core.DbEntity
import java.math.BigDecimal
import javax.persistence.*

/**
 * Novo pridane produkty.
 */
@Entity(name = "NEW_PRODUCT")
class NewProductEntity : DbEntity() {

    @Id
    @GeneratedValue(generator = "NEW_PRODUCT_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "NEW_PRODUCT_SEC", sequenceName = "NEW_PRODUCT_SEC", allocationSize = 1)
    override var id: Long? = null

    /**
     * URL produktu - nastavuje sa pri vytvoreni.
     */
    @Column(nullable = false, unique = true)
    var url: String? = null

    /**
     * Nazov produktu - nastavuje sa pri prvom vytvoreni.
     */
    @Column(nullable = false)
    var name: String? = null

    /**
     * Unikatny identifikator eshopu ku ktoremu patri dany produkt - nastavuje sa pri prvom vytvoreni.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var eshopUuid: EshopUuid? = null

    /**
     * Typ meratelnej jednotky(GRAM, MILILITER, KUS...),
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    @Enumerated(EnumType.STRING)
    var unit: Unit? = null

    /**
     * Hodnota jednotky, napr. 100, 20, ...
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    @Column(precision = 11, scale = 5)
    var unitValue: BigDecimal? = null

    /**
     * Pocet kusov v baleni
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    var unitPackageCount: Int? = null

    /**
     * flag, ktory definuje, ci su vyplnene vsetky udaje (unit, unitValue, unitPackageCount)
     */
    @Column(nullable = false)
    var valid: Boolean? = null

    /**
     * flag, ktory urcuje ci to niekto skontroloval ze su tam naozaj spravne hodnoty
     * (unit, unitValue, a unitPackageCount)
     */
    @Column(nullable = false)
    var confirmValidity: Boolean? = null

    /**
     * URL obrazku, k danemu produktu.
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    var pictureUrl: String? = null
}
