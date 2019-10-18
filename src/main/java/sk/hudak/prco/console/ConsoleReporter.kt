package sk.hudak.prco.console

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.GroupCreateDto
import sk.hudak.prco.dto.GroupUpdateDto
import sk.hudak.prco.dto.UnitData
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.dto.product.ProductFullDto
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.utils.CalculationUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

@Component
class PrcoConsole(val internalTxService: InternalTxService) {

    fun showInConsole() {

        showDuplicityProductsInEshops()

//        showProductPerEshop(EshopUuid.MOJA_LEKAREN)

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

    private fun showDuplicityProductsInEshops() {
        for (eshopUuid in EshopUuid.values()) {
            println("Duplicity for eshop: $eshopUuid")
            val result = internalTxService.findDuplicityProductsByNameAndPriceInEshop(eshopUuid)
            for (productFullDto in result) {
                println(productFullDto.id.toString() + ", "
                        + productFullDto.name + " "
                        + productFullDto.priceForPackage + " "
                        + productFullDto.url + " "
                        + formatDate(productFullDto.created))
            }
            println()
        }
    }

    private fun formatDate(date: Date?): String {
        return if (date == null) {
            ""
        } else SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date)
    }

    private fun showAllProducts() {
        val products = internalTxService.findProducts(ProductFilterUIDto())
        for (product in products) {
            println("id ${product.id}, name ${product.name}, url ${product.url}")
        }
    }

    private fun showAllGroups() {
        println("Zoznam groups:")
        val groups = internalTxService.findAllGroupExtended()
        for (group in groups) {
            print(group.id.toString() + "/" + group.name + "(" + group.countOfProduct + ") ")
            val countOfProductInEshop = group.countOfProductInEshop
            if (!countOfProductInEshop.isEmpty()) {
                for (eshopUuid in countOfProductInEshop.keys) {
                    print(eshopUuid.toString() + "/" + countOfProductInEshop[eshopUuid] + ", ")
                }
            }
            println()
        }
        println("---")
    }


    private fun updateGroupName(id: Long, name: String) {
        internalTxService.updateGroup(GroupUpdateDto(id, name))
    }

    private fun createNewGroup(groupName: String) {
        internalTxService.createGroup(GroupCreateDto(groupName))
    }

    fun UC_fixOneInvalidNewProduct() {
        println("pocet neplatnych: " + internalTxService.countOfInvalidNewProduct)

        val firstInvalidNewProductInfo = internalTxService.findFirstInvalidNewProduct()
        if (firstInvalidNewProductInfo != null) {
            println("je to null")
            return
        }
        println(firstInvalidNewProductInfo)

        //TODO z GUI vyplnit spravne data
        internalTxService.repairInvalidUnitForNewProduct(
                1185L,
                UnitData(Unit.KUS, BigDecimal("1"), Integer.valueOf(1)))

    }

    fun UC_markAsNotInterested(newProductIds: Long?) {
        internalTxService.markNewProductAsNotInterested(newProductIds!!)
    }
    private fun showProductsNotInAnyGroup() {
        println()
        println("Products not in any group:")

        val products = internalTxService.findProductsNotInAnyGroup()
        for (product in products) {
            println("eshop " + product.eshopUuid + ", " +
                    "id " + product.id + ", " +
                    "name: " + product.name + ", " +
                    "url: " + product.url + ", " +
                    "unit: " + product.unitValue + " " + product.unit + " count: " + product.unitPackageCount)

        }
        if (products.isEmpty()) {
            println("ziadny")
        }
    }

    private fun showProductsInEshop(eshopUuid: EshopUuid) {
        println("Eshop $eshopUuid")

        val products = internalTxService.findProducts(ProductFilterUIDto.withEshopOnly(eshopUuid))
        for (product in products) {
            println("id " + product.id + ", "
                    + formatPrice(product.priceForPackage) + "(" + formatValidTo(product.actionValidTo) + ") " + formatPrice(product.commonPrice)
                    + " '" + product.name + "', "
                    + product.groupList + "  "
                    + product.url)
        }
    }


    private fun formatValidTo(date: Date?): String {
        return if (date == null) {
            "-"
        } else SimpleDateFormat("dd.MM.yyyy").format(date)
    }

    private fun formatPrice(bigDecimal: BigDecimal?, countOfDecimal: Int): String {
        var bigDecimal: BigDecimal? = bigDecimal ?: return "-"
        bigDecimal = bigDecimal!!.setScale(countOfDecimal, RoundingMode.HALF_UP)
        return formatPrice(bigDecimal)
    }

    private fun formatPrice(bigDecimal: BigDecimal?): String {
        return bigDecimal?.toString()?.replace("0+$".toRegex(), "") ?: "-"
    }

    private fun showProductsInEshopInAction(eshopUuid: EshopUuid) {
        println()
        println("Produkty v akcii pre eshop $eshopUuid:")

        for (product in internalTxService.findProductsInAction(eshopUuid)) {
            println("id " + product.id + ", "
                    + formatPrice(product.priceForPackage) + "/"
                    + formatPrice(product.commonPrice) + " "
                    + formatPercentage(product.priceForPackage, product.commonPrice)
                    + " '" + product.name + "', "
                    + formatPrice(product.priceForOneItemInPackage, 2) + " " +
                    "(" + formatValidTo(product.actionValidTo) + ") "
                    + product.bestPriceInGroup + " "
                    + product.url)
        }
    }

    private fun showProductsInEshopWithBestPriceInGroupOnly(eshopUuid: EshopUuid) {
        println()
        println("Produkty v akcii a najlepsou cenou v groupe pre eshop $eshopUuid:")

        val products = internalTxService.findProductsBestPriceInGroupDto(eshopUuid)
        for (product in products) {
            println("id " + product.id + ", "
                    + formatPrice(product.priceForUnit, 2) + " "
                    + formatPrice(product.priceForOneItemInPackage, 2) + " "
                    + formatPrice(product.priceForPackage) + "/"
                    + formatPrice(product.commonPrice) + " "
                    + formatPercentage(product.priceForPackage, product.commonPrice) +
                    "(" + formatValidTo(product.actionValidTo) + ") "
                    + " '" + product.name + "', "
                    + product.url)
        }

    }

    private fun formatPercentage(priceForPackage: BigDecimal?, commonPrice: BigDecimal?): String {
        return if (priceForPackage == null || commonPrice == null) {
            ""
        } else CalculationUtils.calculatePercetage(priceForPackage, commonPrice).toString() + "%"

    }

}