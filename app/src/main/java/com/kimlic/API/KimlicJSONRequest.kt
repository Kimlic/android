package com.kimlic.API

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

open class KimlicJSONRequest(method: Int, url: String, private val requestHeaders: Map<String, String>, jsonRequest: JSONObject, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
    init {
        retryPolicy = DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return requestHeaders.toMutableMap()
    }
}