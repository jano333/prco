package sk.hudak.prco.model;

import org.jetbrains.annotations.Nullable;
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
import java.util.Objects;

/**
 * Produkty, o ktore mam zaujem a aktualizujem ich cenu.
 */
@Entity(name = "PRODUCT")
public class ProductEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "PRODUCT_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "PRODUCT_SEC", sequenceName = "PRODUCT_SEC", allocationSize = 1)
    private Long id;

    /**
     * URL produktu
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

    // nastavujem podla potreby aka je bezna cena daneho vyrobku... aby som vedel realne povedat aka zlava je...
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

    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public EshopUuid getEshopUuid() {
        return eshopUuid;
    }

    public void setEshopUuid(EshopUuid eshopUuid) {
        this.eshopUuid = eshopUuid;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    public Integer getUnitPackageCount() {
        return unitPackageCount;
    }

    public void setUnitPackageCount(Integer unitPackageCount) {
        this.unitPackageCount = unitPackageCount;
    }

    public BigDecimal getPriceForPackage() {
        return priceForPackage;
    }

    public void setPriceForPackage(BigDecimal priceForPackage) {
        this.priceForPackage = priceForPackage;
    }

    public BigDecimal getPriceForOneItemInPackage() {
        return priceForOneItemInPackage;
    }

    public void setPriceForOneItemInPackage(BigDecimal priceForOneItemInPackage) {
        this.priceForOneItemInPackage = priceForOneItemInPackage;
    }

    public BigDecimal getPriceForUnit() {
        return priceForUnit;
    }

    public void setPriceForUnit(BigDecimal priceForUnit) {
        this.priceForUnit = priceForUnit;
    }

    public BigDecimal getCommonPrice() {
        return commonPrice;
    }

    public void setCommonPrice(BigDecimal commonPrice) {
        this.commonPrice = commonPrice;
    }

    public Date getLastTimeDataUpdated() {
        return lastTimeDataUpdated;
    }

    public void setLastTimeDataUpdated(Date lastTimeDataUpdated) {
        this.lastTimeDataUpdated = lastTimeDataUpdated;
    }

    public ProductAction getProductAction() {
        return productAction;
    }

    public void setProductAction(ProductAction productAction) {
        this.productAction = productAction;
    }

    public Date getActionValidTo() {
        return actionValidTo;
    }

    public void setActionValidTo(Date actionValidTo) {
        this.actionValidTo = actionValidTo;
    }

    public String getProductPictureUrl() {
        return productPictureUrl;
    }

    public void setProductPictureUrl(String productPictureUrl) {
        this.productPictureUrl = productPictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(url, that.url) &&
                eshopUuid == that.eshopUuid &&
                unit == that.unit &&
                Objects.equals(name, that.name) &&
                Objects.equals(unitValue, that.unitValue) &&
                Objects.equals(unitPackageCount, that.unitPackageCount) &&
                Objects.equals(priceForPackage, that.priceForPackage) &&
                Objects.equals(priceForOneItemInPackage, that.priceForOneItemInPackage) &&
                Objects.equals(priceForUnit, that.priceForUnit) &&
                Objects.equals(commonPrice, that.commonPrice) &&
                Objects.equals(lastTimeDataUpdated, that.lastTimeDataUpdated) &&
                productAction == that.productAction &&
                Objects.equals(actionValidTo, that.actionValidTo) &&
                Objects.equals(productPictureUrl, that.productPictureUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, eshopUuid, unit, name, unitValue, unitPackageCount, priceForPackage, priceForOneItemInPackage, priceForUnit, commonPrice, lastTimeDataUpdated, productAction, actionValidTo, productPictureUrl);
    }
}
