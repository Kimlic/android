package com.kimlic.API

import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


open class KimlicJSONRequest(method: Int, url: String, private val requestHeaders: Map<String, String>, jsonRequest: JSONObject, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
    init {
        retryPolicy = DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return requestHeaders.toMutableMap()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        try {
            //String jsonString = new String(response.data,HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            val jsonString = String(response.data)
            val jsonResponse = JSONObject(jsonString)
            val map = mapOf(Pair("statusCode", response.statusCode.toString()))
//            jsonResponse.put("headers", JSONObject(response.headers))
            jsonResponse.put("headers", JSONObject(map))
            return Response.success(jsonResponse,
                    HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        } catch (je: JSONException) {
            return Response.error(ParseError(je))
        }

    }
}