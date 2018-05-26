package com.kimlic.auth

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.support.v4.app.ActivityCompat
import com.kimlic.BaseActivity
import java.io.IOException
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.security.cert.CertificateException

abstract class AuthActivity : BaseActivity() {

    // Variables

    private var authCallback: AuthCallback? = null

    // Life

    // Private

    fun setAuthCallback(authCallback: AuthCallback){
        this.authCallback = authCallback
    }
}