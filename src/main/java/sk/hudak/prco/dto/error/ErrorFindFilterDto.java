package sk.hudak.prco.dto.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.hudak.prco.api.ErrorType;

@ToString
@Getter
@Setter
@Builder
public class ErrorFindFilterDto {

    public static final int DEFAULT_COUNT_LIMIT = 50;

    private int limit = DEFAULT_COUNT_LIMIT;

    private ErrorType[] errorTypes;
    private ErrorType[] errorTypesToSkip;

    private String[] statusCodes;
    private String[] statusCodesToSkip;

}
