package sk.hudak.prco.dao.db;

import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.model.ErrorEntity;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ErrorEntityDao extends BaseDao<ErrorEntity> {

    ErrorEntity findByUrl(String url);

    List<ErrorEntity> findAll();

    List<ErrorEntity> findByTypes(ErrorType... errorTypes);

    /**
     * vrati pocet chyb na zaklade typu
     *
     * @param type
     * @return
     */
    Long getCountOfType(ErrorType type);

    /**
     * vyhlada starsie ako pocet jednotie z vstupu(zatial podpovovane len dni !!)
     *
     * @param unitCount
     * @param timeUnit
     * @return zoznam entit
     */
    List<ErrorEntity> findOlderThan(int unitCount, TimeUnit timeUnit);

    List<ErrorEntity> findByUrls(List<String> urls);

    List<ErrorEntity> findByMaxCount(int limit, ErrorType errorType);
}
