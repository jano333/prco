package sk.hudak.prco.console

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.dto.product.ProductFullDto
import sk.hudak.prco.service.InternalTxService

@Component
class PrcoConsole(val internalTxService: InternalTxService) {

    fun showInConsole() {
        showProductPerEshop(EshopUuid.KID_MARKET)


//        showProductIdBuUrl("https://www.drmax.sk/nutrilon-3-pronutra/")
//        showProductInfoById(3959L)
//        updateProductUnitPackageCount(3959L, 1)
    }

    private fun showProductPerEshop(eshopUuid: EshopUuid) {
        println("product for eshop $eshopUuid")
        internalTxService.findProducts(ProductFilterUIDto.withEshopOnly(eshopUuid).orderBy(ProductFilterUIDto.ORDER_BY.NAME))
                .forEach {
                    println("${it.name} ${it.url} ${it.id}")
                }
    }

    private fun updateProductUnitPackageCount(productId: Long, unitPackageCount: Int) {
        internalTxService.updateProductUnitPackageCount(productId, unitPackageCount)
    }

    private fun showProductInfoById(productId: Long) {

        val productById: ProductFullDto = internalTxService.getProductById(productId)
        println(productById.toString())

    }


    private fun showProductIdBuUrl(productURL: String) {
        val productIdWithUrl: Long? = internalTxService.findProductIdWithUrl(productURL, null)
        print("$productIdWithUrl id for url $productURL")
    }

}