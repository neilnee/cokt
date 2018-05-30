package com.cokt.secure

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import com.cokt.tool.CoktLog
import java.security.InvalidKeyException
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

class FingerprintAuth {

    companion object {
        private const val PROVIDER = "AndroidKeyStore"
    }

    private var callback: ((result: FingerprintManagerCompat.AuthenticationResult?) -> Unit)? = null
    private val signal = CancellationSignal()
    private var keystore: KeyStore? = null

    fun request(context: Context, name: String,
                c: ((result: FingerprintManagerCompat.AuthenticationResult?) -> Unit)?): Boolean {
        keystore = KeyStore.getInstance(PROVIDER)
        keystore?.load(null)
        callback = c
        val manager =  FingerprintManagerCompat.from(context)
        if (manager.isHardwareDetected && manager.hasEnrolledFingerprints()) {
            CoktLog.debug("start fingerprint auth")
            manager.authenticate(
                    cryptoObject(name),
                    0,
                    signal,
                    FingerprintCallback(),
                    null)
            return true
        }
        return false
    }

    fun cancel() {
        callback = null
        signal.cancel()
    }

    private fun cryptoObject(name: String): FingerprintManagerCompat.CryptoObject? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return null

        fun key(): Key? {
            if (keystore?.isKeyEntry(name) == true) {
                CoktLog.debug("fun cryptoObject generateKey")
                val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
                val keyGenSpec = KeyGenParameterSpec
                        .Builder(name, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setUserAuthenticationRequired(true)
                        .build()
                keyGen.init(keyGenSpec)
                keyGen.generateKey()
            }
            return keystore?.getKey(name, null)
        }

        fun cipher(retry: Boolean): Cipher? {
            val k = key() ?: return null
            val c = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/" +
                    "${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
            try {
                c.init(Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE, k)
            } catch (e: InvalidKeyException) {
                keystore?.deleteEntry(name)
                if (retry) {
                    cipher(false)
                } else {
                    return null
                }
            }
            return c
        }

        val c = cipher(true)
        CoktLog.debug("fun cryptoObject [$c]")
        return when {
            c != null -> FingerprintManagerCompat.CryptoObject(c)
            else -> null
        }
    }

    inner class FingerprintCallback : FingerprintManagerCompat.AuthenticationCallback() {
        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
            CoktLog.error("onAuthenticationError [$errMsgId][$errString]")
            if (errMsgId == 5) return
            callback?.invoke(null)
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            CoktLog.debug("onAuthenticationSucceeded [$result]")
            signal.setOnCancelListener(null)
            callback?.invoke(result)
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
            CoktLog.debug("onAuthenticationHelp [$helpMsgId][$helpString]")
        }

        override fun onAuthenticationFailed() {
            CoktLog.error("onAuthenticationFailed")
            callback?.invoke(null)
        }
    }

}