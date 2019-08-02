package sk.hudak.prco.manager

import sk.hudak.prco.api.GroupProductKeywords

interface GroupProductResolver {

    fun resolveGroup(productName: String): GroupProductKeywords?
}
