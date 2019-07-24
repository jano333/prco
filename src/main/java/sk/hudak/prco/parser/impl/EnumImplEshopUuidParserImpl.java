package sk.hudak.prco.parser.impl;

import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.exception.EshopNotFoundPrcoException;
import sk.hudak.prco.parser.EshopUuidParser;

import java.util.Arrays;

import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Component
public class EnumImplEshopUuidParserImpl implements EshopUuidParser {

    // TODO prerobit na impl z db, kde bude ulozene zaciatok

    /**
     * Na zaklade url vrati eshop UUID, ak nenajde vyhodi vynimku
     *
     * @param productUrl
     * @return
     */
    @Override
    public EshopUuid parseEshopUuid(String productUrl) {
        notNullNotEmpty(productUrl, "productUrl");

        return Arrays.stream(EshopUuid.values())
                .filter(eshopUuid -> productUrl.startsWith(eshopUuid.getProductStartUrl()))
                .findFirst()
                .orElseThrow(() -> new EshopNotFoundPrcoException(productUrl));
    }
}
