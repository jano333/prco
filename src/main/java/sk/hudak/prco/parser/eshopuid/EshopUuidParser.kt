package sk.hudak.prco.parser.eshopuid

import sk.hudak.prco.api.EshopUuid

interface EshopUuidParser {

    fun parseEshopUuid(productUrl: String): EshopUuid
}
