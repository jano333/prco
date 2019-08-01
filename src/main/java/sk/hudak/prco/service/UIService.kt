package sk.hudak.prco.service

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.*
import sk.hudak.prco.dto.product.*
import java.math.BigDecimal

/**
 * Metody pre UI.
 */
interface UIService {

    // ------------ Statistiky --------------

    val statisticsOfProducts: ProductStatisticInfoDto

    val countOfAllNewProducts: Long

    //-------------- NEW PRODUCTS -------------------

    /**
     * Zoznam vsetkych 'novo' pridanych produktov na zaklade filtra.
     *
     * @param filter data, na zaklade ktorych sa filtruje
     * @return
     */
    fun findNewProducts(filter: NewProductFilterUIDto): List<NewProductFullDto>

    /**
     * Nacita informacie o novom produkte na zaklade jeho id.
     *
     * @param newProductId
     * @return
     */
    fun getNewProduct(newProductId: Long?): NewProductFullDto

    /**
     * Nastavi 'confirm' na 'novom' produkte na true. Co znamena, ze potvrdzujem data pre unit hodnoty su spravne.
     *
     * @param newProductId id new produktu
     */
    fun confirmUnitDataForNewProduct(newProductId: Long?)

    /**
     * Spusti znova vyparsovanie 'unit' values na zaklade nazvu 'new' produktu.
     *
     * @param newProductId id new produktu
     */
    fun tryToRepairInvalidUnitForNewProductByReprocessing(newProductId: Long?)

    /**
     * Presunie 'novy' produkt do zoznamu 'interested' produktov. Precondition je ze musi uz mat nastave confirm na true.
     *
     * @param newProductId id new produktu
     */
    fun markNewProductAsInterested(newProductId: Long?)

    /**
     * Presunie 'novy' produkt do zoznamu 'not interested' produktov.
     *
     * @param newProductId id new produktu
     */
    fun markNewProductAsNotInterested(newProductId: Long?)

    //-------------- PRODUCTS -------------------

    fun getProduct(productId: Long?): ProductAddingToGroupDto

    fun updateProductUnitData(productUnitDataDto: ProductUnitDataDto)

    fun updateCommonPrice(productId: Long?, newcommonPrice: BigDecimal)

    fun resetUpdateDateForAllProductsInEshop(eshopUuid: EshopUuid)

    /**
     * Zoznam vsetkych produktov na zaklade filtra.
     *
     * @param filter data na zaklade ktorych sa filtruje
     * @return zoznam najdenych produktov
     */
    fun findProducts(filter: ProductFilterUIDto): List<ProductFullDto>

    /**
     * Finalne odstranenie(odmazanie) produktu na zaklade jeho id.
     *
     * @param productId id produktu
     */
    fun removeProduct(productId: Long?)

    /**
     * Zoznam vsetkych produktov v danej skupine zoradenych podla najlepsej ceny hore...
     */
    fun findProductsInGroup(groupId: Long?, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid): List<ProductFullDto>

    /**
     * Zoznam produktov, ktore nie su v ziadnej skupine
     *
     * @return
     */
    fun findProductsWitchAreNotInAnyGroup(): List<ProductFullDto>

    //-------------- GROUPS -------------------

    /**
     * Vytvorenie novej skupiny produktov.
     *
     * @param groupCreateDto data pre vytvorenie novej grupy
     * @return id novo vytvorenej groupy
     */
    fun createGroup(groupCreateDto: GroupCreateDto): Long?

    /**
     * Editacia(update) existujucej group-y.
     *
     * @param groupUpdateDto data pre update
     */
    fun updateGroup(groupUpdateDto: GroupUpdateDto)

    /**
     * TODO
     *
     * @param groupId id group entity
     * @return
     */
    fun getGroupById(groupId: Long?): GroupIdNameDto

    /**
     * Zoznam vsetkych grup na zaklade filtra.
     *
     * @param groupFilterDto data na zaklade ktorych sa filtruje
     * @return zoznam najdenych grup
     */
    fun findGroups(groupFilterDto: GroupFilterDto): List<GroupListDto>

    /**
     * Pridanie produktov do skupiny.
     *
     * @param groupId    id grupy, do ktorej maju byt pridane produkty
     * @param productIds idcka produktov, ktore maju byt pridane do danej skupiny
     */
    fun addProductsToGroup(groupId: Long?, vararg productIds: Long)

    /**
     * Odstranenie produktov zo skupiny.
     *
     * @param groupId    id grupy, z ktorej maju byt odstranene produkty
     * @param productIds idcka produktov, ktore maju byt odstranene z danej skupiny
     */
    fun removeProductsFromGroup(groupId: Long?, vararg productIds: Long)

    /**
     * Zoznam skupin, v ktorych dany produkt nie je pridany.
     *
     * @param productId id produktu
     * @return zoznam skupin, ktore neobsahuju dany produkt
     */
    fun getGroupsWithoutProduct(productId: Long?): List<GroupListDto>

    fun findAllGroupExtended(): List<GroupListExtendedDto>

    // ------------ TODO other prest a pretriedit !!!

    /**
     * Overi existenciu produktu s danou URL, pozera sa iba do 'interested' produktov TODO staci iba tam?
     *
     * @param productURL
     * @return
     */
    fun existProductWithUrl(productURL: String): Boolean

    /**
     * Odsrani/odmaza existujuce produkty
     *
     * @param productIds id-cka produktov, ktore budu odmazane
     */
    fun deleteProducts(vararg productIds: Long)

    fun findProductsInAction(eshopUuid: EshopUuid): List<ProductInActionDto>

    fun findProductsBestPriceInGroupDto(eshopUuid: EshopUuid): List<ProductBestPriceInGroupDto>

    fun deleteNewProducts(vararg newProductIds: Long)

    fun markProductAsNotInterested(productId: Long?)
}
