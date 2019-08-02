package sk.hudak.prco.ui.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.product.NewProductFilterUIDto
import sk.hudak.prco.dto.product.ProductUnitDataDto
import sk.hudak.prco.service.UIService
import sk.hudak.prco.ui.MvcConstants.REDIRECT_TO_VIEW_NEW_PRODUCTS
import sk.hudak.prco.ui.MvcConstants.VIEW_NEW_PRODUCTS
import sk.hudak.prco.ui.MvcConstants.VIEW_NEW_PRODUCT_UNIT_DATA_EDIT
import java.util.*
import kotlin.streams.toList

@Controller
class NewProductController (uiService: UIService) : BasicController(uiService) {

    /**
     * Zoznam vsetky Unit hodnot
     *
     * @return
     */
    val multiCheckboxAllValues: Array<String>
        @ModelAttribute("allUnitValues")
        get() {
            val result = Arrays.stream(Unit.values())
                    .map { it.name }
                    .toList()
            return result.toTypedArray()
        }

    /**
     * Zoznam novych produktov
     *
     * @return
     */
    @RequestMapping("/newProducts")
    fun listNewProducts(): ModelAndView {
        val newProducts = uiService.findNewProducts(NewProductFilterUIDto())
        val modelAndView = ModelAndView(VIEW_NEW_PRODUCTS, "newProducts", newProducts)
        modelAndView.addObject("countOfAllNewProducts", newProducts.size)
        modelAndView.addObject("fullCountOfAllNewProducts", uiService.countOfAllNewProducts)

        return modelAndView
    }

    /**
     * Potvrdenie hodnoty pre unit
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/confirm")
    fun confirmNewProducts(@PathVariable id: Long?): ModelAndView {
        uiService.confirmUnitDataForNewProduct(id)
        //reload zoznamu
        return ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS)
    }

    /**
     * Spusti znova vyparsovanie 'unit' values na zaklade nazvu 'new' produktu.
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/reprocess")
    fun reprocessNewProducts(@PathVariable id: Long?): ModelAndView {
        uiService.tryToRepairInvalidUnitForNewProductByReprocessing(id)
        return ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS)
    }

    /**
     * Presunie new product do product
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/interested")
    fun interestedNewProducts(@PathVariable id: Long?): ModelAndView {
        uiService.markNewProductAsInterested(id)
        return ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS)
    }

    /**
     * Presunie new product do not interested product
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/notInterested")
    fun notInterestedNewProducts(@PathVariable id: Long?): ModelAndView {
        uiService.markNewProductAsNotInterested(id)
        return ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS)
    }

    /**
     * Editacia(zobrazie) unit data
     *
     * @param newProductId
     * @return
     */
    @RequestMapping(value = ["/newProduct/{id}/unitData"])
    fun editProductUnitData(@PathVariable(name = "id") newProductId: Long?): ModelAndView {
        val (id, _, _, _, name, _, unit, unitValue, unitPackageCount) = uiService.getNewProduct(newProductId)

        val productUnitDataDto = ProductUnitDataDto()
        productUnitDataDto.id = id
        productUnitDataDto.name = name
        productUnitDataDto.unit = unit?.name
        productUnitDataDto.unitPackageCount = unitPackageCount
        productUnitDataDto.unitValue = unitValue

        return ModelAndView(VIEW_NEW_PRODUCT_UNIT_DATA_EDIT, "productUnitDataDto", productUnitDataDto)
    }

    /**
     * Unit data save action
     *
     * @param unitData
     * @return
     */
    @RequestMapping(value = ["/newProduct/unitData/save"], method = [RequestMethod.POST])
    fun saveProductUnitData(unitData: ProductUnitDataDto): String {
        uiService.updateProductUnitData(unitData)
        return REDIRECT_TO_VIEW_NEW_PRODUCTS
    }

}
