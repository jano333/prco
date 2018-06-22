package sk.hudak.prco.parser;

import sk.hudak.prco.api.EshopUuid;


public interface EshopUuidParser {

    EshopUuid parseEshopUuid(String productUrl);
}
