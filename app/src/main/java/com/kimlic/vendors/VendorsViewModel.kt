package com.kimlic.vendors

import android.arch.lifecycle.*
import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.model.SingleLiveEvent
import com.kimlic.preferences.Prefs
import org.json.JSONObject

class VendorsViewModel : ViewModel(), LifecycleObserver {

    // Variables

    private val vendorsRepository = VendorsRepository.instance
    private var documentsLiveData = object : MutableLiveData<Vendors>() {}
    private var progressLiveData: SingleLiveEvent<Boolean> = object : SingleLiveEvent<Boolean>() {}
    
    // public


    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    fun getDocumentslist() {

        vendorsRepository.getDocuments(accountAddress = Prefs.currentAccountAddress,
                responseListner = object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        val responceCode = JSONObject(response).getJSONObject("meta").optString("code").toString()

                        if (!responceCode.startsWith("2")) return

                        val dataBody = JSONObject(response).getJSONObject("data").toString()
                        val type = object : TypeToken<Vendors>() {}.type
                        val responseObject: Vendors = Gson().fromJson(dataBody, type)
                        documentsLiveData.postValue(responseObject)
                        // progressLiveData.postValue(false)
                    }
                },
                errorListner = object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {}
                })
    }

    fun getVendors() = documentsLiveData

    fun progress() = progressLiveData

}