package com.kimlic.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.KimlicApi
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.db.KimlicDB
import com.kimlic.db.dao.CompanyDao
import com.kimlic.db.entity.Company
import com.kimlic.preferences.Prefs
import com.kimlic.service.entity.CompanyVerifyEntity
import com.kimlic.utils.AppConstants
import org.json.JSONObject
import java.util.*

class CompanyDetailsSyncService : IntentService("DetailsSyncService") {

    // Variables

    private var dataBase: KimlicDB? = null
    private lateinit var companyDao: CompanyDao
    private lateinit var unverifiedList: List<Company>
    private lateinit var unverifiedQueue: ArrayDeque<Company>
    // Live

    override fun onCreate() {
        super.onCreate()

        dataBase = KimlicDB.getInstance()
        companyDao = dataBase!!.companyDao()
        unverifiedQueue = ArrayDeque()
    }

    override fun onHandleIntent(intent: Intent?) {
        unverifiedList = companyDao.companyByStatus(Prefs.currentAccountAddress, AppConstants.UNVERIFIED.key)
        unverifiedQueue.addAll(unverifiedList)
        Log.d("TAGSERVICE", "unverified list ${unverifiedList}")
        recursiveRequest()
    }

    // Private

    private fun recursiveRequest() {
        unverifiedQueue.poll()?.let {
            val headers = mapOf(Pair("account-address", Prefs.currentAccountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"))

            val request = KimlicJSONRequest(GET, it.website + KimlicApi.DOCUMENTS_VERIFIED.path, headers, JSONObject(),
                    Response.Listener {
                        // TODO parce responce, update database!!!
                        val type = object : TypeToken<CompanyVerifyEntity>() {}.type
                        val documentDataString = it.getJSONObject("data").getJSONArray("document").toString()

                        val verifiedDocumentInfo: CompanyVerifyEntity = Gson().fromJson(documentDataString, type)
                        Log.d("TAGSERVICE", "company verify entity - $it")
                    },
                    Response.ErrorListener {
                        Log.d("TAGSERVICE", "sync responce - $it")
                    })

            VolleySingleton.getInstance(this).requestQueue.add(request)
        } ?: stopSelf()
    }
}
