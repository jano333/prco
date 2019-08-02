package sk.hudak.prco.model;

import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.model.core.DbEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "PRODUCT")
public class ProductDataUpdateEntity extends DbEntity {

    @Id
    private Long id;

    private String name;

    private String url;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Unit unit;

    @Column(nullable = false, precision = 11, scale = 5)
    private BigDecimal unitValue;

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

    // kedy naposledy bol robeny update informacii(cena, nazov,...) o danom produkte
    private Date lastTimeDataUpdated;

    // typ akcie
    @Enumerated(EnumType.STRING)
    private ProductAction productAction;

    // platnost akcie
    private Date actionValidTo;

    // url na obrazok produktu
    private String productPictureUrl;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
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
}
