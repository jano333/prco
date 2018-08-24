package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;

import java.util.List;

@Controller
public class NewProductListController extends BasicController {

    /**
     * Zoznam novych produktov
     *
     * @param model
     * @return
     */
    @RequestMapping("/newProducts")
    public String listNewProducts(Model model) {
        List<NewProductFullDto> newProducts = getUiService().findNewProducts(new NewProductFilterUIDto());
        model.addAttribute("newProducts", newProducts);
        return "newProducts";
    }

    // ------------   ACTIONS --------------

    /**
     * Potvrdenie hodnoty pre unit
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/newProduct/{id}/confirm")
    public String confirmNewProducts(@PathVariable Long id, Model model) {
        getUiService().confirmUnitDataForNewProduct(id);
        //reload zoznamu
        return listNewProducts(model);
    }

    /**
     * Spusti znova vyparsovanie 'unit' values na zaklade nazvu 'new' produktu.
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/newProduct/{id}/reprocess")
    public String reprocessNewProducts(@PathVariable Long id, Model model) {
        getUiService().tryToRepairInvalidUnitForNewProductByReprocessing(id);
        //reload zoznamu
        return listNewProducts(model);
    }

    @RequestMapping("/newProduct/{id}/interested")
    public String interestedNewProducts(@PathVariable Long id, Model model) {
        getUiService().markNewProductAsInterested(id);
        //reload zoznamu
        return listNewProducts(model);
    }

    @RequestMapping("/newProduct/{id}/notInterested")
    public String notInterestedNewProducts(@PathVariable Long id, Model model) {
        getUiService().markNewProductAsNotInterested(id);
        //reload zoznamu
        return listNewProducts(model);
    }

}
