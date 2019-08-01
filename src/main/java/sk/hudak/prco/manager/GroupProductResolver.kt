package sk.hudak.prco.manager

import sk.hudak.prco.api.GroupProductKeywords
import java.util.*

interface GroupProductResolver {

    fun resolveGroup(productName: String): Optional<GroupProductKeywords>
}
