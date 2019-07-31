package sk.hudak.prco.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dao.db.NotInterestedProductDbDao;
import sk.hudak.prco.dto.product.NotInterestedProductFindDto;
import sk.hudak.prco.model.NotInterestedProductEntity;
import sk.hudak.prco.service.NotInterestedProductService;

@Slf4j
@Service("notInterestedProductService")
public class NotInterestedProductServiceImpl implements NotInterestedProductService {

    @Autowired
    private NotInterestedProductDbDao notInterestedProductDbDao;

    @Override
    public void deleteNotInterestedProducts(@NonNull long... notInterestedProductIds) {
        for (Long notInterestedProductId : notInterestedProductIds) {
            notInterestedProductDbDao.delete(notInterestedProductDbDao.findById(notInterestedProductId));
            log.debug("product with id {} was removed", notInterestedProductId);
        }
    }

    @Override
    public void deleteNotInterestedProducts(EshopUuid eshopUuid) {
        for (NotInterestedProductEntity entity : notInterestedProductDbDao.findAll(new NotInterestedProductFindDto(eshopUuid))) {
            notInterestedProductDbDao.delete(entity);
        }
    }
}
