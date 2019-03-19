package sk.hudak.prco.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.dao.db.NotInterestedProductDbDao;
import sk.hudak.prco.service.NotInterestedProductService;

@Slf4j
@Service("notInterestedProductService")
public class NotInterestedProductServiceImpl implements NotInterestedProductService {

    @Autowired
    private NotInterestedProductDbDao notInterestedProductDbDao;

    @Override
    public void deleteNotInterestedProducts(@NonNull Long... notInterestedProductIds) {
        for (Long notInterestedProductId : notInterestedProductIds) {
            notInterestedProductDbDao.delete(notInterestedProductDbDao.findById(notInterestedProductId));
            log.debug("product with id {} was removed", notInterestedProductId);
        }
    }
}
