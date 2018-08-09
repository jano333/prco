package sk.hudak.prco.dao.db;

import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.model.ErrorEntity;

import java.util.List;

public interface ErrorEntityDao extends BaseDao<ErrorEntity> {

    ErrorEntity findByUrl(String url);

    List<ErrorEntity> findAll();

    List<ErrorEntity> findByTypes(ErrorType... errorTypes);

    Long getCountOfType(ErrorType type);
}
