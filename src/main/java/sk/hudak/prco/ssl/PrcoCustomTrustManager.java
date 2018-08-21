package sk.hudak.prco.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PrcoCustomTrustManager implements X509TrustManager {

    private X509TrustManager javaDefaultTrustManager;
    private X509Certificate serverCert;

    public PrcoCustomTrustManager() throws Exception {
        log.debug("inicializing");
        this.javaDefaultTrustManager = (X509TrustManager) getJavaDefaultTrustManager();
    }

    private TrustManager getJavaDefaultTrustManager() throws Exception {
        TrustManagerFactory tmg = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmg.init((KeyStore) null);
        // beriem prvy
        return tmg.getTrustManagers()[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        log.debug("checkClientTrusted()");
        log.debug("delegating to java default trust manager");
        javaDefaultTrustManager.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        this.serverCert = chain[0];
        String name = serverCert.getSubjectX500Principal().getName();
        log.debug("server cert subject name {}", name);
        // ak sa v subjecte certifikatu nachadza povoleny hostname, koncim
        // validaciu
        for (String allowedHostName : PrcoSSLContants.ALLOWED_HOSTNAME) {
            if (name.contains(allowedHostName)) {
                log.debug("ignoring server cert");
                return;
            }
        }
        log.debug("delegating checking to java default trust manager");
        this.javaDefaultTrustManager.checkServerTrusted(chain, authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        log.debug("getAcceptedIssuers()");
        List<X509Certificate> issuers = new ArrayList<>();
        issuers.add(this.serverCert);
        for (X509Certificate javaDefaultIssuer : this.javaDefaultTrustManager.getAcceptedIssuers()) {
            issuers.add(javaDefaultIssuer);
        }
        return issuers.toArray(new X509Certificate[issuers.size()]);
    }

}