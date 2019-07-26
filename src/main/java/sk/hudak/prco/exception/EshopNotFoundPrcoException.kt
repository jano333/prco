package sk.hudak.prco.exception

import sk.hudak.prco.api.EshopUuid

class EshopNotFoundPrcoException(val productUrl: String) :
        PrcoRuntimeException(EshopUuid::class.java.simpleName + " for $productUrl not found")
