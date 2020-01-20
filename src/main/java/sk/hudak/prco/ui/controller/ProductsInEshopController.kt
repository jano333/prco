package sk.hudak.prco.ui.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.service.UIService
import sk.hudak.prco.ui.MvcConstants


data class Item(val eshopUuid: EshopUuid)

@Controller
class ProductsInEshopController(uiService: UIService) : BasicController(uiService) {

    @GetMapping("/productsInEshop")
    fun productsInEshop(): ModelAndView {
        val modelAndView = ModelAndView(MvcConstants.VIEW_PRODUCTS_IN_ESHOP)
        modelAndView.addObject("item", Item(EshopUuid.ALZA))
        modelAndView.addObject("eshopList", EshopUuid.values())
        return modelAndView
    }

    @PostMapping("/productsInEshop")
    fun productsInEshop(@ModelAttribute("item") selectedEshop: Item): ModelAndView {
        val modelAndView = ModelAndView(MvcConstants.VIEW_PRODUCTS_IN_ESHOP)
        modelAndView.addObject("item", selectedEshop)
        modelAndView.addObject("eshopList", EshopUuid.values())
        modelAndView.addObject("productInEshopList", uiService.findProductsInEshop(selectedEshop.eshopUuid))
        return modelAndView
    }
}