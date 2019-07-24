package sk.hudak.prco.dto.error;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@ToString
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
}
