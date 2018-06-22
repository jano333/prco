package sk.hudak.prco.model;

import lombok.Getter;
import lombok.Setter;
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
 * Produkty, o ktore nemam zaujem.
 */
@Getter
@Setter
@Entity(name = "NOT_ITERESTED_PRODUCT")
public class NotInterestedProductEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "NOT_ITERESTED_PRODUCT_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "NOT_ITERESTED_PRODUCT_SEC", sequenceName = "NOT_ITERESTED_PRODUCT_SEC", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    /**
     * Nazov produktu
     */
    @Column(nullable = false)
    private String name;

    /**
     * Unikatny identifikator eshopu ku ktoremu patri dany produkt
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EshopUuid eshopUuid;

    /**
     * Typ meratelnej jednotky(GRAM, MILILITER, KUS...)
     */
    @Enumerated(EnumType.STRING)
    private Unit unit;

    /**
     * Hodnota jednotky, napr. 100, 20, ...
     */
    @Column(precision = 11, scale = 5)
    private BigDecimal unitValue;

    /**
     * Pocet kusov v baleni
     */
    private Integer unitPackageCount;
}
