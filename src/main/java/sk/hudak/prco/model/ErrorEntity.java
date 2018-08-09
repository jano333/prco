package sk.hudak.prco.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import sk.hudak.prco.api.ErrorType;
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

@Getter
@Setter
@Entity(name = "ERROR")
@EqualsAndHashCode
public class ErrorEntity extends DbEntity {

    @Id
    @GeneratedValue(generator = "ERROR_SEC", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ERROR_SEC", sequenceName = "ERROR_SEC", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EshopUuid eshopUuid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    private String statusCode;

    private String message;

    @Column(length = 4000)
    private String fullMsg;

    private String url;

    private String additionalInfo;

}
