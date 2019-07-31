package sk.hudak.prco.service

import sk.hudak.prco.dto.ProductStatisticInfoDto
import sk.hudak.prco.dto.product.NewProductFullDto
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.dto.product.NotInterestedProductFullDto
import sk.hudak.prco.dto.product.ProductFullDto

interface ProductCommonService {

    val statisticsOfProducts: ProductStatisticInfoDto

    /**
     * @param productURL
     * @return true, ak produkt s danou `url` uz existuje v [sk.hudak.prco.model.NewProductEntity],
     * [sk.hudak.prco.model.NotInterestedProductEntity] alebo [sk.hudak.prco.model.ProductEntity]
     * inak false
     */
    fun existProductWithURL(productURL: String): Boolean

    /**
     * Oznaci dane produkty, ze o ne mam zaujem.
     *
     *
     * Implementacne: presunie zaznam z [sk.hudak.prco.model.NewProductEntity] do [sk.hudak.prco.model.ProductEntity]
     *
     * @param newProductIds zoznam idcok z [sk.hudak.prco.model.NewProductEntity]
     */
    fun markNewProductAsInterested(vararg newProductIds: Long)

    /**
     * Oznaci dane produkty, ze o ne mam zaujem.
     *
     *
     * Implementacne: presunie zaznam z [sk.hudak.prco.model.NewProductEntity] do [sk.hudak.prco.model.NotInterestedProductEntity]
     *
     * @param newProductIds zoznam idcok z [sk.hudak.prco.model.NewProductEntity]
     */
    fun markNewProductAsNotInterested(vararg newProductIds: Long)

    fun findNotInterestedProducts(findDto: NotInterestedProductFindDto): List<NotInterestedProductFullDto>

    /**
     * Importne pokial tam taky este nie je...,
     *
     * @param newProductList
     * @return pocet skutocne imortnutych
     */
    fun importNewProducts(newProductList: List<NewProductFullDto>): Long

    fun importProducts(productList: List<ProductFullDto>): Long

    fun importNotInterestedProducts(notInterestedProductList: List<NotInterestedProductFullDto>): Long
}
