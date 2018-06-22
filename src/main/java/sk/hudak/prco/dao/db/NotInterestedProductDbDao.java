package sk.hudak.prco.dao.db;

import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.model.NotInterestedProductEntity;

import java.util.List;

public interface NotInterestedProductDbDao extends BaseDao<NotInterestedProductEntity> {

    boolean existWithUrl(String url);

    List<NotInterestedProductEntity> findAll();
}
