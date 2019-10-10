package sk.hudak.prco.service

import sk.hudak.prco.dto.SearchKeywordCreateDto
import sk.hudak.prco.dto.SearchKeywordListDto
import sk.hudak.prco.dto.SearchKeywordUdateDto

interface SearchKeywordService {

    fun createSearchKeyword(createDto: SearchKeywordCreateDto): Long
    fun updateSearchKeyword(updateDto: SearchKeywordUdateDto)
    fun findAllSearchKeyword(): List<SearchKeywordListDto>

    fun getSearchKeywordById(searchKeyWordId: Long): String
}