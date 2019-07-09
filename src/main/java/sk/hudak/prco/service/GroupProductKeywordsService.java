package sk.hudak.prco.service;

import sk.hudak.prco.dto.group.GroupProductKeywordsCreateDto;
import sk.hudak.prco.dto.group.GroupProductKeywordsFullDto;

import java.util.Optional;

public interface GroupProductKeywordsService {

    Long createGroupProductKeywords(GroupProductKeywordsCreateDto groupProductKeywordsCreateDto);

    Optional<GroupProductKeywordsFullDto> getGroupProductKeywordsByGroupId(Long groupId);
}
