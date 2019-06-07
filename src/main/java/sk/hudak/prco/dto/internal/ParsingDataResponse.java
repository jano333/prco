package sk.hudak.prco.dto.internal;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ParsingDataResponse {

    private ProductUpdateData productUpdateData;
    private Exception error;


    public ParsingDataResponse(@NonNull ProductUpdateData productUpdateData) {
        this.productUpdateData = productUpdateData;
    }

    public ParsingDataResponse(@NonNull Exception error) {
        this.error = error;
    }

    public boolean isError() {
        return error != null;
    }
}
