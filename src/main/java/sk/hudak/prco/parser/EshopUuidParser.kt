package sk.hudak.prco.parser

import sk.hudak.prco.api.EshopUuid

interface EshopUuidParser {

    fun parseEshopUuid(productUrl: String): EshopUuid
}
