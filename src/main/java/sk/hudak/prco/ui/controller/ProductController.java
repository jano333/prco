package sk.hudak.prco.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductUnitDataDto;

import java.util.List;

import static sk.hudak.prco.ui.ViewNamesConstants.VIEW_NEW_PRODUCTS;
import static sk.hudak.prco.ui.ViewNamesConstants.VIEW_NEW_PRODUCT_UNIT_DATA_EDIT;
import static sk.hudak.prco.ui.ViewNamesConstants.VIEW_PRODUCTS_NOT_IN_ANY_GROUP;

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

    @RequestMapping("/product/{id}/unitData")
    public ModelAndView editProductUnitData(@PathVariable(name = "id") Long newProductId) {
        ProductUnitDataDto productUnitDataDto = new ProductUnitDataDto();
        productUnitDataDto.setId(newProductId);
        return new ModelAndView(VIEW_NEW_PRODUCT_UNIT_DATA_EDIT, "productUnitDataDto", productUnitDataDto);
    }

    @RequestMapping(value = "/product/unitData/save", method = RequestMethod.POST)
    public String saveProductUnitData(ProductUnitDataDto unitData) {
        getUiService().updateProductUnitData(unitData);
        return VIEW_NEW_PRODUCTS;
    }

}
