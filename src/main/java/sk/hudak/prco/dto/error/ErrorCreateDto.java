package sk.hudak.prco.dto.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.DtoAble;

@Setter
@Getter
@Builder
public class ErrorCreateDto implements DtoAble {

    private EshopUuid eshopUuid;
    private ErrorType errorType;
    private String statusCode;
    //TODO impl validation cez validation spring framework a moje vynimky
//    @NotNull
    private String message;
    private String fullMsg;
    private String url;
    private String additionalInfo;
}
