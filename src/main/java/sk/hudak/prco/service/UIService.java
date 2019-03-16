package sk.hudak.prco.service;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.ProductStatisticInfoDto;
import sk.hudak.prco.dto.group.GroupCreateDto;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.group.GroupListDto;
import sk.hudak.prco.dto.group.GroupListExtendedDto;
import sk.hudak.prco.dto.group.GroupUpdateDto;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.product.ProductAddingToGroupDto;
import sk.hudak.prco.dto.product.ProductBestPriceInGroupDto;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductInActionDto;
import sk.hudak.prco.dto.product.ProductUnitDataDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Metody pre UI.
 */
public interface UIService {

    //-------------- NEW PRODUCTS -------------------

    /**
     * Zoznam vsetkych 'novo' pridanych produktov na zaklade filtra.
     *
     * @param filter data, na zaklade ktorych sa filtruje
     * @return
     */
    List<NewProductFullDto> findNewProducts(NewProductFilterUIDto filter);

    /**
     * Nacita informacie o novom produkte na zaklade jeho id.
     *
     * @param newProductId
     * @return
     */
    NewProductFullDto getNewProduct(Long newProductId);

    /**
     * Nastavi 'confirm' na 'novom' produkte na true. Co znamena, ze potvrdzujem data pre unit hodnoty su spravne.
     *
     * @param newProductId id new produktu
     */
    void confirmUnitDataForNewProduct(Long newProductId);

    /**
     * Spusti znova vyparsovanie 'unit' values na zaklade nazvu 'new' produktu.
     *
     * @param newProductId id new produktu
     */
    void tryToRepairInvalidUnitForNewProductByReprocessing(Long newProductId);

    /**
     * Presunie 'novy' produkt do zoznamu 'interested' produktov. Precondition je ze musi uz mat nastave confirm na true.
     *
     * @param newProductId id new produktu
     */
    void markNewProductAsInterested(Long newProductId);

    /**
     * Presunie 'novy' produkt do zoznamu 'not interested' produktov.
     *
     * @param newProductId id new produktu
     */
    void markNewProductAsNotInterested(Long newProductId);

    //-------------- PRODUCTS -------------------

    ProductAddingToGroupDto getProduct(Long productId);

    void updateProductUnitData(ProductUnitDataDto productUnitDataDto);

    void updateCommonPrice(Long productId, BigDecimal newcommonPrice);

    void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid);

    /**
     * Zoznam vsetkych produktov na zaklade filtra.
     *
     * @param filter data na zaklade ktorych sa filtruje
     * @return zoznam najdenych produktov
     */
    List<ProductFullDto> findProducts(ProductFilterUIDto filter);

    /**
     * Finalne odstranenie(odmazanie) produktu na zaklade jeho id.
     *
     * @param productId id produktu
     */
    void removeProduct(Long productId);

    /**
     * Zoznam vsetkych produktov v danej skupine zoradenych podla najlepsej ceny hore...
     */
    List<ProductFullDto> findProductsInGroup(Long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip);

    /**
     * Zoznam produktov, ktore nie su v ziadnej skupine
     *
     * @return
     */
    List<ProductFullDto> findProductsWitchAreNotInAnyGroup();

    //-------------- GROUPS -------------------

    /**
     * Vytvorenie novej skupiny produktov.
     *
     * @param groupCreateDto data pre vytvorenie novej grupy
     * @return id novo vytvorenej groupy
     */
    Long createGroup(GroupCreateDto groupCreateDto);

    /**
     * Editacia(update) existujucej group-y.
     *
     * @param groupUpdateDto data pre update
     */
    void updateGroup(GroupUpdateDto groupUpdateDto);

    /**
     * TODO
     *
     * @param groupId id group entity
     * @return
     */
    GroupIdNameDto getGroupById(Long groupId);

    /**
     * Zoznam vsetkych grup na zaklade filtra.
     *
     * @param groupFilterDto data na zaklade ktorych sa filtruje
     * @return zoznam najdenych grup
     */
    List<GroupListDto> findGroups(GroupFilterDto groupFilterDto);

    /**
     * Pridanie produktov do skupiny.
     *
     * @param groupId    id grupy, do ktorej maju byt pridane produkty
     * @param productIds idcka produktov, ktore maju byt pridane do danej skupiny
     */
    void addProductsToGroup(Long groupId, Long... productIds);

    /**
     * Odstranenie produktov zo skupiny.
     *
     * @param groupId    id grupy, z ktorej maju byt odstranene produkty
     * @param productIds idcka produktov, ktore maju byt odstranene z danej skupiny
     */
    void removeProductsFromGroup(Long groupId, Long... productIds);

    /**
     * Zoznam skupin, v ktorych dany produkt nie je pridany.
     *
     * @param productId id produktu
     * @return zoznam skupin, ktore neobsahuju dany produkt
     */
    List<GroupListDto> getGroupsWithoutProduct(Long productId);

    List<GroupListExtendedDto> findAllGroupExtended();

    // ------------ Statistiky --------------

    ProductStatisticInfoDto getStatisticsOfProducts();

    // ------------ TODO other prest a pretriedit !!!

    /**
     * Overi existenciu produktu s danou URL, pozera sa iba do 'interested' produktov TODO staci iba tam?
     *
     * @param productURL
     * @return
     */
    boolean existProductWithUrl(String productURL);

    /**
     * Odsrani/odmaza existujuce produkty
     *
     * @param productIds id-cka produktov, ktore budu odmazane
     */
    void deleteProducts(Long... productIds);

    List<ProductInActionDto> findProductsInAction(EshopUuid eshopUuid);

    List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid);

    void deleteNewProducts(Long... newProductIds);
}
