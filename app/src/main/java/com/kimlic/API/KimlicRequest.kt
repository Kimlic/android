package com.kimlic.API

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class KimlicRequest(method: Int, url: String, onSuccess: Response.Listener<String>, onError: Response.ErrorListener) : StringRequest(method, url, onSuccess, onError) {

    private var requestParasms: MutableMap<String, String> = emptyMap<String, String>().toMutableMap()
    private var headers: MutableMap<String, String> = emptyMap<String, String>().toMutableMap()

    // Life


    override fun getHeaders(): MutableMap<String, String> {
        return headers
    }

    // Public


    override fun getParams(): MutableMap<String, String> {

        return requestParasms
    }

    fun setParams(params: MutableMap<String, String>) {
        this.requestParasms = params
    }

    fun setHeaders(headersEmail: MutableMap<String, String>) {
        this.headers = headersEmail
    }

}