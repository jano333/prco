package sk.hudak.prco.service;


import sk.hudak.prco.dto.UnitData;
import sk.hudak.prco.dto.newproduct.NewProductCreateDto;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.newproduct.NewProductInfoDetail;
import sk.hudak.prco.dto.product.ProductUnitDataDto;

import java.util.List;
import java.util.Optional;

/**
 * Service pre db entitu {@link sk.hudak.prco.model.NewProductEntity}
 */
public interface NewProductService {

    /**
     * Vytvori novy zaznam v databaze do DB, pricom<br>
     * <code>valid</code> sa nastavuje na true, iba ak su vyplnene vsetky atributy okrem pictureURL,<br>
     * <code>confirmValidity</code> na false, a interested na false<br>
     *
     * @param newProductCreateDto vstupne data pre vytvorenie
     * @return id novo vytvorenej entity {@link sk.hudak.prco.model.NewProductEntity}
     */
    Long createNewProduct(NewProductCreateDto newProductCreateDto);

    /**
     * NA zaklade id vrati cely novy produkt
     * @param newProductId
     * @return
     */
    NewProductFullDto getNewProduct(Long newProductId);

    /**
     * @return prvy nevalidny produkt, ktory treba opravit.<br>
     * {@link sk.hudak.prco.model.NewProductEntity#valid} je nastaveny na false
     */
    Optional<NewProductInfoDetail> findFirstInvalidNewProduct();

    /**
     * @return pocet vsetkych nevalidnych novych produktov
     */
    long getCountOfInvalidNewProduct();

    /**
     * Nastavi confirmaciu a validitu na true.
     *
     * @param newProductId
     * @param correctUnitData
     */
    void repairInvalidUnitForNewProduct(Long newProductId, UnitData correctUnitData);

    /**
     * sprusti este raz zistovanie UnitData na zaklade nazvu productu.
     *
     * @param newProductId
     */
    void repairInvalidUnitForNewProductByReprocessing(Long newProductId);

    /**
     * Potvrdi, ze data pre unit, value, a package count odpovedaju tomu co je v nazve produktu,
     * teda ze som to skontroloval
     *
     * @param newProductIds
     */
    void confirmUnitDataForNewProducts(Long... newProductIds);

    /**
     * vyklada zoznam max <code>maxCountOfInvalid</code> produktov NewProductEntity.
     * A pre kazdy na zaklade nazvu skusi vyparsovat unit data, ak sa podari tak ich upravi.
     *
     * @param maxCountOfInvalid maximalny pocet productov, ktore sa maju opravit
     * @return pocet skutocne opravenych
     */
    long fixAutomaticalyProductUnitData(int maxCountOfInvalid);

    /**
     * Vyhladavanie pre UI na zaklade filtra.
     *
     * @param filter
     * @return
     */
    List<NewProductFullDto> findNewProducts(NewProductFilterUIDto filter);

    List<NewProductFullDto> findNewProductsForExport();

    void updateProductUnitData(ProductUnitDataDto productUnitDataDto);

    void deleteNewProducts(Long... newProductIds);

}
