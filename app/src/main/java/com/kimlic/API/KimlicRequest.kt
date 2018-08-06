package com.kimlic.API

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest

class KimlicRequest(method: Int, url: String, val requestHeaders: Map<String, String>?, val requestParams: Map<String, String>?,
                    onSuccess: Response.Listener<String>, onError: Response.ErrorListener)
    : StringRequest(method, url, onSuccess, onError) {

    init {
        setRetryPolicy(DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
    }

    // Life

    override fun getHeaders(): Map<String, String> = requestHeaders ?: emptyMap()

    override fun getParams(): Map<String, String> = requestParams ?: emptyMap()
}