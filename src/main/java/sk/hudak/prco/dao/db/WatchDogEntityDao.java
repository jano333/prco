package sk.hudak.prco.dao.db;

import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.model.WatchDogEntity;

import java.util.List;

public interface WatchDogEntityDao extends BaseDao<WatchDogEntity> {

    List<WatchDogEntity> findAll();

    boolean existWithUrl(String productUrl);
}
