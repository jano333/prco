package sk.hudak.prco.service;

import sk.hudak.prco.dto.ProductStatisticInfoDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.notinteretedproduct.NotInterestedProductFindDto;
import sk.hudak.prco.dto.notinteretedproduct.NotInterestedProductFullDto;
import sk.hudak.prco.dto.product.ProductFullDto;

import java.util.List;

public interface ProductCommonService {

    /**
     * @param productURL
     * @return true, ak produkt s danou <code>url</code> uz existuje v {@link sk.hudak.prco.model.NewProductEntity},
     * {@link sk.hudak.prco.model.NotInterestedProductEntity} alebo {@link sk.hudak.prco.model.ProductEntity}
     * inak false
     */
    boolean existProductWithURL(String productURL);

    /**
     * Oznaci dane produkty, ze o ne mam zaujem.
     * <p>
     * Implementacne: presunie zaznam z {@link sk.hudak.prco.model.NewProductEntity} do {@link sk.hudak.prco.model.ProductEntity}
     *
     * @param newProductIds zoznam idcok z {@link sk.hudak.prco.model.NewProductEntity}
     */
    void markNewProductAsInterested(Long... newProductIds);

    /**
     * Oznaci dane produkty, ze o ne mam zaujem.
     * <p>
     * Implementacne: presunie zaznam z {@link sk.hudak.prco.model.NewProductEntity} do {@link sk.hudak.prco.model.NotInterestedProductEntity}
     *
     * @param newProductIds zoznam idcok z {@link sk.hudak.prco.model.NewProductEntity}
     */
    void markNewProductAsNotInterested(Long... newProductIds);

    List<NotInterestedProductFullDto> findNotInterestedProducts(NotInterestedProductFindDto findDto);

    /**
     * Importne pokial tam taky este nie je...,
     *
     * @param newProductList
     * @return pocet skutocne imortnutych
     */
    long importNewProducts(List<NewProductFullDto> newProductList);

    long importProducts(List<ProductFullDto> productList);

    long importNotInterestedProducts(List<NotInterestedProductFullDto> notInterestedProductList);

    ProductStatisticInfoDto getStatisticsOfProducts();
}
