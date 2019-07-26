package sk.hudak.prco.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

@Slf4j
public class PrcoCustomHostnameVerifier implements HostnameVerifier {

    private HostnameVerifier javaDefaultHostnameVerifier;

    /**
     * Povoluje prechod pre {@link PrcoSSLContants#ALLOWED_HOSTNAME}. Pre ostatne
     * deleguje volanie do java default host name verifikatora.
     */
    public PrcoCustomHostnameVerifier() {
        log.debug("inicializing");
        this.javaDefaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        log.debug("verify {}", hostname);
        if (PrcoSSLContants.ALLOWED_HOSTNAME.contains(hostname)) {
            return true;
        }
        log.debug("delegating verifying to java default host name");
        return javaDefaultHostnameVerifier.verify(hostname, session);
    }

}
