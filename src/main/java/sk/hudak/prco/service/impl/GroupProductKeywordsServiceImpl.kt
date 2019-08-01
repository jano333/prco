package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import sk.hudak.prco.dao.db.GroupEntityDao
import sk.hudak.prco.dao.db.GroupProductKeywordsDao
import sk.hudak.prco.dto.GroupIdNameDto
import sk.hudak.prco.dto.GroupProductKeywordsCreateDto
import sk.hudak.prco.dto.GroupProductKeywordsFullDto
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.model.GroupProductKeywordsEntity
import sk.hudak.prco.service.GroupProductKeywordsService
import sk.hudak.prco.utils.Validate.notNull
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import java.util.Optional.empty
import java.util.stream.Collectors

@Service("groupProductKeywordsService")
class GroupProductKeywordsServiceImpl : GroupProductKeywordsService {

    @Autowired
    private val groupProductKeywordsDao: GroupProductKeywordsDao? = null

    @Autowired
    private val groupEntityDao: GroupEntityDao? = null

    @Autowired
    private val mapper: PrcoOrikaMapper? = null

    companion object {
        val log = LoggerFactory.getLogger(GroupProductKeywordsServiceImpl::class.java)!!
    }

    override fun createGroupProductKeywords(groupProductKeywordsCreateDto: GroupProductKeywordsCreateDto): Long? {
        notNull(groupProductKeywordsCreateDto, "groupProductKeywordsCreateDto")
        notNull(groupProductKeywordsCreateDto.groupId, "groupId")
        notNullNotEmpty(groupProductKeywordsCreateDto.keyWords, "keyWords")

        val entity = GroupProductKeywordsEntity()
        entity.group = groupEntityDao!!.findById(groupProductKeywordsCreateDto.groupId)
        entity.keyWords = groupProductKeywordsCreateDto.keyWords.stream()
                .collect(Collectors.joining("|"))

        val id = groupProductKeywordsDao!!.save(entity)
        log.debug("create new entity {} with id {}", entity.javaClass.simpleName, entity.id)
        return id
    }

    override fun getGroupProductKeywordsByGroupId(groupId: Long?): Optional<GroupProductKeywordsFullDto> {
        notNull(groupId, "groupId")

        val entityList = groupProductKeywordsDao!!.findByGroupId(groupId)
        if (CollectionUtils.isEmpty(entityList)) {
            return empty()
        }

        val dto = GroupProductKeywordsFullDto()
        dto.groupIdNameDto = mapper!!.map(groupEntityDao!!.findById(groupId!!), GroupIdNameDto::class.java)
        dto.keyWords = entityList.stream()
                .map { it.keyWords }
                .map { str -> str!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
                .collect(Collectors.toList())
        return Optional.of(dto)
    }

    override fun removeAllKeywordForGroupId(groupId: Long?) {
        notNull(groupId, "groupId")

        groupProductKeywordsDao!!.findByGroupId(groupId)
                .forEach { entity -> groupProductKeywordsDao.delete(entity) }

        log.debug("all keywords for group id {}", groupId)
    }
}
