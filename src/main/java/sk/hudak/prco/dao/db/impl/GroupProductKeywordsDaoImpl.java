package sk.hudak.prco.dao.db.impl;

import org.springframework.stereotype.Component;
import sk.hudak.prco.dao.db.GroupProductKeywordsDao;
import sk.hudak.prco.model.GroupProductKeywordsEntity;
import sk.hudak.prco.model.QGroupProductKeywordsEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupProductKeywordsDaoImpl extends BaseDaoImpl<GroupProductKeywordsEntity> implements GroupProductKeywordsDao {

    @Override
    public GroupProductKeywordsEntity findById(Long id) {
        return findById(GroupProductKeywordsEntity.class, id);
    }

    @Override
    public List<GroupProductKeywordsEntity> findByGroupId(Long groupId) {
        return getQueryFactory()
                .select(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .from(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .where(QGroupProductKeywordsEntity.groupProductKeywordsEntity.group.id.eq(groupId))
                .fetch();
    }

    @Override
    public List<String[]> findKeywordsForGroupId(Long groupId) {
        return getQueryFactory()
                .select(QGroupProductKeywordsEntity.groupProductKeywordsEntity.keyWords)
                .from(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .where(QGroupProductKeywordsEntity.groupProductKeywordsEntity.group.id.eq(groupId))
                .fetch()
                .stream()
                .map(value -> value.split("\\|"))
                .collect(Collectors.toList());
    }
}
