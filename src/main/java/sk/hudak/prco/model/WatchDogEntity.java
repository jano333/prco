package sk.hudak.prco.model;

import sk.hudak.prco.api.EshopUuid;
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

@Entity(name = "WATCH_DOG")
public class WatchDogEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "WATCH_DOG_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "WATCH_DOG_SEC", sequenceName = "WATCH_DOG_SEC", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EshopUuid eshopUuid;

    @Column(nullable = false, precision = 11, scale = 5)
    private BigDecimal maxPriceToBeInterestedIn;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public EshopUuid getEshopUuid() {
        return eshopUuid;
    }

    public void setEshopUuid(EshopUuid eshopUuid) {
        this.eshopUuid = eshopUuid;
    }

    public BigDecimal getMaxPriceToBeInterestedIn() {
        return maxPriceToBeInterestedIn;
    }

    public void setMaxPriceToBeInterestedIn(BigDecimal maxPriceToBeInterestedIn) {
        this.maxPriceToBeInterestedIn = maxPriceToBeInterestedIn;
    }
}
