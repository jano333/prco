package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sk.hudak.prco.dao.db.GroupEntityDao;
import sk.hudak.prco.dao.db.GroupProductKeywordsDao;
import sk.hudak.prco.dto.GroupIdNameDto;
import sk.hudak.prco.dto.GroupProductKeywordsCreateDto;
import sk.hudak.prco.dto.GroupProductKeywordsFullDto;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.GroupProductKeywordsEntity;
import sk.hudak.prco.service.GroupProductKeywordsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Slf4j
@Service("groupProductKeywordsService")
public class GroupProductKeywordsServiceImpl implements GroupProductKeywordsService {

    @Autowired
    private GroupProductKeywordsDao groupProductKeywordsDao;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    private PrcoOrikaMapper mapper;

    @Override
    public Long createGroupProductKeywords(GroupProductKeywordsCreateDto groupProductKeywordsCreateDto) {
        notNull(groupProductKeywordsCreateDto, "groupProductKeywordsCreateDto");
        notNull(groupProductKeywordsCreateDto.getGroupId(), "groupId");
        notNullNotEmpty(groupProductKeywordsCreateDto.getKeyWords(), "keyWords");

        GroupProductKeywordsEntity entity = new GroupProductKeywordsEntity();
        entity.setGroup(groupEntityDao.findById(groupProductKeywordsCreateDto.getGroupId()));
        entity.setKeyWords(groupProductKeywordsCreateDto.getKeyWords().stream()
                .collect(Collectors.joining("|")));

        Long id = groupProductKeywordsDao.save(entity);
        log.debug("create new entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());
        return id;
    }

    @Override
    public Optional<GroupProductKeywordsFullDto> getGroupProductKeywordsByGroupId(Long groupId) {
        notNull(groupId, "groupId");

        List<GroupProductKeywordsEntity> entityList = groupProductKeywordsDao.findByGroupId(groupId);
        if (CollectionUtils.isEmpty(entityList)) {
            return empty();
        }

        GroupProductKeywordsFullDto dto = new GroupProductKeywordsFullDto();
        dto.setGroupIdNameDto(mapper.map(groupEntityDao.findById(groupId), GroupIdNameDto.class));
        dto.setKeyWords(entityList.stream()
                .map(GroupProductKeywordsEntity::getKeyWords)
                .map(str -> str.split("\\|"))
                .collect(Collectors.toList()));
        return Optional.of(dto);
    }

    @Override
    public void removeAllKeywordForGroupId(Long groupId) {
        notNull(groupId, "groupId");

        groupProductKeywordsDao
                .findByGroupId(groupId)
                .forEach(entity -> groupProductKeywordsDao.delete(entity));

        log.debug("all keywords for group id {}", groupId);
    }
}
