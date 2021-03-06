package sk.hudak.prco.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import sk.hudak.prco.dto.ErrorListDto;
import sk.hudak.prco.dto.GroupIdNameDto;
import sk.hudak.prco.dto.ProductNewData;
import sk.hudak.prco.dto.StatisticForUpdateForEshopDto;
import sk.hudak.prco.dto.product.NewProductCreateDto;
import sk.hudak.prco.dto.product.NewProductFullDto;
import sk.hudak.prco.dto.product.ProductAddingToGroupDto;
import sk.hudak.prco.dto.product.ProductFullDto;
import sk.hudak.prco.manager.updateprocess.UpdateStatusInfo;
import sk.hudak.prco.model.ErrorEntity;
import sk.hudak.prco.model.GroupEntity;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.model.NotInterestedProductEntity;
import sk.hudak.prco.model.ProductEntity;

@Component
public class PrcoOrikaMapper extends ConfigurableMapper {

    // uroit tiredu ktore rozsuje jednotlive class
    //TODO zrusit tuto class a pouzit v kotline:
    // https://stackoverflow.com/questions/39199426/better-way-to-map-kotlin-data-objects-to-data-objects

    @Override
    protected void configure(MapperFactory factory) {

        config_NewProductEntity_To_ProductEntity(factory);
        config_NewProductEntity_To_NotInterestedProductEntity(factory);
        config_NewProductEntity_To_NewProductFullDto(factory);

        config_ProductEntity_To_NotInterestedProductEntity(factory);
        config_ProductEntity_To_ProductFullDto(factory);
        config_ProductEntity_To_ProductAddingToGroupDto(factory);

        config_ErrorEntity_To_ErrorListDto(factory);

        config_GroupEntity_To_GroupIdNameDto(factory);

        config_StatisticForUpdateForEshopDto_To_UpdateStatusInfo(factory);


        config_ProductNewData_To_NewProductCreateDto(factory);
    }

    private void config_ProductNewData_To_NewProductCreateDto(MapperFactory mapperFactory) {
        mapperFactory.classMap(ProductNewData.class, NewProductCreateDto.class)
                .customize(new CustomMapper<ProductNewData, NewProductCreateDto>() {
                    @Override
                    public void mapAtoB(ProductNewData productNewData, NewProductCreateDto newProductCreateDto, MappingContext context) {
                        if (productNewData.getName() != null) {
                            newProductCreateDto.setName(productNewData.getName());
                        }
                        if (productNewData.getPictureUrl() != null) {
                            newProductCreateDto.setPictureUrl(productNewData.getPictureUrl());
                        }
                    }
                })
                .fieldMap("name").exclude().add()
                .fieldMap("pictureUrl").exclude().add()
                .byDefault()
                .register();
    }

    private void config_ProductEntity_To_NotInterestedProductEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(ProductEntity.class, NotInterestedProductEntity.class)
                .fieldMap("id").exclude().add()
                .byDefault()
                .register();
    }

    private void config_ProductEntity_To_ProductFullDto(MapperFactory mapperFactory) {
        //TODO zoznam group
        mapperFactory.classMap(ProductEntity.class, ProductFullDto.class)
                .byDefault()
                .register();
    }

    private void config_ProductEntity_To_ProductAddingToGroupDto(MapperFactory mapperFactory) {
        mapperFactory.classMap(ProductEntity.class, ProductAddingToGroupDto.class)
                .byDefault()
                .register();
    }

    private void config_NewProductEntity_To_NewProductFullDto(MapperFactory mapperFactory) {
        mapperFactory.classMap(NewProductEntity.class, NewProductFullDto.class)
                .byDefault()
                .register();
    }

    private void config_NewProductEntity_To_ProductEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(NewProductEntity.class, ProductEntity.class)
                .fieldMap("id").exclude().add()
                .fieldMap("pictureUrl", "productPictureUrl").add()
                .byDefault()
                .register();
    }

    private void config_NewProductEntity_To_NotInterestedProductEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(NewProductEntity.class, NotInterestedProductEntity.class)
                .fieldMap("id").exclude().add()
                .byDefault()
                .register();
    }

    private void config_GroupEntity_To_GroupIdNameDto(MapperFactory mapperFactory) {
        mapperFactory.classMap(GroupEntity.class, GroupIdNameDto.class)
                .byDefault()
                .register();
    }

    private void config_StatisticForUpdateForEshopDto_To_UpdateStatusInfo(MapperFactory mapperFactory) {
        mapperFactory.classMap(StatisticForUpdateForEshopDto.class, UpdateStatusInfo.class)
                .byDefault()
                .register();
    }

    private void config_ErrorEntity_To_ErrorListDto(MapperFactory mapperFactory) {
        mapperFactory.classMap(ErrorEntity.class, ErrorListDto.class)
                .byDefault()
                .register();
    }
}
