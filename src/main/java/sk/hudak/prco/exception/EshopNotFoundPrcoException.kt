package sk.hudak.prco.exception;

import lombok.Getter;
import sk.hudak.prco.api.EshopUuid;

public class EshopNotFoundPrcoException extends PrcoRuntimeException {

    @Getter
    private String productUrl;

    public EshopNotFoundPrcoException(String productUrl) {
        super(EshopUuid.class.getSimpleName() + " for " + productUrl + " not found");
    }
}
