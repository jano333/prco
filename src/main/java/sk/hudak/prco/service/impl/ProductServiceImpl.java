package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.api.BestPriceInGroup;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dao.db.GroupEntityDao;
import sk.hudak.prco.dao.db.GroupOfProductFindEntityDao;
import sk.hudak.prco.dao.db.NotInterestedProductDbDao;
import sk.hudak.prco.dao.db.ProductDataUpdateEntityDao;
import sk.hudak.prco.dao.db.ProductEntityDao;
import sk.hudak.prco.dto.ProductUpdateDataDto;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.internal.StatisticForUpdateForEshopDto;
import sk.hudak.prco.dto.product.ProductAddingToGroupDto;
import sk.hudak.prco.dto.product.ProductBestPriceInGroupDto;
import sk.hudak.prco.dto.product.ProductDetailInfo;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.dto.product.ProductInActionDto;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.GroupEntity;
import sk.hudak.prco.model.NotInterestedProductEntity;
import sk.hudak.prco.model.ProductDataUpdateEntity;
import sk.hudak.prco.model.ProductEntity;
import sk.hudak.prco.service.ProductService;
import sk.hudak.prco.utils.CalculationUtils;
import sk.hudak.prco.utils.PriceCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static sk.hudak.prco.utils.Validate.notNegativeAndNotZeroValue;
import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Slf4j
@Service("productService")
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_ID = "productId";
    private static final String ESHOP_UUID = "eshopUuid";
    private static final String PRODUCT_URL = "productURL";

    @Autowired
    private ProductEntityDao productEntityDao;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    private GroupOfProductFindEntityDao groupOfProductFindEntityDao;

    @Autowired
    private ProductDataUpdateEntityDao productDataUpdateEntityDao;

    @Autowired
    private NotInterestedProductDbDao notInterestedProductDbDao;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Autowired
    private PriceCalculator priceCalculator;

    @Override
    public List<ProductFullDto> findProductsForExport() {
        return mapper.mapAsList(
                productEntityDao.findAll().toArray(),
                ProductFullDto.class);
    }

    @Override
    public Optional<ProductDetailInfo> getProductForUpdate(EshopUuid eshopUuid, int olderThanInHours) {
        notNull(eshopUuid, ESHOP_UUID);
        notNegativeAndNotZeroValue(olderThanInHours, "olderThanInHours");

        Optional<ProductEntity> productEntityOpt = productEntityDao.findProductForUpdate(eshopUuid, olderThanInHours);
        // ak sa nenaslo
        if (!productEntityOpt.isPresent()) {
            return empty();
        }
        return of(mapper.map(
                productEntityOpt.get(),
                ProductDetailInfo.class)
        );
    }

    @Override
    public List<ProductFullDto> findProducts(ProductFilterUIDto filter) {
        notNull(filter, "filter");
        //FIXME skusit optimalizovat databazovo

        List<ProductFullDto> productFullDtos = mapper.mapAsList(
                productEntityDao.findByFilter(filter).toArray(),
                ProductFullDto.class);

        productFullDtos.forEach(p ->
                p.setGroupList(mapper.mapAsList(
                        groupEntityDao.findGroupsForProduct(p.getId()),
                        GroupIdNameDto.class))
        );
        return productFullDtos;
    }

    @Override
    public List<ProductInActionDto> findProductsInAction(EshopUuid eshopUuid) {
        List<ProductEntity> productsInAction = productEntityDao.findByFilter(new ProductFilterUIDto(eshopUuid, Boolean.TRUE));

        List<ProductInActionDto> result = new ArrayList<>(productsInAction.size());
        for (ProductEntity entity : productsInAction) {
            // TODO urobit cast cez orika mapping a tu len doplnenie dopocitavaneho atributu
            ProductInActionDto dto = new ProductInActionDto();
            dto.setId(entity.getId());
            dto.setUrl(entity.getUrl());
            dto.setName(entity.getName());
            dto.setEshopUuid(entity.getEshopUuid());
            dto.setPriceForPackage(entity.getPriceForPackage());
            dto.setPriceForOneItemInPackage(entity.getPriceForOneItemInPackage());
            dto.setCommonPrice(entity.getCommonPrice());
            dto.setProductAction(entity.getProductAction());
            dto.setActionValidTo(entity.getActionValidTo());
            // vypocitam percenta
            // FIXME presunut nech sa to perzistuje aby som vedel vyhladavat podla najvecsej zlavy...
            if (entity.getPriceForPackage() != null && entity.getCommonPrice() != null) {
                int actionInPercentage = CalculationUtils.calculatePercetage(entity.getPriceForPackage(),
                        entity.getCommonPrice());
                dto.setActionInPercentage(actionInPercentage);
            } else {
                dto.setActionInPercentage(-1);
            }

            calculateBestPriceInGroup(dto);

            result.add(dto);
        }
        return result;
    }

    private void calculateBestPriceInGroup(ProductInActionDto dto) {
        Optional<Long> groupIdOptional = groupOfProductFindEntityDao.findFirstProductGroupId(dto.getId());
        if (!groupIdOptional.isPresent()) {
            dto.setBestPriceInGroup(BestPriceInGroup.NO_GROUP);
            return;
        }
        //TODO ak je vo viacerych grupach
        List<ProductEntity> products = groupEntityDao.findById(groupIdOptional.get()).getProducts();

        List<ProductEntity> withValidPriceForPackage = products.stream()
                .filter(p -> p.getPriceForUnit() != null)
                .collect(Collectors.toList());

        // FIXME cez db query
        Collections.sort(withValidPriceForPackage, Comparator.comparing(ProductEntity::getPriceForUnit));

        if (withValidPriceForPackage.get(0).getId().equals(dto.getId())) {
            dto.setBestPriceInGroup(BestPriceInGroup.YES);
        } else {
            dto.setBestPriceInGroup(BestPriceInGroup.NO);
        }
    }

    @Override
    public List<ProductBestPriceInGroupDto> findProductsBestPriceInGroupDto(EshopUuid eshopUuid) {
        notNull(eshopUuid, ESHOP_UUID);

        List<ProductEntity> productsInAction = productEntityDao.findByFilter(new ProductFilterUIDto(eshopUuid, Boolean.TRUE));

        return productsInAction.stream()
                .filter(bestPricePredicate())
                .map(entity -> {
                    ProductBestPriceInGroupDto dto = new ProductBestPriceInGroupDto();
                    dto.setId(entity.getId());
                    dto.setUrl(entity.getUrl());
                    dto.setName(entity.getName());
                    dto.setEshopUuid(entity.getEshopUuid());
                    dto.setPriceForPackage(entity.getPriceForPackage());
                    dto.setPriceForOneItemInPackage(entity.getPriceForOneItemInPackage());
                    dto.setCommonPrice(entity.getCommonPrice());
                    dto.setPriceForUnit(entity.getPriceForUnit());
                    dto.setProductAction(entity.getProductAction());
                    dto.setActionValidTo(entity.getActionValidTo());
                    // vypocitam percenta
                    // FIXME presunut nech sa to perzistuje aby som vedel vyhladavat podla najvecsej zlavy...
                    if (entity.getPriceForPackage() != null && entity.getCommonPrice() != null) {
                        int actionInPercentage = CalculationUtils.calculatePercetage(entity.getPriceForPackage(),
                                entity.getCommonPrice());
                        dto.setActionInPercentage(actionInPercentage);
                    } else {
                        dto.setActionInPercentage(-1);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Predicate<ProductEntity> bestPricePredicate() {
        return productEntity -> {
            Long productEntityId = productEntity.getId();
            Optional<Long> groupIdOptional = groupOfProductFindEntityDao.findFirstProductGroupId(productEntityId);
            if (!groupIdOptional.isPresent()) {
                return false;
            }

            //TODO ak je vo viacerych grupach
            List<ProductEntity> products = groupEntityDao.findById(groupIdOptional.get()).getProducts();
            List<ProductEntity> withValidPriceForPackage = products.stream()
                    .filter(p -> p.getPriceForUnit() != null)
                    .collect(Collectors.toList());
            // FIXME cez db query
            Collections.sort(withValidPriceForPackage, Comparator.comparing(ProductEntity::getPriceForUnit));
            return withValidPriceForPackage.get(0).getId().equals(productEntityId);
        };
    }

    @Override
    public StatisticForUpdateForEshopDto getStatisticForUpdateForEshop(EshopUuid eshopUuid, int olderThanInHours) {
        notNull(eshopUuid, ESHOP_UUID);
        notNegativeAndNotZeroValue(olderThanInHours, "olderThanInHours");

        return new StatisticForUpdateForEshopDto(
                eshopUuid,
                productEntityDao.countOfProductsWaitingToBeUpdated(eshopUuid, olderThanInHours),
                productEntityDao.countOfProductsAlreadyUpdated(eshopUuid, olderThanInHours));
    }

    @Override
    public void removeProduct(Long productId) {
        notNull(productId, PRODUCT_ID);

        ProductEntity productEntity = productEntityDao.findById(productId);

        removeProductFromGroup(productEntity);

        productEntityDao.delete(productEntity);
        log.debug("product with id {} was deleted", productId);
    }

    @Override
    public void removeProductByUrl(String productUrl) {
        notNullNotEmpty(productUrl, "productUrl");

        productEntityDao.findByUrl(productUrl)
                .ifPresent(entity -> {

                            // remove from group
                            removeProductFromGroup(entity);

                            productEntityDao.delete(entity);
                            log.debug("product with url {} has been removed", productUrl);
                        }
                );
    }

    private void removeProductFromGroup(ProductEntity productEntity) {
        for (GroupEntity groupEntity : groupEntityDao.findGroupsForProduct(productEntity.getId())) {
            groupEntity.getProducts().remove(productEntity);
            groupEntityDao.update(groupEntity);
            log.debug("removed product '{}' from group '{}'", productEntity.getName(), groupEntity.getName());
        }
    }

    @Override
    public List<ProductFullDto> findProductsInGroup(Long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip) {
        notNull(groupId, "groupId");

        return mapper.mapAsList(
                groupEntityDao.findProductsInGroup(groupId, withPriceOnly, eshopsToSkip).toArray(),
                ProductFullDto.class);
    }

    @Override
    public List<ProductFullDto> findProductsNotInAnyGroup() {
        return mapper.mapAsList(
                groupOfProductFindEntityDao.findProductsWitchAreNotInAnyGroup().toArray(),
                ProductFullDto.class);
    }

    @Override
    public ProductAddingToGroupDto getProduct(Long productId) {
        return mapper.map(productEntityDao.findById(productId), ProductAddingToGroupDto.class);
    }

    @Override
    public boolean existProductWithUrl(String productURL) {
        notNullNotEmpty(productURL, PRODUCT_URL);

        return productEntityDao.existWithUrl(productURL);
    }

    @Override
    public void resetUpdateDateForAllProductsInEshop(EshopUuid eshopUuid) {
        notNull(eshopUuid, ESHOP_UUID);

        //FIXME robit bulkovo po 25 ks, nie vsetky natiahnut naraz
        productEntityDao.findByFilter(new ProductFilterUIDto(eshopUuid))
                .forEach(productEntity -> {
                    productEntity.setLastTimeDataUpdated(null);
                    productEntityDao.update(productEntity);
                });
        log.debug("all products for eshop {} marked as not updated yet", eshopUuid);
    }

    @Override
    public void updateProductCommonPrice(Long productId, BigDecimal newCommonPrice) {
        notNull(productId, PRODUCT_ID);
        notNull(newCommonPrice, "newCommonPrice");
        // TODO validacia na vecsie ako nula...

        ProductEntity product = productEntityDao.findById(productId);
        product.setCommonPrice(newCommonPrice);
        productEntityDao.update(product);

        log.debug("product with id {} was updated with common price {}", productId, newCommonPrice);
    }

    @Override
    public EshopUuid getEshopForProductId(Long productId) {
        notNull(productId, PRODUCT_ID);

        return productEntityDao.findById(productId).getEshopUuid();
    }

    @Override
    public ProductDetailInfo getProductForUpdate(Long productId) {
        notNull(productId, PRODUCT_ID);

        return mapper.map(productEntityDao.findById(productId), ProductDetailInfo.class);
    }

    @Override
    public void updateProduct(ProductUpdateDataDto updateData) {
        notNull(updateData, "updateData");
        notNull(updateData.getId(), "id");
        notNullNotEmpty(updateData.getName(), "name");
        notNullNotEmpty(updateData.getUrl(), "url");
        notNull(updateData.getPriceForPackage(), "priceForPackage");

        ProductDataUpdateEntity productEntity = productDataUpdateEntityDao.findById(updateData.getId());
        productEntity.setName(updateData.getName());
        // can change because of redirect URL, that why update of url
        productEntity.setUrl(updateData.getUrl());

        // prices
        productEntity.setPriceForPackage(updateData.getPriceForPackage());
        BigDecimal priceForOneItemInPackage = priceCalculator.calculatePriceForOneItemInPackage(
                updateData.getPriceForPackage(),
                productEntity.getUnitPackageCount()
        );
        BigDecimal priceForUnit = priceCalculator.calculatePriceForUnit(
                productEntity.getUnit(),
                productEntity.getUnitValue(),
                priceForOneItemInPackage
        );
        productEntity.setPriceForOneItemInPackage(priceForOneItemInPackage);
        productEntity.setPriceForUnit(priceForUnit);
        // action info
        productEntity.setProductAction(updateData.getProductAction());
        productEntity.setActionValidTo(updateData.getActionValidity());

        productEntity.setProductPictureUrl(updateData.getPictureUrl());

        productEntity.setLastTimeDataUpdated(new Date());
        productDataUpdateEntityDao.update(productEntity);

        log.info("product with id {} has been updated with price for package {}",
                productEntity.getId(), updateData.getPriceForPackage());
    }


    @Override
    public void markProductAsUnavailable(Long productId) {
        ProductDataUpdateEntity updateEntity = productDataUpdateEntityDao.findById(productId);
        updateEntity.setLastTimeDataUpdated(new Date());
        // prices
        updateEntity.setPriceForOneItemInPackage(null);
        updateEntity.setPriceForPackage(null);
        updateEntity.setPriceForUnit(null);
        // action
        updateEntity.setProductAction(null);
        updateEntity.setActionValidTo(null);

        productDataUpdateEntityDao.update(updateEntity);
        log.info("product with id {} was reset/mark as unavailable", productId);
    }

    @Override
    public void resetUpdateDateProduct(Long productId) {
        internalLastTimeDataUpdated(productId, null);
    }


    @Override
    public void markProductAsNotInterested(Long productId) {
        // vyhladam povodny produkt
        ProductEntity productEntity = productEntityDao.findById(productId);

        // premapujem do noveho
        NotInterestedProductEntity notInterestedProductEntity = mapper.map(productEntity, NotInterestedProductEntity.class);

        // ulozim ho
        notInterestedProductDbDao.save(notInterestedProductEntity);
        log.debug("created new {} with id {}", notInterestedProductEntity.getClass().getSimpleName(), notInterestedProductEntity.getId());

        // odmazem stary
        for (GroupEntity groupEntity : groupEntityDao.findGroupsForProduct(productId)) {
            groupEntity.getProducts().remove(productEntity);
            groupEntityDao.update(groupEntity);
            log.debug("removed product '{}' from group '{}'", productEntity.getName(), groupEntity.getName());
        }
        productEntityDao.delete(productEntity);
        log.info("deleted {} with id {}", productEntity.getClass().getSimpleName(), productId);
    }

    @Override
    public List<ProductFullDto> findDuplicityProductsByNameAndPriceInEshop(EshopUuid eshopUuid) {
        notNull(eshopUuid, "eshopUuid");

        Map<String, List<ProductEntity>> tmp = new HashMap<>();
        for (ProductEntity productEntity : productEntityDao.findByFilter(new ProductFilterUIDto(eshopUuid))) {
            List<ProductEntity> values = tmp.get(productEntity.getName());
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(productEntity);
            tmp.put(productEntity.getName(), values);
        }

        List<ProductEntity> result = new ArrayList<>();
        for (String name : tmp.keySet()) {
            List<ProductEntity> productEntities = tmp.get(name);
            if (productEntities.size() > 1) {


                result.addAll(productEntities);
            }
        }
        return mapper.mapAsList(result, ProductFullDto.class);
    }

    @Override
    public Optional<Long> getProductWithUrl(String productUrl, Long productIdToIgnore) {
        notNullNotEmpty(productUrl, "productUrl");
        notNull(productIdToIgnore, "productIdToIgnore");

        return productEntityDao.getProductWithUrl(productUrl, productIdToIgnore);
    }

    private void internalLastTimeDataUpdated(Long productId, Date lastTimeDataUpdated) {
        ProductDataUpdateEntity updateEntity = productDataUpdateEntityDao.findById(productId);
        updateEntity.setLastTimeDataUpdated(lastTimeDataUpdated);
        productDataUpdateEntityDao.update(updateEntity);
    }
}
