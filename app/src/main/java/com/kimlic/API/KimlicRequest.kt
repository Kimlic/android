package com.kimlic.API

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class KimlicRequest(method: Int, url: String, onSuccess: Response.Listener<String>, onError: Response.ErrorListener) : StringRequest(method, url, onSuccess, onError) {

    // Variables

    var requestParasms: MutableMap<String, String> = emptyMap<String, String>().toMutableMap(); set
    var requestHeaders: MutableMap<String, String> = emptyMap<String, String>().toMutableMap(); set

    // Life

    override fun getHeaders(): MutableMap<String, String> = requestHeaders

    override fun getParams(): MutableMap<String, String>  = requestParasms



}