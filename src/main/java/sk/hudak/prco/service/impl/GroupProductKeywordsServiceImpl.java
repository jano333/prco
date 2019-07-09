package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.dao.db.GroupEntityDao;
import sk.hudak.prco.dao.db.GroupProductKeywordsDao;
import sk.hudak.prco.dto.group.GroupProductKeywordsCreateDto;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.GroupProductKeywordsEntity;
import sk.hudak.prco.service.GroupProductKeywordsService;

import java.util.stream.Collectors;

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
}
