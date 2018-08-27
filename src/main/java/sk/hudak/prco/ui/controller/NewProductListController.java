package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;

import java.util.List;

import static sk.hudak.prco.ui.ViewNamesConstants.VIEW_NEW_PRODUCTS;

@Controller
public class NewProductListController extends BasicController {

    /**
     * Zoznam novych produktov
     *
     * @return
     */
    @RequestMapping("/newProducts")
    public ModelAndView listNewProducts() {
        List<NewProductFullDto> newProducts = getUiService().findNewProducts(new NewProductFilterUIDto());
        ModelAndView modelAndView = new ModelAndView(VIEW_NEW_PRODUCTS, "newProducts", newProducts);
        modelAndView.addObject("countOfAllNewProducts", newProducts.size());

        return modelAndView;
    }

    // ------------   ACTIONS --------------

    /**
     * Potvrdenie hodnoty pre unit
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/confirm")
    public ModelAndView confirmNewProducts(@PathVariable Long id) {
        getUiService().confirmUnitDataForNewProduct(id);
        //reload zoznamu
        return new ModelAndView("redirect:/" + VIEW_NEW_PRODUCTS);
    }

    /**
     * Spusti znova vyparsovanie 'unit' values na zaklade nazvu 'new' produktu.
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/reprocess")
    public ModelAndView reprocessNewProducts(@PathVariable Long id) {
        getUiService().tryToRepairInvalidUnitForNewProductByReprocessing(id);
        return new ModelAndView("redirect:/" + VIEW_NEW_PRODUCTS);
    }

    @RequestMapping("/newProduct/{id}/interested")
    public ModelAndView interestedNewProducts(@PathVariable Long id) {
        getUiService().markNewProductAsInterested(id);
        return new ModelAndView("redirect:/" + VIEW_NEW_PRODUCTS);
    }

    @RequestMapping("/newProduct/{id}/notInterested")
    public ModelAndView notInterestedNewProducts(@PathVariable Long id) {
        getUiService().markNewProductAsNotInterested(id);
        return new ModelAndView("redirect:/" + VIEW_NEW_PRODUCTS);
    }

}
