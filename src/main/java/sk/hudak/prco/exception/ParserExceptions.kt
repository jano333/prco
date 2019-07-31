package sk.hudak.prco.exception

class ProductNameNotFoundException(productUrl: String) :
        PrcoRuntimeException("Product name for url '$productUrl' not found.")

class ProductPriceNotFoundException(productUrl: String) :
        PrcoRuntimeException("Product price for url '$productUrl' not found.")