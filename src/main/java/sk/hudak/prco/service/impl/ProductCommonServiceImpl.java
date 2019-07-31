package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dao.db.GroupEntityDao;
import sk.hudak.prco.dao.db.GroupOfProductFindEntityDao;
import sk.hudak.prco.dao.db.NewProductEntityDbDao;
import sk.hudak.prco.dao.db.NotInterestedProductDbDao;
import sk.hudak.prco.dao.db.ProductDataUpdateEntityDao;
import sk.hudak.prco.dao.db.ProductEntityDao;
import sk.hudak.prco.dto.EshopProductInfoDto;
import sk.hudak.prco.dto.ProductStatisticInfoDto;
import sk.hudak.prco.dto.product.NewProductFullDto;
import sk.hudak.prco.dto.product.NotInterestedProductFindDto;
import sk.hudak.prco.dto.product.NotInterestedProductFullDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.model.NotInterestedProductEntity;
import sk.hudak.prco.model.ProductEntity;
import sk.hudak.prco.service.ProductCommonService;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sk.hudak.prco.utils.Validate.atLeastOneIsNotNull;
import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Slf4j
@Service("productCommonService")
public class ProductCommonServiceImpl implements ProductCommonService {

    @Autowired
    private NewProductEntityDbDao newProductEntityDao;

    @Autowired
    private NotInterestedProductDbDao notInterestedProductDbDao;

    @Autowired
    private ProductEntityDao productEntityDao;

    @Autowired
    private ProductDataUpdateEntityDao productDataUpdateEntityDao;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    private GroupOfProductFindEntityDao groupOfProductFindEntityDao;

    @Autowired
    private PrcoOrikaMapper mapper;


    @Override
    public boolean existProductWithURL(String productURL) {
        return internalExistProductWithURL(productURL);
    }

    private boolean internalExistProductWithURL(String productURL) {
        notNullNotEmpty("productURL", productURL);

        // nove produkty
        if (newProductEntityDao.existWithUrl(productURL)) {
            return true;
        }
        // produkty, o ktore nemam zaujem
        if (notInterestedProductDbDao.existWithUrl(productURL)) {
            return true;
        }
        // produkty, o ktore mam zaujem - aktualizuju sa
        return productEntityDao.existWithUrl(productURL);
    }

    @Override
    public void markNewProductAsInterested(Long... newProductIds) {
        atLeastOneIsNotNull(newProductIds, "newProductIds");

        Arrays.asList(newProductIds).forEach(newProductId -> {

            // find existing new product
            NewProductEntity newProductEntity = newProductEntityDao.findById(newProductId);

            if (!Boolean.TRUE.equals(newProductEntity.getConfirmValidity())) {
                throw new PrcoRuntimeException(newProductEntity.getClass().getSimpleName() + " with id " + newProductEntity.getId() + " is not confirmed.");
            }

            // overim ci uz s takou URL neexistuje PRODUCT... ak ano tak ho len odsranim z NEW a nerobim save do ProductEntity
            if (productEntityDao.existWithUrl(newProductEntity.getUrl())) {
                log.debug("product with url {} already exist in products -> deleting from new", newProductEntity.getUrl());

            } else {
                // map NewProductEntity -> ProductEntity
                ProductEntity productEntity = mapper.map(newProductEntity, ProductEntity.class);

                // save new ProductEntity
                productEntityDao.save(productEntity);
                log.debug("created new {} with id {}", productEntity.getClass().getSimpleName(), productEntity.getId());
            }

            // remove NewProductEntity
            newProductEntityDao.delete(newProductEntity);
            log.debug("deleted {} with id {}", newProductEntity.getClass().getSimpleName(), newProductEntity.getId());
        });
    }

    @Override
    public void markNewProductAsNotInterested(Long... newProductIds) {
        atLeastOneIsNotNull(newProductIds, "newProductIds");

        for (Long newProductId : newProductIds) {

            // find existing product
            NewProductEntity newProductEntity = newProductEntityDao.findById(newProductId);

            //TODO tak ako v metode hore impl -> overim ci uz s takou URL neexistuje PRODUCT...

            // map NewProductEntity -> NotInterestedProductEntity
            NotInterestedProductEntity notInterestedProductEntity = mapper.map(newProductEntity, NotInterestedProductEntity.class);

            // save new NotInterestedProductEntity
            notInterestedProductDbDao.save(notInterestedProductEntity);
            log.trace("created new {} with id {}", NotInterestedProductEntity.class.getSimpleName(), notInterestedProductEntity.getId());

            // remove NewProductEntity
            newProductEntityDao.delete(newProductEntity);
            log.debug("deleted {} with id {}", NewProductEntity.class.getSimpleName(), newProductEntity.getId());
        }
    }


