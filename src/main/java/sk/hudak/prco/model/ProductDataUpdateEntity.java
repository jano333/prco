package sk.hudak.prco.model;

import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
@Entity(name = "PRODUCT")
public class ProductDataUpdateEntity extends DbEntity {

    @Id
    private Long id;

    private String name;

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

}
