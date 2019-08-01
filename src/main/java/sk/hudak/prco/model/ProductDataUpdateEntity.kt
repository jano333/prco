package sk.hudak.prco.model

import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.Unit
import sk.hudak.prco.model.core.DbEntity
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity(name = "PRODUCT")
class ProductDataUpdateEntity : DbEntity() {

    @Id
    override var id: Long? = null

    var name: String? = null

    var url: String? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var unit: Unit? = null

    @Column(nullable = false, precision = 11, scale = 5)
    var unitValue: BigDecimal? = null

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

    // kedy naposledy bol robeny update informacii(cena, nazov,...) o danom produkte
    var lastTimeDataUpdated: Date? = null

    // typ akcie
    @Enumerated(EnumType.STRING)
    var productAction: ProductAction? = null

    // platnost akcie
    var actionValidTo: Date? = null

    // url na obrazok produktu
    var productPictureUrl: String? = null
}
