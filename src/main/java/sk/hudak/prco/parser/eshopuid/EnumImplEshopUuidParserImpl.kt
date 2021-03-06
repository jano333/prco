package sk.hudak.prco.parser.eshopuid

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.exception.EshopNotFoundParserException
import java.util.*

@Component
class EnumImplEshopUuidParserImpl : EshopUuidParser {

    // TODO prerobit na impl z db, kde bude ulozene zaciatok

    override fun parseEshopUuid(productUrl: String): EshopUuid {
        return Arrays.stream(EshopUuid.values())
                .filter { productUrl.startsWith(it.productStartUrl) }
                .findFirst()
                .orElseThrow { EshopNotFoundParserException(productUrl) }
    }


}