package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.dao.db.NewProductEntityDbDao;
import sk.hudak.prco.dto.UnitData;
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.dto.newproduct.NewProductCreateDto;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.dto.newproduct.NewProductFullDto;
import sk.hudak.prco.dto.newproduct.NewProductInfo;
import sk.hudak.prco.dto.newproduct.NewProductInfoDetail;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.parser.UnitParser;
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

    @Override
    public Long createNewProduct(NewProductCreateDto newProductCreateDto) {
        try {
            notNull(newProductCreateDto, "newProductInfo");
            notNull(newProductCreateDto.getEshopUuid(), "eshopUuid");
            notNullNotEmpty(newProductCreateDto.getUrl(), "url");
            notNullNotEmpty(newProductCreateDto.getName(), "name");

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

            Long id = newProductEntityDao.save(entity);
            log.debug("create new entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());
            return id;

        } catch (Exception e) {
            String errMsg = "error creating " + NewProductInfo.class.getSimpleName();
            log.debug(errMsg, e);
            throw new PrcoRuntimeException(errMsg, e);
        }
    }

    @Override
    public Optional<NewProductInfoDetail> findFirstInvalidNewProduct() {
        Optional<NewProductEntity> invalid = newProductEntityDao.findFirstInvalid();
        if (invalid.isPresent()) {
            return Optional.of(mapper.map(invalid.get(), NewProductInfoDetail.class));
        }
        return Optional.empty();
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

        Optional<UnitTypeValueCount> unitTypeValueCountOpt = unitParser.parseUnitTypeValueCount(productEntity.getName());
        if (unitTypeValueCountOpt.isPresent()) {
            UnitTypeValueCount unitTypeValueCount = unitTypeValueCountOpt.get();

            productEntity.setUnit(unitTypeValueCount.getUnit());
            productEntity.setUnitValue(unitTypeValueCount.getValue());
            productEntity.setUnitPackageCount(unitTypeValueCount.getPackageCount());
            productEntity.setValid(Boolean.TRUE);
            newProductEntityDao.update(productEntity);
            log.debug("New product with id {} was updated with unit data {}", productEntity.getId(), unitTypeValueCountOpt);
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
                    .build());
        }
        return result;
    }

    @Override
    public List<NewProductFullDto> findNewProductsForExport() {
        return mapper.mapAsList(newProductEntityDao.findAll(), NewProductFullDto.class);
    }



}
