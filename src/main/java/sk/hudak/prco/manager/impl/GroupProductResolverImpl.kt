package sk.hudak.prco.manager.impl

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import sk.hudak.prco.api.GroupProductKeywords
import sk.hudak.prco.manager.GroupProductResolver
import java.util.*
import java.util.stream.Collectors

@Component
class GroupProductResolverImpl : GroupProductResolver {

    override fun resolveGroup(productName: String): GroupProductKeywords? {
        // spritnem nazov produktu zo zoznamu slov(lower case)
        val productNameWords = Arrays.stream(StringUtils.split(productName, StringUtils.SPACE))
                .filter { StringUtils.isNotBlank(it) }
                .map { it.trim { it <= ' ' } }
                .map { it.toLowerCase() }
                .map { StringUtils.stripAccents(it) }
                .collect(Collectors.toSet())


        return GroupProductKeywords.values()
                .filter { resolve(it, productNameWords) }
                .firstOrNull()
    }

    private fun resolve(keyword: GroupProductKeywords, productNameWords: Set<String>): Boolean {
        return keyword.choices.stream()
                .filter { productNameWords.containsAll(it) }
                .findFirst()
                .isPresent
    }
}
