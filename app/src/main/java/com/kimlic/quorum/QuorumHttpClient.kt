package com.kimlic.quorum

import android.content.Context
import android.util.Log
import com.kimlic.R
import com.kimlic.TrustAllHostnameVerifier
import com.kimlic.utils.allopen.TestOpen
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import org.web3j.protocol.Service
import org.web3j.protocol.exceptions.ClientConnectionException
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

@TestOpen
class QuorumHttpClient(context: Context, private val url: String, private val httpClient: OkHttpClient, private val includeRawResponse: Boolean) : Service(includeRawResponse) {

    private val headers = HashMap<String, String>()
    private var mSocketFactory: SSLSocketFactory? = null

    //  private static SSLSocketFactory createSSLSocketFactory() {
    //    SSLSocketFactory ssfFactory = null;
    //    try {
    //      // 在这处理证书
    //      SSLContext sc = SSLContext.getInstance("TLS");
    //      sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
    //      ssfFactory = sc.getSocketFactory();
    //    } catch (Exception e) {
    //    }
    //    return ssfFactory;
    //  }

    private//return true; // contactVerify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
    val hostnameVerifier: HostnameVerifier
        get() = HostnameVerifier { hostname, session ->
            val hv = HttpsURLConnection.getDefaultHostnameVerifier()
            hv.verify("localhost", session)
        }

    // Init

    init {
        try {
            mSocketFactory = createSSLSocketFactory(context)
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

    }

    // Constructors

    constructor(context: Context, httpClient: OkHttpClient, includeRawResponses: Boolean) : this(context, DEFAULT_URL, httpClient, includeRawResponses)

    private constructor(context: Context, url: String, httpClient: OkHttpClient) : this(context, url, httpClient, false)

    @JvmOverloads
    constructor(context: Context, url: String = DEFAULT_URL) : this(context, url, createOkHttpClient(context))

    constructor(context: Context, url: String, includeRawResponse: Boolean) : this(context, url, createOkHttpClient(context), includeRawResponse)

    constructor(context: Context, httpClient: OkHttpClient) : this(context, DEFAULT_URL, httpClient)

    constructor(context: Context, includeRawResponse: Boolean) : this(context, DEFAULT_URL, includeRawResponse)

    companion object {
        val JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8")
        const val DEFAULT_URL = "http://localhost:8545/"

        private val log = LoggerFactory.getLogger(org.web3j.protocol.http.HttpService::class.java)

        private fun createOkHttpClient(context: Context): OkHttpClient {
            val builder = OkHttpClient.Builder()
            try {
                builder.sslSocketFactory(createSSLSocketFactory(context))
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }

            builder.hostnameVerifier(TrustAllHostnameVerifier())
            configureLogging(builder)
            return builder.build()
        }

        private fun configureLogging(builder: OkHttpClient.Builder) {
            if (log.isDebugEnabled) {
                val logging = HttpLoggingInterceptor(
                        HttpLoggingInterceptor.Logger { msg -> log.debug(msg) })
                logging.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(logging)
            }
        }

        private fun getWrappedTrustManagers(trustManagers: Array<TrustManager>): Array<TrustManager> {
            val originalTrustManager = trustManagers[0] as X509TrustManager
            return arrayOf(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return originalTrustManager.acceptedIssuers
                }

                override fun checkClientTrusted(certs: Array<X509Certificate>?, authType: String) {
                    try {
                        if (certs != null && certs.isNotEmpty()) {
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
                        if (certs != null && certs.isNotEmpty()) {
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

        @Throws(CertificateException::class, KeyStoreException::class, IOException::class, NoSuchAlgorithmException::class, KeyManagementException::class)
        private fun createSSLSocketFactory(context: Context): SSLSocketFactory {
            val cf = CertificateFactory.getInstance("X.509")
//          val caInput = context.resources.openRawResource(R.raw.kimlic_com) // this cert file stored in \app\src\main\res\raw folder path
            val caInput = getCaInput(context)

            val ca = cf.generateCertificate(caInput)
            caInput.close()

            val keyStore = KeyStore.getInstance("BKS")
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

        fun getCaInput(context: Context): InputStream {
            return context.resources.openRawResource(R.raw.kimlic_com) // this cert file stored in \app\src\main\res\raw folder path
        }
    }

    @Throws(IOException::class)
    override fun performIO(request: String): InputStream? {

        val requestBody = RequestBody.create(JSON_MEDIA_TYPE, request)
        val headers = buildHeaders()

        val httpRequest = okhttp3.Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build()

        val response = httpClient.newCall(httpRequest).execute()

        if (response.isSuccessful) {
            val responseBody = response.body()
            return if (responseBody != null) {
                buildInputStream(responseBody)
            } else {
                null
            }
        } else {
            throw ClientConnectionException(
                    "Invalid response received: " + response.body()!!)
        }
    }

    @Throws(IOException::class)
    private fun buildInputStream(responseBody: ResponseBody): InputStream {
        val inputStream = responseBody.byteStream()

        if (includeRawResponse) {
            // we have to buffer the entire input payload, so that after processing
            // it can be re-read and used to populate the rawResponse field.

            val source = responseBody.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body
            val buffer = source.buffer()

            val size = buffer.size()

            if (size > Integer.MAX_VALUE) throw UnsupportedOperationException("Non-integer input buffer size specified: $size")

            val bufferSize = size.toInt()
            val bufferedinputStream = BufferedInputStream(inputStream, bufferSize)

            bufferedinputStream.mark(inputStream.available())
            return bufferedinputStream

        } else {
            return inputStream
        }
    }

    private fun buildHeaders(): Headers {
        return Headers.of(headers)
    }

    fun addHeader(key: String, value: String) {
        headers[key] = value
    }

    fun addHeaders(headersToAdd: Map<String, String>) {
        headers.putAll(headersToAdd)
    }
}