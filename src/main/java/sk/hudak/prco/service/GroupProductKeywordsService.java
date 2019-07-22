package sk.hudak.prco.service;

import sk.hudak.prco.dto.group.GroupProductKeywordsCreateDto;
import sk.hudak.prco.dto.group.GroupProductKeywordsFullDto;

import java.util.Optional;

public interface GroupProductKeywordsService {

    /**
     * @param groupProductKeywordsCreateDto data from creating of new keyword for given group
     * @return primary key id
     */
    Long createGroupProductKeywords(GroupProductKeywordsCreateDto groupProductKeywordsCreateDto);

    /**
     * @param groupId group id
     * @return
     */
    Optional<GroupProductKeywordsFullDto> getGroupProductKeywordsByGroupId(Long groupId);

    /**
     * @param groupId group id
     */
    void removeAllKeywordForGroupId(Long groupId);
}
