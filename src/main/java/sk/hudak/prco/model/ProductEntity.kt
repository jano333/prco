package sk.hudak.prco.model

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.Unit
import sk.hudak.prco.model.core.DbEntity
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

/**
 * Produkty, o ktore mam zaujem a aktualizujem ich cenu.
 */
@Entity(name = "PRODUCT")
class ProductEntity : DbEntity() {

    @Id
    @GeneratedValue(generator = "PRODUCT_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "PRODUCT_SEC", sequenceName = "PRODUCT_SEC", allocationSize = 1)
    override var id: Long? = null

    /**
     * URL produktu
     */
    @Column(nullable = false, unique = true)
    var url: String? = null

    /**
     * Unikatny identifikator eshopu ku ktoremu patri dany produkt
     * - nastavuje sa raz iba pri prvom vytvoreni
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var eshopUuid: EshopUuid? = null

    /**
     * Typ meratelnej jednotky(GRAM, MILILITER, KUS...)
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var unit: Unit? = null

    /**
     * Nazov produktu
     * - prvy krat sa nastavuje pri vytvoreni, nasledne aktualizuje cez update job
     */
    @Column(nullable = false)
    var name: String? = null

    /**
     * Hodnota jednotky, napr. 100, 20, ...
     */
    @Column(nullable = false, precision = 11, scale = 5)
    var unitValue: BigDecimal? = null

    /**
     * Pocet kusov v baleni
     */
    @Column(nullable = false)
    var unitPackageCount: Int? = null

    // cena za balenie(z eshopu)
    @Column(precision = 11, scale = 5)
    var priceForPackage: BigDecimal? = null

    // cena dopocitavana
    @Column(precision = 11, scale = 5)
    var priceForOneItemInPackage: BigDecimal? = null

    // cena dopocitavana
    @Column(precision = 11, scale = 5)
    var priceForUnit: BigDecimal? = null

    // nastavujem podla potreby aka je bezna cena daneho vyrobku... aby som vedel realne povedat aka zlava je...
    @Column(precision = 11, scale = 5)
    var commonPrice: BigDecimal? = null

    // kedy naposledy bol robeny update informacii(cena, nazov,...) o danom produkte
    var lastTimeDataUpdated: Date? = null

    // typ akcie
    @Enumerated(EnumType.STRING)
    var productAction: ProductAction? = null

    // platnost akcie do
    var actionValidTo: Date? = null

    // URL na obrazok produktu
    var productPictureUrl: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ProductEntity?
        return id == that!!.id &&
                url == that.url &&
                eshopUuid == that.eshopUuid &&
                unit === that.unit &&
                name == that.name &&
                unitValue == that.unitValue &&
                unitPackageCount == that.unitPackageCount &&
                priceForPackage == that.priceForPackage &&
                priceForOneItemInPackage == that.priceForOneItemInPackage &&
                priceForUnit == that.priceForUnit &&
                commonPrice == that.commonPrice &&
                lastTimeDataUpdated == that.lastTimeDataUpdated &&
                productAction === that.productAction &&
                actionValidTo == that.actionValidTo &&
                productPictureUrl == that.productPictureUrl
    }

    override fun hashCode(): Int {
        return Objects.hash(id, url, eshopUuid, unit, name, unitValue, unitPackageCount, priceForPackage, priceForOneItemInPackage, priceForUnit, commonPrice, lastTimeDataUpdated, productAction, actionValidTo, productPictureUrl)
    }
}
