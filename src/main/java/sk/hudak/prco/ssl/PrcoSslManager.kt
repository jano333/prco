package sk.hudak.prco.ssl

import org.slf4j.LoggerFactory
import sk.hudak.prco.exception.PrcoRuntimeException
import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


object PrcoSslManager {

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
            val log = LoggerFactory.getLogger(MockKeyManager::class.java)
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