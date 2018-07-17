package sk.hudak.prco.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Slf4j
public class PrcoCustomKeyManager implements X509KeyManager {


    private X509KeyManager javaDefaultKeyManager;

    public PrcoCustomKeyManager() throws Exception {
        log.debug("inicializing");
        this.javaDefaultKeyManager = (X509KeyManager) getJavaDefaultKeyManager();
    }

    private static KeyManager getJavaDefaultKeyManager() throws Exception {
        KeyManagerFactory kmg = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmg.init(null, null);
        // beriem prvy
        return kmg.getKeyManagers()[0];
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return javaDefaultKeyManager.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return javaDefaultKeyManager.chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return javaDefaultKeyManager.getCertificateChain(alias);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return javaDefaultKeyManager.getClientAliases(keyType, issuers);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return javaDefaultKeyManager.getPrivateKey(alias);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return javaDefaultKeyManager.getServerAliases(keyType, issuers);
    }
}
