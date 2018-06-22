package sk.hudak.prco.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.internal.StatisticForUpdateForEshopDto;
import sk.hudak.prco.manager.UpdateStatusInfo;
import sk.hudak.prco.model.GroupEntity;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.model.NotInterestedProductEntity;
import sk.hudak.prco.model.ProductEntity;

@Component
public class PrcoOrikaMapper extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {
        config_NewProductEntity_To_ProductEntity(factory);
        config_NewProductEntity_To_NotInterestedProductEntity(factory);

        config_GroupEntity_To_GroupIdNameDto(factory);
        config_StatisticForUpdateForEshopDto_To_UpdateStatusInfo(factory);
    }

    private void config_NewProductEntity_To_ProductEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(NewProductEntity.class, ProductEntity.class)
                .fieldMap("id").exclude().add()
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
}
