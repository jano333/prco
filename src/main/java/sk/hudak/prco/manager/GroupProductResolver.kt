package sk.hudak.prco.manager

import sk.hudak.prco.dto.GroupIdNameDto

interface GroupProductResolver {
    /**
     * Na zaklade nazvu produktu urcit do ktorej grupy patri
     */
    fun resolveGroupId(productName: String): GroupIdNameDto?

//    @Deprecated("use upper method")
//    fun resolveGroup(productName: String): GroupProductKeywords?
}
