package sk.hudak.prco.dto.error;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;

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
}
