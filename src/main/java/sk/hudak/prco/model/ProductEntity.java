package sk.hudak.prco.model;

import lombok.Getter;
import lombok.Setter;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.model.core.DbEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Produkty, o ktore mam zaujem a aktualizujem ich cenu.
 */
@Getter
@Setter
@Entity(name = "PRODUCT" )
public class ProductEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "PRODUCT_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "PRODUCT_SEC", sequenceName = "PRODUCT_SEC", allocationSize = 1)
    private Long id;

    /**
     * URL produktu
     * - nastavuje sa raz iba pri prvom vytvoreni.
     */
    @Column(nullable = false, unique = true)
    private String url;

    /**
     * Unikatny identifikator eshopu ku ktoremu patri dany produkt
     * - nastavuje sa raz iba pri prvom vytvoreni
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EshopUuid eshopUuid;

    /**
     * Typ meratelnej jednotky(GRAM, MILILITER, KUS...)
     * - nastavuje sa raz iba pri prvom vytvoreni
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Unit unit;

    /**
     * Nazov produktu
     * - prvy krat sa nastavuje pri vytvoreni, nasledne aktualizuje cez update job
     */
    @Column(nullable = false)
    private String name;

    /**
     * Hodnota jednotky, napr. 100, 20, ...
     */
    @Column(nullable = false, precision = 11, scale = 5)
    private BigDecimal unitValue;

    /**
     * Pocet kusov v baleni
     */
    @Column(nullable = false)
    private Integer unitPackageCount;

    // cena za balenie(z eshopu)
    @Column(precision = 11, scale = 5)
    private BigDecimal priceForPackage;

    // cena dopocitavana
    @Column(precision = 11, scale = 5)
    private BigDecimal priceForOneItemInPackage;

    // cena dopocitavana
    @Column(precision = 11, scale = 5)
    private BigDecimal priceForUnit;

    // nastavijem podla potreby aka je bezna cena daneho vyrobku... aby som vedel realne povedat aka zlava je...
    @Column(precision = 11, scale = 5)
    private BigDecimal commonPrice;

    // kedy naposledy bol robeny update informacii(cena, nazov,...) o danom produkte
    private Date lastTimeDataUpdated;

    // typ akcie
    @Enumerated(EnumType.STRING)
    private ProductAction productAction;

    // platnost akcie do
    private Date actionValidTo;

    // URL na obrazok produktu
    private String productPictureUrl;


}
