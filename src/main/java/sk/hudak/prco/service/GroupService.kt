package sk.hudak.prco.service;

import sk.hudak.prco.dto.GroupCreateDto;
import sk.hudak.prco.dto.GroupFilterDto;
import sk.hudak.prco.dto.GroupIdNameDto;
import sk.hudak.prco.dto.GroupListDto;
import sk.hudak.prco.dto.GroupListExtendedDto;
import sk.hudak.prco.dto.GroupUpdateDto;

import java.util.List;

public interface GroupService {

    Long createGroup(GroupCreateDto createDto);

    void updateGroup(GroupUpdateDto updateDto);

    void addProductsToGroup(Long groupId, Long... productIds);

    void removeProductsFromGroup(Long groupId, Long... productIds);

    List<GroupListDto> getGroupsWithoutProduct(Long productId);

    List<GroupListDto> findGroups(GroupFilterDto groupFilterDto);

    List<GroupListExtendedDto> findAllGroupExtended();

    GroupIdNameDto getGroupById(Long groupId);
}
