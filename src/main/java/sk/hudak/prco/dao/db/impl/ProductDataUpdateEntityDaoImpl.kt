package sk.hudak.prco.dao.db.impl;

import org.springframework.stereotype.Component;
import sk.hudak.prco.dao.db.ProductDataUpdateEntityDao;
import sk.hudak.prco.model.ProductDataUpdateEntity;

@Component
public class ProductDataUpdateEntityDaoImpl extends BaseDaoImpl<ProductDataUpdateEntity> implements ProductDataUpdateEntityDao {

    @Override
    public ProductDataUpdateEntity findById(long id) {
        return findById(ProductDataUpdateEntity.class, id);
    }
}
