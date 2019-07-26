package sk.hudak.prco.ssl

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

@Slf4j
class PrcoCustomHostnameVerifier : HostnameVerifier {

    companion object {
        val log = LoggerFactory.getLogger(PrcoCustomHostnameVerifier::class.java)
    }

    private val javaDefaultHostnameVerifier: HostnameVerifier

    /**
     * Povoluje prechod pre [PrcoSSLContants.ALLOWED_HOSTNAME]. Pre ostatne
     * deleguje volanie do java default host name verifikatora.
     */
    init {
        log.debug("initializing")
        this.javaDefaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
    }

    override fun verify(hostname: String, session: SSLSession): Boolean {
        log.debug("verify $hostname")
        if (PrcoSSLContants.ALLOWED_HOSTNAME.contains(hostname)) {
            return true
        }
        log.debug("delegating verifying to java default host name")
        return javaDefaultHostnameVerifier.verify(hostname, session)
    }

}
