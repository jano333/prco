package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.dto.product.ProductFullDto;

import java.util.Collections;
import java.util.List;

import static sk.hudak.prco.ui.MvcConstants.VIEW_PRODUCTS_IN_GROUP;
import static sk.hudak.prco.ui.MvcConstants.VIEW_PRODUCTS_NOT_IN_ANY_GROUP;
import static sk.hudak.prco.ui.MvcConstants.VIEW_PRODUCT_ADD_TO_GROUP;

@Controller
public class ProductController extends BasicController {


    /**
     * Zoznam produktov, ktore nie su v ziadne grupe
     *
     * @return
     */
    @RequestMapping("/productsNotIntAnyGroup")
    public ModelAndView listProductsWitchAreNotInAnyGroup() {
        List<ProductFullDto> products = getUiService().findProductsWitchAreNotInAnyGroup();
        return new ModelAndView(VIEW_PRODUCTS_NOT_IN_ANY_GROUP, "productsNotIntAnyGroup", products);
    }

    /**
     * Pohlad pre pridanie produktu do skupiny
     *
     * @param id
     * @return
     */
    @RequestMapping("/products/{id}/addToGroup")
    public ModelAndView addProductToGroupView(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(VIEW_PRODUCT_ADD_TO_GROUP, "product", getUiService().getProduct(id));
        modelAndView.addObject("groupsWithoutProduct", getUiService().getGroupsWithoutProduct(id));
        return modelAndView;
    }

    /**
     * Save produktu do skupiny
     *
     * @param productId
     * @param selectedGroupId
     * @return
     */
    @RequestMapping(value = "/products/{productId}/addToGroup", method = RequestMethod.POST)
    public ModelAndView addProductToGroupSave(@PathVariable Long productId,
                                              @ModelAttribute(value = "groupId") Long selectedGroupId) {

        getUiService().addProductsToGroup(selectedGroupId, productId);
        return listProductsWitchAreNotInAnyGroup();
    }

    @RequestMapping("/products/groups")
    public ModelAndView productInGroupView() {
        ModelAndView modelAndView = new ModelAndView(VIEW_PRODUCTS_IN_GROUP, "productsInGroup", Collections.emptyList());
        modelAndView.addObject("groupListDtos", getUiService().findGroups(new GroupFilterDto()));
        return modelAndView;
    }

    @RequestMapping("/products/groups/{groupId}")
    public ModelAndView productInGroupView(@PathVariable Long groupId) {
        List<ProductFullDto> productsInGroup = getUiService().findProductsInGroup(groupId);
        ModelAndView modelAndView = new ModelAndView(VIEW_PRODUCTS_IN_GROUP, "productsInGroup", productsInGroup);
        modelAndView.addObject("groupListDtos", getUiService().findGroups(new GroupFilterDto()));
        return modelAndView;
    }

}
