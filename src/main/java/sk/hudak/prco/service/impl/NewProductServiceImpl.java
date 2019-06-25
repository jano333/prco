package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dao.db.NewProductEntityDbDao;
import sk.hudak.prco.dto.UnitData;
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.internal.ProductNewData;
import sk.hudak.prco.dto.newproduct.NewProductCreateDto;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.newproduct.NewProductInfoDetail;
import sk.hudak.prco.dto.product.ProductUnitDataDto;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.parser.HtmlParser;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.service.ErrorService;
import sk.hudak.prco.service.NewProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static sk.hudak.prco.utils.Validate.atLeastOneIsNotNull;
import static sk.hudak.prco.utils.Validate.notNegativeAndNotZeroValue;
import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Slf4j
@Service("newProductService")
public class NewProductServiceImpl implements NewProductService {

    @Autowired
    private NewProductEntityDbDao newProductEntityDao;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Autowired
    private UnitParser unitParser;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private HtmlParser htmlParser;

    @Override
    public Long createNewProduct(NewProductCreateDto newProductCreateDto) {
        try {
            notNull(newProductCreateDto, "newProductInfo");
            notNull(newProductCreateDto.getEshopUuid(), "eshopUuid");
            notNullNotEmpty(newProductCreateDto.getUrl(), "url");
            notNullNotEmpty(newProductCreateDto.getName(), "name");

            // check if product with given URL already exist
            if (existProductWithUrl(newProductCreateDto.getUrl())) {
                throw new PrcoRuntimeException("Product with URL " + newProductCreateDto.getUrl() + " already exist.");
            }

            NewProductEntity entity = new NewProductEntity();
            entity.setEshopUuid(newProductCreateDto.getEshopUuid());
            entity.setUrl(newProductCreateDto.getUrl());
            entity.setName(newProductCreateDto.getName());
            entity.setValid(newProductCreateDto.isValid());
            entity.setConfirmValidity(Boolean.FALSE);
            // nepovinne:
            entity.setUnit(newProductCreateDto.getUnit());
            entity.setUnitValue(newProductCreateDto.getUnitValue());
            entity.setUnitPackageCount(newProductCreateDto.getUnitPackageCount());
            entity.setPictureUrl(newProductCreateDto.getPictureUrl());

            Long id = newProductEntityDao.save(entity);
            log.debug("create new entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());
            return id;

        } catch (Exception e) {
            String errMsg = "error creating " + ProductNewData.class.getSimpleName();
            log.debug(errMsg, e);
            if (e instanceof PrcoRuntimeException) {
                throw (PrcoRuntimeException) e;
            }
            throw new PrcoRuntimeException(errMsg, e);
        }
    }

    private boolean existProductWithUrl(String url) {
        //TODO impl
        return false;
    }

    @Override
    public NewProductFullDto getNewProduct(Long newProductId) {
        notNull(newProductId, "newProductId");

        return mapper.map(newProductEntityDao.findById(newProductId), NewProductFullDto.class);
    }

    @Override
    public Optional<NewProductInfoDetail> findFirstInvalidNewProduct() {
        return newProductEntityDao.findFirstInvalid()
                .map(newProductEntity -> mapper.map(newProductEntity, NewProductInfoDetail.class));
    }

    public long getCountOfInvalidNewProduct() {
        return newProductEntityDao.countOfAllInvalidNewProduct();
    }

    @Override
    public void repairInvalidUnitForNewProduct(Long newProductId, UnitData validUnitData) {
        notNull(newProductId, "newProductId");
        notNull(validUnitData, "validUnitData");
        notNull(validUnitData.getUnit(), "unit");
        notNull(validUnitData.getUnitValue(), "unitValue");
        notNull(validUnitData.getUnitPackageCount(), "unitPackageCount");
        //TODO validacie na plusove hodnoty pre unit value a package count...

        //toto by malo hodit mandatory exception ak sa nepodari...
        NewProductEntity entity = newProductEntityDao.findById(newProductId);
        entity.setUnit(validUnitData.getUnit());
        entity.setUnitValue(validUnitData.getUnitValue());
        entity.setUnitPackageCount(validUnitData.getUnitPackageCount());

        entity.setValid(Boolean.TRUE);
        entity.setConfirmValidity(Boolean.TRUE);

        newProductEntityDao.update(entity);

        log.debug("product with id {} has been updated to {}", newProductId, validUnitData);
    }

    @Override
    public void repairInvalidUnitForNewProductByReprocessing(Long newProductId) {
        notNull(newProductId, "newProductId");
        NewProductEntity productEntity = newProductEntityDao.findById(newProductId);
        // parsujem
        ProductNewData productNewData = htmlParser.parseProductNewData(productEntity.getUrl());

        if (productNewData.getUnit() == null) {
            if (StringUtils.isBlank(productEntity.getPictureUrl()) && productNewData.getPictureUrl().isPresent()) {
                log.debug("updating product picture url to {}", productNewData.getPictureUrl());
                productEntity.setPictureUrl(productNewData.getPictureUrl().get());
                newProductEntityDao.update(productEntity);
            }

            log.warn("parsing unit data failed for name {}", productEntity.getName());
            errorService.createError(ErrorCreateDto.builder()
                    .errorType(ErrorType.PARSING_PRODUCT_UNIT_ERR)
                    .url(productEntity.getUrl())
                    .eshopUuid(productEntity.getEshopUuid())
                    .additionalInfo(productEntity.getName())
                    .build());


        } else {
            productEntity.setUnit(productNewData.getUnit());
            productEntity.setUnitValue(productNewData.getUnitValue());
            productEntity.setUnitPackageCount(productNewData.getUnitPackageCount());
            productEntity.setValid(Boolean.TRUE);
            log.debug("new product with id {} was updated with unit data {}", productEntity.getId(),
                    new UnitTypeValueCount(productNewData.getUnit(), productNewData.getUnitValue(), productNewData.getUnitPackageCount()));
            productEntity.setPictureUrl(productNewData.getPictureUrl().isPresent() ? productNewData.getPictureUrl().get() : null);
            newProductEntityDao.update(productEntity);
        }
    }

    @Override
    public void confirmUnitDataForNewProducts(Long... newProductIds) {
        atLeastOneIsNotNull(newProductIds, "newProductIds");

        Stream.of(newProductIds)
                .map(id -> newProductEntityDao.findById(id))
                .forEach(entity -> {
                    entity.setConfirmValidity(Boolean.TRUE);
                    newProductEntityDao.update(entity);
                    log.debug("unit data for product {} mark as confirm", entity.getId());
                });
    }

    @Override
    public long fixAutomaticalyProductUnitData(int maxCountOfInvalid) {
        notNegativeAndNotZeroValue(maxCountOfInvalid, "maxCountOfInvalid");

        List<NewProductEntity> invalidProductList = newProductEntityDao.findInvalid(maxCountOfInvalid);
        log.debug("count of invalid products: {}", invalidProductList.size());

        final int[] countOfRepaired = {0};

        invalidProductList.stream().forEach(productEntity -> {
            Optional<UnitTypeValueCount> unitTypeValueCountOpt = unitParser.parseUnitTypeValueCount(productEntity.getName());
            if (unitTypeValueCountOpt.isPresent()) {
                UnitTypeValueCount unitTypeValueCount = unitTypeValueCountOpt.get();

                productEntity.setUnit(unitTypeValueCount.getUnit());
                productEntity.setUnitValue(unitTypeValueCount.getValue());
                productEntity.setUnitPackageCount(unitTypeValueCount.getPackageCount());
                productEntity.setValid(Boolean.TRUE);
                newProductEntityDao.update(productEntity);

                countOfRepaired[0]++;
            }
        });
        log.info("count of repaired products: {}", countOfRepaired[0]);
        return countOfRepaired[0];
    }

    @Override
    public List<NewProductFullDto> findNewProducts(NewProductFilterUIDto filter) {
        List<NewProductEntity> entities = newProductEntityDao.findByFilter(filter);

        //FIXME toto nefunguje(opravit aby fungovalo cez oriku):
//        List<NewProductFullDto> result = mapper.mapAsList(entities, NewProductFullDto.class);

        List<NewProductFullDto> result = new ArrayList<>(entities.size());
        for (NewProductEntity entity : entities) {
            result.add(NewProductFullDto.builder()
                    .created(entity.getCreated())
                    .updated(entity.getUpdated())
                    .id(entity.getId())
                    .url(entity.getUrl())
                    .name(entity.getName())
                    .eshopUuid(entity.getEshopUuid())
                    .unit(entity.getUnit())
                    .unitValue(entity.getUnitValue())
                    .unitPackageCount(entity.getUnitPackageCount())
                    .valid(entity.getValid())
                    .confirmValidity(entity.getConfirmValidity())
                    .pictureUrl(entity.getPictureUrl())
                    .build());
        }
        return result;
    }

    @Override
    public List<NewProductFullDto> findNewProductsForExport() {
        return mapper.mapAsList(newProductEntityDao.findAll(), NewProductFullDto.class);
    }

    @Override
    public void updateProductUnitData(ProductUnitDataDto productUnitDataDto) {
        notNull(productUnitDataDto, "productUnitDataDto");
        notNull(productUnitDataDto.getId(), "id");
        notNullNotEmpty(productUnitDataDto.getUnit(), "unit");
        notNull(productUnitDataDto.getUnitValue(), "unitValue");
        notNull(productUnitDataDto.getUnitPackageCount(), "unitPackageCount");

        // name is ignored

        NewProductEntity entity = newProductEntityDao.findById(productUnitDataDto.getId());
        entity.setUnit(Unit.valueOf(productUnitDataDto.getUnit()));
        entity.setUnitValue(productUnitDataDto.getUnitValue());
        entity.setUnitPackageCount(productUnitDataDto.getUnitPackageCount());
        entity.setValid(Boolean.TRUE);
        newProductEntityDao.update(entity);

        log.debug("new product with id {} was updated for unit data values", productUnitDataDto.getId());

    }

    @Override
    public void deleteNewProducts(Long... newProductIds) {
        for (Long newProductId : newProductIds) {
            newProductEntityDao.delete(newProductEntityDao.findById(newProductId));
        }
    }

    @Override
    public long getCountOfAllNewProducts() {
        return newProductEntityDao.getCountOfAllNewProducts();
    }


}
