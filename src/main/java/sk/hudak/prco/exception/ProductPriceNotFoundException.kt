package sk.hudak.prco.exception

class ProductPriceNotFoundException(productUrl: String) :
        PrcoRuntimeException("Product price for url '$productUrl' not found.")