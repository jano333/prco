package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sk.hudak.prco.dto.product.ProductFullDto;

import java.util.List;

@Controller
public class ProductController extends BasicController {

    /**
     * Zoznam produktov, ktore nie su v ziadne grupe
     *
     * @param model
     * @return
     */
    @RequestMapping("/productsNotIntAnyGroup")
    public String listProductsWitchAreNotInAnyGroup(Model model) {
        List<ProductFullDto> products = getUiService().findProductsWitchAreNotInAnyGroup();
        model.addAttribute("productsNotIntAnyGroup", products);
        return "productsNotIntAnyGroup";
    }
}
