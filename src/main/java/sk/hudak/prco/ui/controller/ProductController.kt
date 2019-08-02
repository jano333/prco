package sk.hudak.prco.ui.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import sk.hudak.prco.api.GroupProductKeywords
import sk.hudak.prco.dto.GroupFilterDto
import sk.hudak.prco.dto.product.ProductNotInAnyGroupDto
import sk.hudak.prco.manager.GroupProductResolver
import sk.hudak.prco.service.UIService
import sk.hudak.prco.ui.MvcConstants.ATTRIBUTE_COUNT_OF_PRODUCTS_NOT_IN_ANY_GROUP
import sk.hudak.prco.ui.MvcConstants.REDIRECT_TO_VIEW_PRODUCTS_NOT_IN_ANY_GROUP
import sk.hudak.prco.ui.MvcConstants.VIEW_PRODUCTS_IN_GROUP
import sk.hudak.prco.ui.MvcConstants.VIEW_PRODUCTS_NOT_IN_ANY_GROUP
import sk.hudak.prco.ui.MvcConstants.VIEW_PRODUCT_ADD_TO_GROUP
import java.util.*

@Controller
class ProductController(uiService: UIService, val groupProductResolver: GroupProductResolver) : BasicController(uiService) {

    /**
     * Zoznam produktov, ktore nie su v ziadne grupe
     *
     * @return
     */
    @RequestMapping("/productsNotIntAnyGroup")
    fun listProductsWitchAreNotInAnyGroup(): ModelAndView {
        val products = ArrayList<ProductNotInAnyGroupDto>()

        for (product in uiService.findProductsWitchAreNotInAnyGroup()) {
            //FIXME cez oriku okrem keywords
            val dto = ProductNotInAnyGroupDto()
            dto.eshopUuid = product.eshopUuid
            dto.productPictureUrl = product.productPictureUrl
            dto.name = product.name
            dto.url = product.url
            dto.id = product.id
            dto.keywords = groupProductResolver.resolveGroup(product.name!!)
            products.add(dto)
        }
        val modelAndView = ModelAndView(VIEW_PRODUCTS_NOT_IN_ANY_GROUP, "productsNotIntAnyGroup", products)
        modelAndView.addObject(ATTRIBUTE_COUNT_OF_PRODUCTS_NOT_IN_ANY_GROUP, products.size)
        return modelAndView
    }

    /**
     * Pohlad pre pridanie produktu do skupiny
     */
    @RequestMapping("/products/{productId}/addToGroup")
    fun addProductToGroupView(@PathVariable productId: Long?): ModelAndView {
        val modelAndView = ModelAndView(VIEW_PRODUCT_ADD_TO_GROUP, "product", uiService.getProduct(productId))
        modelAndView.addObject("groupsWithoutProduct", uiService.getGroupsWithoutProduct(productId))
        return modelAndView
    }

    @RequestMapping("/products/{productId}/addToGroupAutomaticaly/{keyword}")
    fun addProductToGroupAutomaticalyView(
            @PathVariable productId: Long?,
            @PathVariable keyword: GroupProductKeywords): ModelAndView {

        uiService.addProductsToGroup(keyword.groupId, productId!!)
        return ModelAndView(REDIRECT_TO_VIEW_PRODUCTS_NOT_IN_ANY_GROUP)
    }

    /**
     * Save produktu do skupiny
     */
    @RequestMapping(value = ["/products/{productId}/addToGroup"], method = [RequestMethod.POST])
    fun addProductToGroupSave(@PathVariable productId: Long?,
                              @ModelAttribute(value = "groupId") selectedGroupId: Long?): ModelAndView {

        uiService.addProductsToGroup(selectedGroupId, productId!!)
        return ModelAndView(REDIRECT_TO_VIEW_PRODUCTS_NOT_IN_ANY_GROUP)
    }

    @RequestMapping("/products/groups")
    fun productInGroupView(): ModelAndView {
        val modelAndView = ModelAndView(VIEW_PRODUCTS_IN_GROUP, "productsInGroup", emptyList<Any>())
        modelAndView.addObject("groupListDtos", uiService.findGroups(GroupFilterDto()))
        return modelAndView
    }

    @RequestMapping("/products/groups/{groupId}")
    fun productInGroupView(@PathVariable groupId: Long?): ModelAndView {
        val productsInGroup = uiService.findProductsInGroup(groupId, true)
        val modelAndView = ModelAndView(VIEW_PRODUCTS_IN_GROUP, "productsInGroup", productsInGroup)
        modelAndView.addObject("groupListDtos", uiService.findGroups(GroupFilterDto()))
        return modelAndView
    }

    @RequestMapping("/product/{id}/notInterested")
    fun notInterestedNewProducts(@PathVariable id: Long?): ModelAndView {
        uiService.markProductAsNotInterested(id)
        return ModelAndView(REDIRECT_TO_VIEW_PRODUCTS_NOT_IN_ANY_GROUP)
    }

}
