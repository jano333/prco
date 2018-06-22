package sk.hudak.prco.exception;

public class ProductPriceNotFoundException extends PrcoRuntimeException {

    public ProductPriceNotFoundException(String productUrl) {
        super("Product price for url '" + productUrl + "' not found.");
    }
}