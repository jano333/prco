package sk.hudak.prco.dto

data class SearchKeywordCreateDto(val name: String) : DtoAble
data class SearchKeywordUdateDto(val id: Long, val name: String) : DtoAble
data class SearchKeywordListDto(val id: Long, val name: String) : DtoAble

