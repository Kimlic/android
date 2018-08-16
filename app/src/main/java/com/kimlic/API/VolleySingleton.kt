package com.kimlic.API

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import com.kimlic.R
//import org.spongycastle.jcajce.provider.asymmetric.x509.CertificateFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.security.cert.CertificateException


class VolleySingleton(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: VolleySingleton(context)
        }
    }

    init {

    }

    var hurlStack: HurlStack = object : HurlStack() {
        @Throws(IOException::class)
        override fun createConnection(url: URL): HttpURLConnection {
            val httpsURLConnection = super.createConnection(url)
            try {
                (httpsURLConnection as HttpsURLConnection).sslSocketFactory = getSSLSocketFactory(context)
                (httpsURLConnection as HttpsURLConnection).hostnameVerifier = getHostnameVerifier()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return httpsURLConnection
        }
    }

    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { hostname, session ->
//            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier()
//            return hv.verify("localhost", session)
            true
        }
    }

//    fun getSSLSocketFactory(): SSLSocketFactory {
//        try {
//            val sslContext = SSLContext.getInstance("SSL")
//            sslContext.init(null, getTrustManager(), SecureRandom())
//            return sslContext.socketFactory
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//    }
//
//    private fun getTrustManager(): Array<TrustManager> {
//        return arrayOf(object : X509TrustManager {
//            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
//            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
//            override fun getAcceptedIssuers(): Array<X509Certificate> {
//                return arrayOf()
//            }
//        })
//    }

    @Throws(CertificateException::class, KeyStoreException::class, IOException::class, NoSuchAlgorithmException::class, KeyManagementException::class)
    private fun getSSLSocketFactory(context: Context): SSLSocketFactory {
        val cf = CertificateFactory.getInstance("X.509")
        val caInput = context.resources.openRawResource(R.raw.kimlic_com) // File path: app\src\main\res\raw\your_cert.cer
        val ca = cf.generateCertificate(caInput)
        caInput.close()

        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        val wrappedTrustManagers = getWrappedTrustManagers(tmf.trustManagers)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, wrappedTrustManagers, null)

        return sslContext.socketFactory
    }

    private fun getWrappedTrustManagers(trustManagers: Array<TrustManager>): Array<TrustManager> {
        val originalTrustManager = trustManagers[0] as X509TrustManager
        return arrayOf(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return originalTrustManager.acceptedIssuers
            }

            override fun checkClientTrusted(certs: Array<X509Certificate>?, authType: String) {
                try {
                    if (certs != null && certs.size > 0) {
                        certs[0].checkValidity()
                    } else {
                        originalTrustManager.checkClientTrusted(certs, authType)
                    }
                } catch (e: CertificateException) {
                    Log.w("checkClientTrusted", e.toString())
                }

            }

            override fun checkServerTrusted(certs: Array<X509Certificate>?, authType: String) {
                try {
                    if (certs != null && certs.size > 0) {
                        certs[0].checkValidity()
                    } else {
                        originalTrustManager.checkServerTrusted(certs, authType)
                    }
                } catch (e: CertificateException) {
                    Log.w("checkServerTrusted", e.toString())
                }

            }
        })
    }


    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext, hurlStack)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}