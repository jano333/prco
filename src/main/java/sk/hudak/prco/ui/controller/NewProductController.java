package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.product.ProductUnitDataDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static sk.hudak.prco.ui.MvcConstants.REDIRECT_TO_VIEW_NEW_PRODUCTS;
import static sk.hudak.prco.ui.MvcConstants.VIEW_NEW_PRODUCTS;
import static sk.hudak.prco.ui.MvcConstants.VIEW_NEW_PRODUCT_UNIT_DATA_EDIT;

@Controller
public class NewProductController extends BasicController {

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
        return new ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS);
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
        return new ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS);
    }

    /**
     * Presunie new product do product
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/interested")
    public ModelAndView interestedNewProducts(@PathVariable Long id) {
        getUiService().markNewProductAsInterested(id);
        return new ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS);
    }

    /**
     * Presunie new product do not interested product
     *
     * @param id
     * @return
     */
    @RequestMapping("/newProduct/{id}/notInterested")
    public ModelAndView notInterestedNewProducts(@PathVariable Long id) {
        getUiService().markNewProductAsNotInterested(id);
        return new ModelAndView(REDIRECT_TO_VIEW_NEW_PRODUCTS);
    }

    /**
     * Editacia(zobrazie) unit data
     *
     * @param newProductId
     * @return
     */
    @RequestMapping(value = "/newProduct/{id}/unitData")
    public ModelAndView editProductUnitData(@PathVariable(name = "id") Long newProductId) {
        NewProductFullDto newProduct = getUiService().getNewProduct(newProductId);

        ProductUnitDataDto productUnitDataDto = new ProductUnitDataDto();
        productUnitDataDto.setId(newProduct.getId());
        productUnitDataDto.setName(newProduct.getName());
        productUnitDataDto.setUnit(newProduct.getUnit() != null ? newProduct.getUnit().name() : null);
        productUnitDataDto.setUnitPackageCount(newProduct.getUnitPackageCount());
        productUnitDataDto.setUnitValue(newProduct.getUnitValue());

        return new ModelAndView(VIEW_NEW_PRODUCT_UNIT_DATA_EDIT, "productUnitDataDto", productUnitDataDto);
    }

    /**
     * Unit data save action
     *
     * @param unitData
     * @return
     */
    @RequestMapping(value = "/newProduct/unitData/save", method = RequestMethod.POST)
    public String saveProductUnitData(ProductUnitDataDto unitData) {
        getUiService().updateProductUnitData(unitData);
        return REDIRECT_TO_VIEW_NEW_PRODUCTS;
    }

    /**
     * Zoznam vsetky Unit hodnot
     *
     * @return
     */
    @ModelAttribute("allUnitValues")
    public String[] getMultiCheckboxAllValues() {
        List<String> result = Arrays.stream(Unit.values())
                .map(t -> t.name())
                .collect(Collectors.toList());
        return result.toArray(new String[result.size()]);
    }

}
