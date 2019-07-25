package sk.hudak.prco.utils

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserAgentDataHolder {

    //TODO zmenit po konvertovani EhopUuid
    private val userAgents = EnumMap<EshopUuid, String>(EshopUuid::class.java)

    @PostConstruct
    fun init() {
        Arrays.stream(EshopUuid.values()).forEach { eshopUuid ->
            userAgents[eshopUuid] = UserAgentManager.getRandom()
        }
    }

    fun getUserAgentForEshop(eshopUuid: EshopUuid): String {
        return userAgents[eshopUuid]!!
    }

}
