package sk.hudak.prco.ssl

import org.slf4j.LoggerFactory
import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.X509KeyManager

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
