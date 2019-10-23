package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import sk.hudak.prco.dao.db.GroupEntityDao
import sk.hudak.prco.dao.db.GroupProductKeywordsDao
import sk.hudak.prco.dto.GroupIdNameDto
import sk.hudak.prco.dto.GroupProductKeywordsCreateDto
import sk.hudak.prco.dto.GroupProductKeywordsFullDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.model.GroupEntity
import sk.hudak.prco.model.GroupProductKeywordsEntity
import sk.hudak.prco.service.GroupProductKeywordsService
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.stream.Collectors

@Service("groupProductKeywordsService")
class GroupProductKeywordsServiceImpl(private val groupEntityDao: GroupEntityDao,
                                      private val groupProductKeywordsDao: GroupProductKeywordsDao,
                                      private val mapper: PrcoOrikaMapper)
    : GroupProductKeywordsService {

    companion object {
        val LOG = LoggerFactory.getLogger(GroupProductKeywordsServiceImpl::class.java)!!
    }

    override fun findAllGroupProductKeywords(): List<GroupProductKeywordsFullDto> {
        val result: MutableList<GroupProductKeywordsFullDto> = ArrayList()
        groupProductKeywordsDao.findGroupsl().forEach {
            result.add(GroupProductKeywordsFullDto(
                    it.toGroupIdNameDto(),
                    groupProductKeywordsDao.findKeywordsForGroupId(it.id)))
        }
        return result
    }

    private fun GroupEntity.toGroupIdNameDto(): GroupIdNameDto {
        val groupIdNameDto = GroupIdNameDto()
        groupIdNameDto.id = this.id
        groupIdNameDto.name = this.name
        return groupIdNameDto
    }

    override fun createGroupProductKeywords(createDto: GroupProductKeywordsCreateDto): Long {
        notNullNotEmpty(createDto.keyWords, "keyWords")

        val keywords = createDto.keyWords.stream()
                .collect(Collectors.joining("|"))

        if (existGivenGroupWithKeywords(createDto.groupId, keywords)) {
            throw PrcoRuntimeException("Group with id ${createDto.groupId} and keywords $keywords already exist.")
        }

        val entity = GroupProductKeywordsEntity()
        entity.group = groupEntityDao.findById(createDto.groupId)
        entity.keyWords = keywords

        val id = groupProductKeywordsDao.save(entity)
        LOG.debug("create new entity ${entity.javaClass.simpleName} with id ${entity.id}")
        return id
    }

    private fun existGivenGroupWithKeywords(groupId: Long, keywords: String): Boolean =
            groupProductKeywordsDao.existGroupWithKeywords(groupId, keywords)

    override fun getGroupProductKeywordsByGroupId(groupId: Long): GroupProductKeywordsFullDto? {
        val entityList = groupProductKeywordsDao.findByGroupId(groupId)
        if (CollectionUtils.isEmpty(entityList)) {
            return null
        }
        return GroupProductKeywordsFullDto(
                groupIdNameDto = mapper.map(groupEntityDao.findById(groupId), GroupIdNameDto::class.java),
                keyWords = entityList.stream()
                        .map { it.keyWords }
                        .map { str -> str!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
                        .collect(Collectors.toList())
        )
    }

    override fun removeAllKeywordForGroupId(groupId: Long) {
        groupProductKeywordsDao.findByGroupId(groupId)
                .forEach { groupProductKeywordsDao.delete(it) }

        LOG.debug("all keywords for group id $groupId were removed")
    }
}
