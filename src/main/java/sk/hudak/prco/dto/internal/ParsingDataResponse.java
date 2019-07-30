package sk.hudak.prco.dto.internal;

public class ParsingDataResponse {

    private ProductUpdateData productUpdateData;
    private Exception error;

    public ParsingDataResponse(ProductUpdateData productUpdateData) {
        this.productUpdateData = productUpdateData;
    }

    public ParsingDataResponse(Exception error) {
        this.error = error;
    }

    public boolean isError() {
        return error != null;
    }

    public ProductUpdateData getProductUpdateData() {
        return productUpdateData;
    }

    public Exception getError() {
        return error;
    }
}
