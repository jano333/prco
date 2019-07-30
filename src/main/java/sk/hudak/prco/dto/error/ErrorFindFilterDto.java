package sk.hudak.prco.dto.error;

import lombok.Builder;
import sk.hudak.prco.api.ErrorType;

import java.util.Arrays;

@Builder
public class ErrorFindFilterDto {

    public static final int DEFAULT_COUNT_LIMIT = 50;

    private int limit = DEFAULT_COUNT_LIMIT;

    private ErrorType[] errorTypes;
    private ErrorType[] errorTypesToSkip;

    private String[] statusCodes;
    private String[] statusCodesToSkip;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ErrorType[] getErrorTypes() {
        return errorTypes;
    }

    public void setErrorTypes(ErrorType[] errorTypes) {
        this.errorTypes = errorTypes;
    }

    public ErrorType[] getErrorTypesToSkip() {
        return errorTypesToSkip;
    }

    public void setErrorTypesToSkip(ErrorType[] errorTypesToSkip) {
        this.errorTypesToSkip = errorTypesToSkip;
    }

    public String[] getStatusCodes() {
        return statusCodes;
    }

    public void setStatusCodes(String[] statusCodes) {
        this.statusCodes = statusCodes;
    }

    public String[] getStatusCodesToSkip() {
        return statusCodesToSkip;
    }

    public void setStatusCodesToSkip(String[] statusCodesToSkip) {
        this.statusCodesToSkip = statusCodesToSkip;
    }

    @Override
    public String toString() {
        return "ErrorFindFilterDto{" +
                "limit=" + limit +
                ", errorTypes=" + Arrays.toString(errorTypes) +
                ", errorTypesToSkip=" + Arrays.toString(errorTypesToSkip) +
                ", statusCodes=" + Arrays.toString(statusCodes) +
                ", statusCodesToSkip=" + Arrays.toString(statusCodesToSkip) +
                '}';
    }
}
