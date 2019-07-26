package sk.hudak.prco.ssl

import org.slf4j.LoggerFactory
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class PrcoCustomTrustManager @Throws(Exception::class)
constructor() : X509TrustManager {

    companion object {
        val log = LoggerFactory.getLogger(PrcoCustomTrustManager::class.java)
    }

    private val javaDefaultTrustManager: X509TrustManager
    private var serverCert: X509Certificate? = null

    init {
        log.debug("initializing")
        this.javaDefaultTrustManager = getJavaDefaultTrustManager() as X509TrustManager
    }

    @Throws(Exception::class)
    private fun getJavaDefaultTrustManager(): TrustManager {
        val tmg = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmg.init(null as KeyStore?)
        // beriem prvy
        return tmg.trustManagers[0]
    }

    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        log.debug("checkClientTrusted()")
        log.debug("delegating to java default trust manager")
        javaDefaultTrustManager.checkClientTrusted(chain, authType)
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        this.serverCert = chain[0]
        val name = serverCert!!.subjectX500Principal.name
        log.debug("server cert subject name $name")
        // ak sa v subjecte certifikatu nachadza povoleny hostname, koncim
        // validaciu
        for (allowedHostName in PrcoSSLContants.ALLOWED_HOSTNAME) {
            if (name.contains(allowedHostName)) {
                log.trace("ignoring server cert")
                return
            }
        }
        log.debug("delegating checking to java default trust manager")
        this.javaDefaultTrustManager.checkServerTrusted(chain, authType)
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        log.trace("getAcceptedIssuers()")
        val issuers = ArrayList<X509Certificate>()
        issuers.add(serverCert!!)
        Collections.addAll(issuers, *this.javaDefaultTrustManager.acceptedIssuers)
        return issuers.toTypedArray()
    }

}