package sk.hudak.prco.model;

import sk.hudak.prco.api.EshopUuid;
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

/**
 * Novo pridane produkty.
 */
@Entity(name = "NEW_PRODUCT")
public class NewProductEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "NEW_PRODUCT_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "NEW_PRODUCT_SEC", sequenceName = "NEW_PRODUCT_SEC", allocationSize = 1)
    private Long id;

    /**
     * URL produktu - nastavuje sa pri vytvoreni.
     */
    @Column(nullable = false, unique = true)
    private String url;

    /**
     * Nazov produktu - nastavuje sa pri prvom vytvoreni.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Unikatny identifikator eshopu ku ktoremu patri dany produkt - nastavuje sa pri prvom vytvoreni.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EshopUuid eshopUuid;

    /**
     * Typ meratelnej jednotky(GRAM, MILILITER, KUS...),
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    @Enumerated(EnumType.STRING)
    private Unit unit;

    /**
     * Hodnota jednotky, napr. 100, 20, ...
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    @Column(precision = 11, scale = 5)
    private BigDecimal unitValue;

    /**
     * Pocet kusov v baleni
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    private Integer unitPackageCount;

    /**
     * flag, ktory definuje, ci su vyplnene vsetky udaje (unit, unitValue, unitPackageCount)
     */
    @Column(nullable = false)
    private Boolean valid;

    /**
     * flag, ktory urcuje ci to niekto skontroloval ze su tam naozaj spravne hodnoty
     * (unit, unitValue, a unitPackageCount)
     */
    @Column(nullable = false)
    private Boolean confirmValidity;

    /**
     * URL obrazku, k danemu produktu.
     * nepovinne, lebo sa nemuselo podarit vyparsovat.
     */
    private String pictureUrl;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getConfirmValidity() {
        return confirmValidity;
    }

    public void setConfirmValidity(Boolean confirmValidity) {
        this.confirmValidity = confirmValidity;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
