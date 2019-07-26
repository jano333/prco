package sk.hudak.prco.exception


import sk.hudak.prco.api.EshopUuid

class EshopParserNotFoundPrcoException(val eshopUuid: EshopUuid) :
        PrcoRuntimeException("Parser implementation for eshop $eshopUuid not found.")
