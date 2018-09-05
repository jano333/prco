package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import sk.hudak.prco.dto.product.ProductFullDto;

import java.util.List;

import static sk.hudak.prco.ui.MvcConstants.VIEW_PRODUCTS_NOT_IN_ANY_GROUP;

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

}
