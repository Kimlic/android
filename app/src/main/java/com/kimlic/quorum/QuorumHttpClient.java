package com.kimlic.quorum;

import com.kimlic.TrustAllCerts;
import com.kimlic.TrustAllHostnameVerifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Service;
import org.web3j.protocol.exceptions.ClientConnectionException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

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

  public QuorumHttpClient(String url, OkHttpClient httpClient, boolean includeRawResponses) {
    super(includeRawResponses);
    this.url = url;
    this.httpClient = httpClient;
    this.includeRawResponse = includeRawResponses;
  }

  public QuorumHttpClient(OkHttpClient httpClient, boolean includeRawResponses) {
    this(DEFAULT_URL, httpClient, includeRawResponses);
  }

  private QuorumHttpClient(String url, OkHttpClient httpClient) {
    this(url, httpClient, false);
  }

  public QuorumHttpClient(String url) {
    this(url, createOkHttpClient());
  }

  public QuorumHttpClient(String url, boolean includeRawResponse) {
    this(url, createOkHttpClient(), includeRawResponse);
  }

  public QuorumHttpClient(OkHttpClient httpClient) {
    this(DEFAULT_URL, httpClient);
  }

  public  QuorumHttpClient(boolean includeRawResponse) {
    this(DEFAULT_URL, includeRawResponse);
  }

  public QuorumHttpClient() {
    this(DEFAULT_URL);
  }

  private static OkHttpClient createOkHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.sslSocketFactory(createSSLSocketFactory());
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

  private static SSLSocketFactory createSSLSocketFactory() {
    SSLSocketFactory ssfFactory = null;
    try {
      // 在这处理证书
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
      ssfFactory = sc.getSocketFactory();
    } catch (Exception e) {
    }
    return ssfFactory;
  }
}