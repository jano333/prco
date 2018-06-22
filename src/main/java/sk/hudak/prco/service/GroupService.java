package sk.hudak.prco.service;

import sk.hudak.prco.dto.group.GroupCreateDto;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.dto.group.GroupIdNameDto;
import sk.hudak.prco.dto.group.GroupListDto;
import sk.hudak.prco.dto.group.GroupListExtendedDto;
import sk.hudak.prco.dto.group.GroupUpdateDto;

import java.util.List;

public interface GroupService {

    Long createGroup(GroupCreateDto createDto);

    void updateGroup(GroupUpdateDto updateDto);

    void addProductsToGroup(Long groupId, Long... productIds);

    List<GroupListDto> getGroupsWithoutProduct(Long productId);

    List<GroupListDto> findGroups(GroupFilterDto groupFilterDto);

    List<GroupListExtendedDto> findAllGroupExtended();

    GroupIdNameDto getGroupById(Long groupId);
}
