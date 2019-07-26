package sk.hudak.prco.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class PrcoSslManager {

        private static PrcoSslManager instance;

        private X509TrustManager prcoCustomTrustManager;
        private X509KeyManager prcoCustomKeyManager;
        private HostnameVerifier prcoCustomHostnameVerifier;

        private PrcoSslManager() {
            // no instance
        }

        public static PrcoSslManager getInstance() {
            if (PrcoSslManager.instance == null) {
                PrcoSslManager.instance = new PrcoSslManager();
            }
            return PrcoSslManager.instance;
        }

        public SSLContext init() throws RuntimeException {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");

                TrustManager[] trustManagers = new TrustManager[]{getJefCustomTrustManager()};
                KeyManager[] keymanagers = new KeyManager[]{getJefCustomKeyManager()};

                sslContext.init(keymanagers, trustManagers, null);

                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                HttpsURLConnection.setDefaultHostnameVerifier(getJefCustomHostnameVerifier());

                return sslContext;

            } catch (Exception e) {
                throw new RuntimeException("SSL initialization error.", e);
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

        private X509TrustManager getJefCustomTrustManager() throws Exception {
            if (prcoCustomTrustManager == null) {
                prcoCustomTrustManager = new PrcoCustomTrustManager();
            }
            return prcoCustomTrustManager;
        }

        private X509KeyManager getJefCustomKeyManager() throws Exception {
            if (prcoCustomKeyManager == null) {
                prcoCustomKeyManager = new PrcoCustomKeyManager();
            }
            return prcoCustomKeyManager;
        }

        private HostnameVerifier getJefCustomHostnameVerifier() {
            if (prcoCustomHostnameVerifier == null) {
                prcoCustomHostnameVerifier = new PrcoCustomHostnameVerifier();
            }
            return prcoCustomHostnameVerifier;
        }


        @Slf4j
private static class MockTrustManager implements X509TrustManager {


    public MockTrustManager() {
        log.warn("using mock trust manager");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        log.warn("checkClientTrusted()");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        log.warn("checkServerTrusted()");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        log.warn("getAcceptedIssuers()");
        return null;
    }
}

@Slf4j
private static class MockHostnameVerifier implements HostnameVerifier {



    public MockHostnameVerifier() {
        log.warn("using mock host name verifier");
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        log.warn("verify");
        return true;
    }
}

@Slf4j
@SuppressWarnings("unused")
private static class MockKeyManager implements X509KeyManager {


    public MockKeyManager() {
        log.warn("using mock key manager");
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        log.warn("chooseClientAlias");
        return null;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        log.warn("chooseServerAlias");
        return null;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        log.warn("getCertificateChain");
        return null;
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        log.warn("getClientAliases");
        return null;
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        log.warn("getPrivateKey");
        return null;
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        log.warn("getServerAliases");
        return null;
    }
}
}