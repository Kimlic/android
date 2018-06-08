package com.kimlic.managers

import android.Manifest
import android.app.KeyguardManager
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class FingerprintService(activity: AppCompatActivity, onSuccess: () -> Unit, onFail: (error: String) -> Unit) {

    // Variables

    private val keyStore = KeyStore.getInstance(KEY_STORE_PROVIDER)
    private val keyGenerator = keyGenerator()
    private val fingerprintManager = fingerprintManager(activity)
    private val keyguardManager = keyguardManager(activity)
    private val cipher = buildCipher()
    private var secretKey: SecretKey? = null
    private var cryptoObject: FingerprintManager.CryptoObject? = null

    // Life

    init {
        if (!fingerprintManager.isHardwareDetected)
            onFail(Error.NOT_SUPPORTED.description())
        else
            if (!isPermissions(activity))
                onFail(Error.NO_PERMISSION.description())
            else
                if (!fingerprintManager.hasEnrolledFingerprints())
                    onFail(Error.NO_CONFIGURATION.description())
                else
                    if (!keyguardManager.isKeyguardSecure)
                        onFail(Error.NO_KEYGUARD.description())
                    else {
                        keyStore.load(null)
                        secretKey = generateKey()
                        cryptoObject = cipherInit()

                        FingerprintHandler(activity, onSuccess, onFail).startAuth(fingerprintManager, cryptoObject!!)
                    }
    }

    // Private

    private fun generateKey(): SecretKey {
        val builder = KeyGenParameterSpec.Builder(KEY_PAIR_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        keyGenerator.init(builder)

        return keyGenerator.generateKey()
    }

    private fun cipherInit(): FingerprintManager.CryptoObject {
        val key = keyStore.getKey(KEY_PAIR_NAME, null) as SecretKey
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return FingerprintManager.CryptoObject(cipher)
    }

    // Companion

    companion object {

        const val KEY_PAIR_NAME = "KimlicKeyPair"
        const val KEY_STORE_PROVIDER = "AndroidKeyStore"

        private fun keyGenerator(): KeyGenerator {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_PROVIDER)
        }

        private fun fingerprintManager(activity: AppCompatActivity): FingerprintManager {
            return activity.getSystemService(AppCompatActivity.FINGERPRINT_SERVICE) as FingerprintManager
        }

        private fun keyguardManager(activity: AppCompatActivity): KeyguardManager {
            return activity.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
        }

        private fun isPermissions(activity: AppCompatActivity): Boolean {
            return ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
        }

        private fun buildCipher(): Cipher {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        }
    }

    // FingerprintHandler

    inner class FingerprintHandler(private val appContext: AppCompatActivity, private val onSuccess: () -> Unit, private val onFail: (error: String) -> Unit) : FingerprintManager.AuthenticationCallback() {

        fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
            if (isPermissions(appContext))
                manager.authenticate(cryptoObject, CancellationSignal(), 0, this, null)
        }

        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
            onSuccess()
        }

        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
            onFail(errString.toString())
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {}

        override fun onAuthenticationFailed() {}
    }

    // Errors

    enum class Error {
        NOT_SUPPORTED {
            override fun description() = "Your device doesn't support fingerprint authentication"
        },
        NO_PERMISSION {
            override fun description() = "Please enable the fingerprint permission"
        },
        NO_CONFIGURATION {
            override fun description() = "No fingerprint configured. Please register at least one fingerprint in your device's Settings"
        },
        NO_KEYGUARD {
            override fun description() = "Please enable lockscreen security in your device's Settings"
        },
        AUTH_FAILED {
            override fun description() = "Authentication Failed"
        };

        abstract fun description(): String
    }
}