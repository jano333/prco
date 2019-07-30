package sk.hudak.prco.dto.error;

import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorListDto {

    private Long id;
    private EshopUuid eshopUuid;
    private ErrorType errorType;
    private String statusCode;
    private String message;
    private String fullMsg;
    private String url;
    private String additionalInfo;
    private Date updated;

    public Long getId() {
        return id;
    }

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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String customToString() {
        return new StringBuilder().append(getEshopUuid()).append(" ")
                .append("[").append(getId()).append("] ")
                .append(formatDate(getUpdated())).append(" ")
                .append(getErrorType()).append(" ")
                .append("status: ").append(getStatusCode()).append(" ")
                .append("message: ").append(getMessage()).append(" ")
//                    "fullMessage " + sb.append(getFullMsg() + " " +
                .append("url ").append(getUrl())
                .toString();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
    }

    @Override
    public String toString() {
        return "ErrorListDto{" +
                "id=" + id +
                ", eshopUuid=" + eshopUuid +
                ", errorType=" + errorType +
                ", statusCode='" + statusCode + '\'' +
                ", message='" + message + '\'' +
                ", fullMsg='" + fullMsg + '\'' +
                ", url='" + url + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", updated=" + updated +
                '}';
    }
}
