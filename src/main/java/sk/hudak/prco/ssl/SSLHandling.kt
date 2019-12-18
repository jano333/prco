package sk.hudak.prco.ssl

import org.slf4j.LoggerFactory
import sk.hudak.prco.exception.PrcoRuntimeException
import java.net.Socket
import java.security.KeyStore
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

object PrcoSslManager {

    @JvmStatic
    fun init(): SSLContext {
        try {
            val sslContext = SSLContext.getInstance("SSL")

            val trustManagers = arrayOf<TrustManager>(PrcoCustomTrustManager())
            val keymanagers = arrayOf<KeyManager>(PrcoCustomKeyManager())

            sslContext.init(keymanagers, trustManagers, null)

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)

            HttpsURLConnection.setDefaultHostnameVerifier(PrcoCustomHostnameVerifier())

            return sslContext

        } catch (e: Exception) {
            throw PrcoRuntimeException("SSL initialization error.", e)
        }
    }


    //    /**
    //     * pozri:
    //     * http://cxf.apache.org/docs/client-http-transport-including-ssl-support
    //     * .html
    //     *
    //     * @param httpConduit
    //     * @throws RuntimeException
    //     */
    //    public void initCxf(HTTPConduit httpConduit) throws RuntimeException {
    //        logger.debug("inicializing SSL for CXF");
    //
    //        try {
    //            TLSClientParameters tlsParams = new TLSClientParameters();
    //            tlsParams.setDisableCNCheck(true);
    //
    //            TrustManager[] trustManagers = new TrustManager[]{getJefCustomTrustManager()};
    //            tlsParams.setTrustManagers(trustManagers);
    //
    //            KeyManager[] keymanagers = new KeyManager[]{getJefCustomKeyManager()};
    //            tlsParams.setKeyManagers(keymanagers);
    //
    //            httpConduit.setTlsClientParameters(tlsParams);
    //
    //        } catch (Exception e) {
    //            throw new RuntimeException("SSL for cxf inicialization error.", e);
    //        }
    //    }

    /*@Deprecated
    public void initMock() throws AmcBusinessException {
		logger.warn("using mock trust manage, key manager and host name verifier");
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			KeyManager[] keymanagers = new KeyManager[] { new TestCustomTomcatKeyManager() };
			TrustManager[] trustManagers = new TrustManager[] { new MockTrustManager() };
			sslContext.init(keymanagers, trustManagers, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new MockHostnameVerifier());
		} catch (Exception e) {
			throw new AmcBusinessException("SSL inicialization error.", e);
		}
	}*/

    private class MockTrustManager : X509TrustManager {

        companion object {
            val log = LoggerFactory.getLogger(MockTrustManager::class.java)
        }

        init {
            log.warn("using mock trust manager")
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            log.warn("checkClientTrusted()")
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            log.warn("checkServerTrusted()")
        }

        override fun getAcceptedIssuers(): Array<X509Certificate>? {
            log.warn("getAcceptedIssuers()")
            return null
        }
    }

    private class MockHostnameVerifier : HostnameVerifier {

        companion object {
            val log = LoggerFactory.getLogger(MockHostnameVerifier::class.java)
        }

        init {
            log.warn("using mock host name verifier")
        }

        override fun verify(hostname: String, session: SSLSession): Boolean {
            log.warn("verify")
            return true
        }
    }

    private class MockKeyManager : X509KeyManager {

        companion object {
            val log = LoggerFactory.getLogger(MockKeyManager::class.java)!!
        }

        init {
            log.warn("using mock key manager")
        }

        override fun chooseClientAlias(keyType: Array<String>, issuers: Array<Principal>, socket: Socket): String? {
            log.warn("chooseClientAlias")
            return null
        }

        override fun chooseServerAlias(keyType: String, issuers: Array<Principal>, socket: Socket): String? {
            log.warn("chooseServerAlias")
            return null
        }

        override fun getCertificateChain(alias: String): Array<X509Certificate>? {
            log.warn("getCertificateChain")
            return null
        }

        override fun getClientAliases(keyType: String, issuers: Array<Principal>): Array<String>? {
            log.warn("getClientAliases")
            return null
        }

        override fun getPrivateKey(alias: String): PrivateKey? {
            log.warn("getPrivateKey")
            return null
        }

        override fun getServerAliases(keyType: String, issuers: Array<Principal>): Array<String>? {
            log.warn("getServerAliases")
            return null
        }
    }


}

object PrcoSSLContants {

    // obsahuje zoznam povenych hostov, pre ktore sa nevaliduje
    @JvmField
    val ALLOWED_HOSTNAME: MutableSet<String> = HashSet(1)

