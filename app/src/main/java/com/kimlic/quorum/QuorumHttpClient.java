package com.kimlic.quorum;

import android.content.Context;
import android.util.Log;

import com.kimlic.R;
import com.kimlic.TrustAllCerts;
import com.kimlic.TrustAllHostnameVerifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Service;
import org.web3j.protocol.exceptions.ClientConnectionException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;

public class QuorumHttpClient extends Service {

  public static final MediaType JSON_MEDIA_TYPE
      = MediaType.parse("application/json; charset=utf-8");

  public static final String DEFAULT_URL = "http://localhost:8545/";

  private static final Logger log = LoggerFactory.getLogger(org.web3j.protocol.http.HttpService.class);

  private OkHttpClient httpClient;

  private final String url;

  private final boolean includeRawResponse;

  private HashMap<String, String> headers = new HashMap<String, String>();
  private SSLSocketFactory mSocketFactory;

  public QuorumHttpClient(Context context, String url, OkHttpClient httpClient, boolean includeRawResponses) {
    super(includeRawResponses);
    this.url = url;
    this.httpClient = httpClient;
    this.includeRawResponse = includeRawResponses;
    try {
      mSocketFactory = createSSLSocketFactory(context);
    } catch (CertificateException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
  }

  public QuorumHttpClient(Context context, OkHttpClient httpClient, boolean includeRawResponses) {
    this(context, DEFAULT_URL, httpClient, includeRawResponses);
  }

  private QuorumHttpClient(Context context, String url, OkHttpClient httpClient) {
    this(context, url, httpClient, false);
  }

  public QuorumHttpClient(Context context, String url) {
    this(context, url, createOkHttpClient(context));
  }

  public QuorumHttpClient(Context context, String url, boolean includeRawResponse) {
    this(context, url, createOkHttpClient(context), includeRawResponse);
  }

  public QuorumHttpClient(Context context, OkHttpClient httpClient) {
    this(context, DEFAULT_URL, httpClient);
  }

  public  QuorumHttpClient(Context context, boolean includeRawResponse) {
    this(context, DEFAULT_URL, includeRawResponse);
  }

  public QuorumHttpClient(Context context) {
    this(context, DEFAULT_URL);
  }

  private static OkHttpClient createOkHttpClient(Context context) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    try {
      builder.sslSocketFactory(createSSLSocketFactory(context));
    } catch (CertificateException e) {
      e.printStackTrace();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
    builder.hostnameVerifier(new TrustAllHostnameVerifier());
    configureLogging(builder);
    return builder.build();
  }

  private static void configureLogging(OkHttpClient.Builder builder) {
    if (log.isDebugEnabled()) {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
          new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String msg) {
              log.debug(msg);
            }
          });
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(logging);
    }
  }

  @Override
  protected InputStream performIO(String request) throws IOException {

    RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, request);
    Headers headers = buildHeaders();

    okhttp3.Request httpRequest = new okhttp3.Request.Builder()
        .url(url)
        .headers(headers)
        .post(requestBody)
        .build();

    okhttp3.Response response = httpClient.newCall(httpRequest).execute();
    if (response.isSuccessful()) {
      ResponseBody responseBody = response.body();
      if (responseBody != null) {
        return buildInputStream(responseBody);
      } else {
        return null;
      }
    } else {
      throw new ClientConnectionException(
          "Invalid response received: " + response.body());
    }
  }

  private InputStream buildInputStream(ResponseBody responseBody) throws IOException {
    InputStream inputStream = responseBody.byteStream();

    if (includeRawResponse) {
      // we have to buffer the entire input payload, so that after processing
      // it can be re-read and used to populate the rawResponse field.

      BufferedSource source = responseBody.source();
      source.request(Long.MAX_VALUE); // Buffer the entire body
      Buffer buffer = source.buffer();

      long size = buffer.size();
      if (size > Integer.MAX_VALUE) {
        throw new UnsupportedOperationException(
            "Non-integer input buffer size specified: " + size);
      }

      int bufferSize = (int) size;
      BufferedInputStream bufferedinputStream =
          new BufferedInputStream(inputStream, bufferSize);

      bufferedinputStream.mark(inputStream.available());
      return bufferedinputStream;

    } else {
      return inputStream;
    }
  }

  private Headers buildHeaders() {
    return Headers.of(headers);
  }

  public void addHeader(String key, String value) {
    headers.put(key, value);
  }

  public void addHeaders(Map<String, String> headersToAdd) {
    headers.putAll(headersToAdd);
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

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

  private HostnameVerifier getHostnameVerifier() {
    return new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
        return hv.verify("localhost", session);
      }
    };
  }

  private static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
    final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
    return new TrustManager[]{
        new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return originalTrustManager.getAcceptedIssuers();
          }

          public void checkClientTrusted(X509Certificate[] certs, String authType) {
            try {
              if (certs != null && certs.length > 0){
                certs[0].checkValidity();
              } else {
                originalTrustManager.checkClientTrusted(certs, authType);
              }
            } catch (CertificateException e) {
              Log.w("checkClientTrusted", e.toString());
            }
          }

          public void checkServerTrusted(X509Certificate[] certs, String authType) {
            try {
              if (certs != null && certs.length > 0){
                certs[0].checkValidity();
              } else {
                originalTrustManager.checkServerTrusted(certs, authType);
              }
            } catch (CertificateException e) {
              Log.w("checkServerTrusted", e.toString());
            }
          }
        }
    };
  }

  private static SSLSocketFactory createSSLSocketFactory(Context context)
      throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    InputStream caInput = context.getResources().openRawResource(R.raw.kimlic_com); // this cert file stored in \app\src\main\res\raw folder path

    Certificate ca = cf.generateCertificate(caInput);
    caInput.close();

    KeyStore keyStore = KeyStore.getInstance("BKS");
    keyStore.load(null, null);
    keyStore.setCertificateEntry("ca", ca);

    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
    tmf.init(keyStore);

    TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, wrappedTrustManagers, null);

    return sslContext.getSocketFactory();
  }
}