    @Override
    public List<NotInterestedProductFullDto> findNotInterestedProducts(NotInterestedProductFindDto findDto) {
        return mapper.mapAsList(notInterestedProductDbDao.findAll(findDto), NotInterestedProductFullDto.class);
    }

    @Override
    public long importNewProducts(List<NewProductFullDto> newProductList) {
        notNull(newProductList, "newProductList");

        // validacia na povinne parametre
        newProductList.forEach(dto -> {
            notNull(dto, "dto");
            notNullNotEmpty(dto.getUrl(), "url");
            notNullNotEmpty(dto.getName(), "name");
            notNull(dto.getConfirmValidity(), "confirmValidity");
        });

        // filter na tie, ktore este nemam v DB
        List<NewProductFullDto> notExistingYet = newProductList.stream()
                .filter(dto -> !internalExistProductWithURL(dto.getUrl()))
                .collect(Collectors.toList());


        // premapovanie a save do DB
        notExistingYet.forEach(dto -> {
            // NewProductFullDto -> NewProductEntity
            newProductEntityDao.save(mapper.map(dto, NewProductEntity.class));
        });

        return notExistingYet.size();
    }

    @Override
    public long importProducts(List<ProductFullDto> productList) {
        notNull(productList, "productList");

        productList.forEach(dto -> {
            notNull(dto, "dto");
            notNullNotEmpty(dto.getUrl(), "url");
            notNullNotEmpty(dto.getName(), "name");
            //TODO ostatne validacie na povinne atributy

        });

        // filter na tie, ktore este nemam v DB
        List<ProductFullDto> notExistingYet = productList.stream()
                .filter(dto -> !internalExistProductWithURL(dto.getUrl()))
                .collect(Collectors.toList());

        // premapovanie a save do DB
        notExistingYet.forEach(dto -> productEntityDao.save(mapper.map(dto, ProductEntity.class)));

        return notExistingYet.size();
    }

    @Override
    public long importNotInterestedProducts(List<NotInterestedProductFullDto> productList) {
        notNull(productList, "productList");

        productList.forEach(dto -> {
            notNull(dto, "dto");
            notNullNotEmpty(dto.getUrl(), "url");
            notNullNotEmpty(dto.getName(), "name");
            //TODO ostatne validacie na povinne atributy

        });

        // filter na tie, ktore este nemam v DB
        List<NotInterestedProductFullDto> notExistingYet = productList.stream()
                .filter(dto -> !internalExistProductWithURL(dto.getUrl()))
                .collect(Collectors.toList());

        // premapovanie a save do DB
        notExistingYet.forEach(dto -> notInterestedProductDbDao.save(mapper.map(dto, NotInterestedProductEntity.class)));

        return notExistingYet.size();
    }

    @Override
    @Transactional
    public ProductStatisticInfoDto getStatisticsOfProducts() {

        ProductStatisticInfoDto result = new ProductStatisticInfoDto();
        result.setCountOfAllProducts(productEntityDao.count());
        result.setCountOfProductsNotInAnyGroup(groupOfProductFindEntityDao.countOfProductsWitchAreNotInAnyGroup());

        List<String> groupNames = groupEntityDao.findAllGroupNames();
        Map<String, Long> countProductInGroup = new HashMap<>(groupNames.size());
        // FIXME cez stream prepisat
        for (String groupName : groupNames) {
            countProductInGroup.put(groupName, groupOfProductFindEntityDao.countOfProductInGroup(groupName));
        }
        result.setCountProductInGroup(countProductInGroup);

        Map<EshopUuid, EshopProductInfoDto> eshopProductInfo = new EnumMap<>(EshopUuid.class);
        Arrays.stream(EshopUuid.values()).forEach(eshopUuid -> {
            long countOfAllProduct = productEntityDao.countOfAllProductInEshop(eshopUuid);
            long countOfAlreadyUpdated = productEntityDao.countOfAllProductInEshopUpdatedMax24Hours(eshopUuid);
            eshopProductInfo.put(eshopUuid, new EshopProductInfoDto(countOfAllProduct, countOfAlreadyUpdated));
        });
        result.setEshopProductInfo(eshopProductInfo);

        return result;
    }
}
