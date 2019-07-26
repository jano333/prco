package sk.hudak.prco.exception;

public class ProductNameNotFoundException extends PrcoRuntimeException {

    public ProductNameNotFoundException(String productUrl) {
        super("Product name for url '" + productUrl + "' not found.");
    }
}
