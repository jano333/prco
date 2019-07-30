package sk.hudak.prco.parser.impl

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.exception.EshopNotFoundPrcoException
import sk.hudak.prco.parser.EshopUuidParser
import java.util.*

@Component
class EnumImplEshopUuidParserImpl : EshopUuidParser {

    // TODO prerobit na impl z db, kde bude ulozene zaciatok

    override fun parseEshopUuid(productUrl: String): EshopUuid {
        return Arrays.stream(EshopUuid.values())
                .filter { eshopUuid -> productUrl.startsWith(eshopUuid.productStartUrl) }
                .findFirst()
                .orElseThrow { EshopNotFoundPrcoException(productUrl) }
    }


}