    init {
        ALLOWED_HOSTNAME.add("hej.sk")

        ALLOWED_HOSTNAME.add("www.alza.cz")
        ALLOWED_HOSTNAME.add("www.alza.sk")

        ALLOWED_HOSTNAME.add("esodrogeria.eu")
        ALLOWED_HOSTNAME.add("premium-wask.cz")

        ALLOWED_HOSTNAME.add("pilulka-lb1.vshosting.cz")
        ALLOWED_HOSTNAME.add("pilulka.cz")
        ALLOWED_HOSTNAME.add("www.pilulka.sk")

        ALLOWED_HOSTNAME.add("www.pilulka24.sk")

        ALLOWED_HOSTNAME.add("www.feedo.sk")

        ALLOWED_HOSTNAME.add("obi.at")
        ALLOWED_HOSTNAME.add("www.obi.sk")

        ALLOWED_HOSTNAME.add("www.lekarna.cz")
        ALLOWED_HOSTNAME.add("www.mojalekaren.sk")

        ALLOWED_HOSTNAME.add("www.drmax.sk")

        ALLOWED_HOSTNAME.add("kidmarket.sk")
        ALLOWED_HOSTNAME.add("admin.asdata.sk")

        ALLOWED_HOSTNAME.add("www.brendon.sk")
        ALLOWED_HOSTNAME.add("brendon.hu")

        ALLOWED_HOSTNAME.add("www.4kids.sk")

        ALLOWED_HOSTNAME.add("mamaaja.sk")
        ALLOWED_HOSTNAME.add("orbi-02.webglobe.sk")

        ALLOWED_HOSTNAME.add("www.amddrogeria.sk")

        ALLOWED_HOSTNAME.add("www.drogeria-vmd.sk")
        ALLOWED_HOSTNAME.add("b2bexchange.vmd-drogerie.cz")

        ALLOWED_HOSTNAME.add("amy.onebit.cz")
        ALLOWED_HOSTNAME.add("dave.onebit.cz")
        ALLOWED_HOSTNAME.add("www.gigalekaren.sk")

        ALLOWED_HOSTNAME.add("www.prva-lekaren.sk")

        ALLOWED_HOSTNAME.add("www.lekaren-bella.sk")

        ALLOWED_HOSTNAME.add("www.lekarenvkocke.sk")

        ALLOWED_HOSTNAME.add("elbiahosting.sk")
        ALLOWED_HOSTNAME.add("www.magano.sk")
        ALLOWED_HOSTNAME.add("blueweb-3.vshosting.cz")

        ALLOWED_HOSTNAME.add("www.esodrogeria.eu")

        ALLOWED_HOSTNAME.add("drogerka.sk")

        ALLOWED_HOSTNAME.add("moonlake.cz")
        ALLOWED_HOSTNAME.add("www.lekarenexpres.sk")

        ALLOWED_HOSTNAME.add("perinbaba.sk")

        //FARBY
        ALLOWED_HOSTNAME.add("CN=*.*,O=WebSupport")
        ALLOWED_HOSTNAME.add("www.farby.sk")


    }
}// no instance

class PrcoCustomTrustManager @Throws(Exception::class)
constructor() : X509TrustManager {

    companion object {
        val log = LoggerFactory.getLogger(PrcoCustomTrustManager::class.java)!!
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
        log.trace("server cert subject name $name")
        // ak sa v subjecte certifikatu nachadza povoleny hostname, koncim
        // validaciu
        for (allowedHostName in PrcoSSLContants.ALLOWED_HOSTNAME) {
            if (name.contains(allowedHostName)) {
                log.trace("ignoring server cert")
                return
            }
        }
        log.trace("delegating checking to java default trust manager")
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

class PrcoCustomKeyManager @Throws(Exception::class)
constructor() : X509KeyManager {

    companion object {
        val log = LoggerFactory.getLogger(PrcoCustomKeyManager::class.java)
    }

    private val javaDefaultKeyManager: X509KeyManager

    init {
        log.debug("initializing")
        this.javaDefaultKeyManager = getJavaDefaultKeyManager()
    }

    @Throws(Exception::class)
    private fun getJavaDefaultKeyManager(): X509KeyManager {
        val kmg = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmg.init(null, null)
        // beriem prvy
        return kmg.keyManagers[0] as X509KeyManager
    }

    override fun chooseClientAlias(keyType: Array<String>, issuers: Array<Principal>, socket: Socket): String {
        return javaDefaultKeyManager.chooseClientAlias(keyType, issuers, socket)
    }

    override fun chooseServerAlias(keyType: String, issuers: Array<Principal>, socket: Socket): String {
        return javaDefaultKeyManager.chooseServerAlias(keyType, issuers, socket)
    }

    override fun getCertificateChain(alias: String): Array<X509Certificate> {
        return javaDefaultKeyManager.getCertificateChain(alias)
    }

    override fun getClientAliases(keyType: String, issuers: Array<Principal>): Array<String> {
        return javaDefaultKeyManager.getClientAliases(keyType, issuers)
    }

    override fun getPrivateKey(alias: String): PrivateKey {
        return javaDefaultKeyManager.getPrivateKey(alias)
    }

    override fun getServerAliases(keyType: String, issuers: Array<Principal>): Array<String> {
        return javaDefaultKeyManager.getServerAliases(keyType, issuers)
    }
}

class PrcoCustomHostnameVerifier : HostnameVerifier {

    companion object {
        val log = LoggerFactory.getLogger(PrcoCustomHostnameVerifier::class.java)!!
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