package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sk.hudak.prco.dao.db.SearchKeywordDao
import sk.hudak.prco.dto.SearchKeywordCreateDto
import sk.hudak.prco.dto.SearchKeywordListDto
import sk.hudak.prco.dto.SearchKeywordUdateDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.model.SearchKeywordEntity
import sk.hudak.prco.service.SearchKeywordService
import sk.hudak.prco.utils.Validate.notNullNotEmpty

@Service("searchKeywordService")
class SearchKeywordServiceImpl(private val searchKeywordDao: SearchKeywordDao)
    : SearchKeywordService {

    companion object {
        val log = LoggerFactory.getLogger(SearchKeywordServiceImpl::class.java)!!
    }

    override fun createSearchKeyword(createDto: SearchKeywordCreateDto): Long {
        notNullNotEmpty(createDto.name, "name")

        val entity = createDto.toEntity()
        val id = searchKeywordDao.save(entity)
        log.debug("create new entity {} with id {}", entity.javaClass.simpleName, id)
        return id!!
    }

    override fun updateSearchKeyword(updateDto: SearchKeywordUdateDto) {
        notNullNotEmpty(updateDto.name, "name")

        if (searchKeywordDao.existSearchKeywordByName(updateDto.name, updateDto.id)) {
            throw PrcoRuntimeException("Another " + SearchKeywordEntity::class.java.simpleName + " with name '" + updateDto.name + "' already exist")
        }

        val entity = searchKeywordDao.findById(updateDto.id)
        entity.name = updateDto.name
        searchKeywordDao.update(entity)

        log.debug("update entity {} with id {}", SearchKeywordEntity::class.java.simpleName, updateDto.id)
    }

    override fun findAllSearchKeyword(): List<SearchKeywordListDto> = searchKeywordDao.findAll()
            .map { it.toSearchKeywordListDto() }
            .toList()
}

fun SearchKeywordCreateDto.toEntity(): SearchKeywordEntity {
    val entity = SearchKeywordEntity()
    entity.name = this.name
    return entity
}

fun SearchKeywordEntity.toSearchKeywordListDto(): SearchKeywordListDto {
    return SearchKeywordListDto(this.id, this.name)
}


