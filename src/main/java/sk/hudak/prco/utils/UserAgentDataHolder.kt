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
            userAgents[eshopUuid] = UserAgentManager.random
        }
    }

    fun getUserAgentForEshop(eshopUuid: EshopUuid): String {
        return userAgents[eshopUuid]!!
    }

}

object UserAgentManager {

    val FIREFOX_1 = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1"
    val FIREFOX_2 = "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0"
    val FIREFOX_3 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0"

    val CHROME_1 = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
    val CHROME_2 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36"
    val CHROME_3 = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36"

    val IE_1 = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko"
    val IE_2 = "Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko"
    val IE_3 = "Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0"


    private val UA_S = arrayOf(FIREFOX_1, FIREFOX_2, FIREFOX_3, CHROME_1, CHROME_2, CHROME_3, IE_1, IE_2, IE_3)

    val random: String
        //random.nextInt(max - min + 1) + min
        get() = UA_S[Random().nextInt(8)]

}
