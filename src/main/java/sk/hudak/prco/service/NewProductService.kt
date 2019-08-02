package sk.hudak.prco.service


import sk.hudak.prco.dto.UnitData
import sk.hudak.prco.dto.product.*

/**
 * Service pre db entitu [sk.hudak.prco.model.NewProductEntity]
 */
interface NewProductService {

    /**
     * @return pocet vsetkych nevalidnych novych produktov
     */
    val countOfInvalidNewProduct: Long

    val countOfAllNewProducts: Long

    /**
     * Vytvori novy zaznam, pricom<br></br>
     * `valid` sa nastavuje na true, iba ak su vyplnene vsetky atributy okrem pictureURL,<br></br>
     * `confirmValidity` na false, a interested na false<br></br>
     *
     * @param newProductCreateDto vstupne data pre vytvorenie
     * @return id novo vytvorenej entity [sk.hudak.prco.model.NewProductEntity]
     */
    fun createNewProduct(newProductCreateDto: NewProductCreateDto): Long

    /**
     * Na zaklade id vrati cely novy produkt
     *
     * @param newProductId
     * @return
     */
    fun getNewProduct(newProductId: Long): NewProductFullDto

    /**
     * @return prvy nevalidny produkt(TODO co to je nevalidny), ktory treba opravit.<br></br>
     * [sk.hudak.prco.model.NewProductEntity.valid] je nastaveny na false
     */
    fun findFirstInvalidNewProduct(): NewProductInfoDetail?

    /**
     * Nastavi confirmaciu a validitu na true.
     *
     * @param newProductId    new product id
     * @param correctUnitData
     */
    fun repairInvalidUnitForNewProduct(newProductId: Long?, correctUnitData: UnitData)

    /**
     * sprusti este raz parsovanie dat pre new product
     *
     * @param newProductId new product id
     */
    fun reprocessProductData(newProductId: Long?)

    /**
     * Potvrdi, ze data pre unit, value, a package count odpovedaju tomu co je v nazve produktu,
     * teda ze som to skontroloval
     *
     * @param newProductIds
     */
    fun confirmUnitDataForNewProducts(newProductIds: LongArray)

    /**
     * vyklada zoznam max `maxCountOfInvalid` produktov NewProductEntity.
     * A pre kazdy na zaklade nazvu skusi vyparsovat unit data, ak sa podari tak ich upravi.
     *
     * @param maxCountOfInvalid maximalny pocet productov, ktore sa maju opravit
     * @return pocet skutocne opravenych
     */
    fun fixAutomaticallyProductUnitData(maxCountOfInvalid: Int): Long

    /**
     * Vyhladavanie pre UI na zaklade filtra.
     *
     * @param filter
     * @return
     */
    fun findNewProducts(filter: NewProductFilterUIDto): List<NewProductFullDto>

    fun findNewProductsForExport(): List<NewProductFullDto>

    fun updateProductUnitData(productUnitDataDto: ProductUnitDataDto)

    fun deleteNewProducts(newProductIds: Array<Long>)
}
