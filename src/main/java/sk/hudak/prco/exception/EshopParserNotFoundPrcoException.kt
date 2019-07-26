package sk.hudak.prco.exception;

import lombok.Getter;
import sk.hudak.prco.api.EshopUuid;

public class EshopParserNotFoundPrcoException extends PrcoRuntimeException {

    @Getter
    private final EshopUuid eshopUuid;

    public EshopParserNotFoundPrcoException(EshopUuid eshopUuid) {
        super("Parser implementation for eshop " + eshopUuid + " not found.");
        this.eshopUuid = eshopUuid;
    }
}
