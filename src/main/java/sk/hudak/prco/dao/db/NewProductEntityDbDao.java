package sk.hudak.prco.dao.db;

import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.dto.NewProductFilterUIDto;
import sk.hudak.prco.model.NewProductEntity;

import java.util.List;
import java.util.Optional;

public interface NewProductEntityDbDao extends BaseDao<NewProductEntity> {

    boolean existWithUrl(String url);

    List<NewProductEntity> findAll();

    List<NewProductEntity> findInvalid(int maxCountOfInvalid);

    long countOfAllInvalidNewProduct();

    Optional<NewProductEntity> findFirstInvalid();

    List<NewProductEntity> findByFilter(NewProductFilterUIDto filter);

    //TODO rename na countOfAllNewProducts
    long getCountOfAllNewProducts();
}
