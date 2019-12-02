package sk.hudak.prco.task.ng.ee

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.exception.EshopParserNotFoundException
import sk.hudak.prco.parser.eshop.EshopProductsParser

@Component
class EshopProductsParserHelper(productParsers: List<EshopProductsParser>) {

    private val registeredParsers: Map<EshopUuid, EshopProductsParser>

    init {
        registeredParsers = HashMap()
        productParsers.forEach {
            registeredParsers[it.eshopUuid] = it
        }
    }

    fun findParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return registeredParsers[eshopUuid] ?: throw EshopParserNotFoundException(eshopUuid)
    }
}