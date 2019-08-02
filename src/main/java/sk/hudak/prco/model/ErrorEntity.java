package sk.hudak.prco.model;

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
import java.util.Objects;

@Entity(name = "ERROR")
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public EshopUuid getEshopUuid() {
        return eshopUuid;
    }

    public void setEshopUuid(EshopUuid eshopUuid) {
        this.eshopUuid = eshopUuid;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFullMsg() {
        return fullMsg;
    }

    public void setFullMsg(String fullMsg) {
        this.fullMsg = fullMsg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorEntity that = (ErrorEntity) o;
        return Objects.equals(id, that.id) &&
                eshopUuid == that.eshopUuid &&
                errorType == that.errorType &&
                Objects.equals(statusCode, that.statusCode) &&
                Objects.equals(message, that.message) &&
                Objects.equals(fullMsg, that.fullMsg) &&
                Objects.equals(url, that.url) &&
                Objects.equals(additionalInfo, that.additionalInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eshopUuid, errorType, statusCode, message, fullMsg, url, additionalInfo);
    }
}
