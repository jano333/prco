package sk.hudak.prco.manager.impl

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import sk.hudak.prco.dto.GroupIdNameDto
import sk.hudak.prco.dto.GroupProductKeywordsFullDto
import sk.hudak.prco.manager.GroupProductResolver
import sk.hudak.prco.service.GroupProductKeywordsService
import java.util.*
import java.util.stream.Collectors

@Component
class GroupProductResolverImpl(val groupProductKeywordsService: GroupProductKeywordsService)
    : GroupProductResolver {

    override fun resolveGroupId(productName: String): GroupIdNameDto? {
        // spritnem nazov produktu zo zoznamu slov(lower case)
        val productNameWords = Arrays.stream(StringUtils.split(productName, StringUtils.SPACE))
                .filter { StringUtils.isNotBlank(it) }
                .map { it.trim { it <= ' ' } }
                .map { it.toLowerCase() }
                .map { StringUtils.stripAccents(it) }
                .collect(Collectors.toSet())

        return groupProductKeywordsService.findAllGroupProductKeywords()
                .firstOrNull { resolve(it, productNameWords) }
                ?.groupIdNameDto
    }

    private fun resolve(fullDto: GroupProductKeywordsFullDto, productNameWords: Set<String>): Boolean {
        return fullDto.keyWords.stream()
                .filter { productNameWords.containsAll(it.asList()) }
                .findFirst()
                .isPresent
    }

//    override fun resolveGroup(productName: String): GroupProductKeywords? {
//        // spritnem nazov produktu zo zoznamu slov(lower case)
//        val productNameWords = Arrays.stream(StringUtils.split(productName, StringUtils.SPACE))
//                .filter { StringUtils.isNotBlank(it) }
//                .map { it.trim { it <= ' ' } }
//                .map { it.toLowerCase() }
//                .map { StringUtils.stripAccents(it) }
//                .collect(Collectors.toSet())
//
//
//        return GroupProductKeywords.values()
//                .filter { resolve(it, productNameWords) }
//                .firstOrNull()
//    }
//
//    @Deprecated("use another resolve")
//    private fun resolve(keyword: GroupProductKeywords, productNameWords: Set<String>): Boolean {
//        return keyword.choices.stream()
//                .filter { productNameWords.containsAll(it) }
//                .findFirst()
//                .isPresent
//    }
}
