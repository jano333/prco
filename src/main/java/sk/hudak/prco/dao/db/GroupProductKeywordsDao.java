package sk.hudak.prco.dao.db;

import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.model.GroupProductKeywordsEntity;

import java.util.List;

public interface GroupProductKeywordsDao extends BaseDao<GroupProductKeywordsEntity> {

    GroupProductKeywordsEntity findByGroupId(Long groupId);

    List<String[]> findKeywordsForGroupId(Long groupId);
}
