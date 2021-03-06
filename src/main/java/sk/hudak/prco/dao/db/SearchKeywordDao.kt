package sk.hudak.prco.dao.db

import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.model.SearchKeywordEntity

interface SearchKeywordDao : BaseDao<SearchKeywordEntity> {

    fun findAll(): List<SearchKeywordEntity>

    fun existSearchKeywordByName(name: String, idToSkip: Long): Boolean
}