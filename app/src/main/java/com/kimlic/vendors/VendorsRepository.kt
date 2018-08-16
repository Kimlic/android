package com.kimlic.vendors

import android.os.Handler
import android.util.Log
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService
import com.kimlic.db.entity.VendorDocument
import com.kimlic.preferences.Prefs
import com.kimlic.utils.QuorumURL
import com.kimlic.utils.mappers.JsonToVenDocMapper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class VendorsRepository private constructor() {

    private object HOLDER {
        val INSTANSE = VendorsRepository()
    }

    // Companion

    companion object {
        val instance: VendorsRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { HOLDER.INSTANSE }
    }

    private var googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())
    private var db: KimlicDB = KimlicDB.getInstance()!!
    private var vendorDao = db.vendorDao()

    fun initDocuments(accountAddress: String) {
        val headers = mapOf(Pair("account-address", accountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"))

        val vendorsRequest = KimlicRequest(GET, QuorumURL.vendors.url, headers, emptyMap(),
                Response.Listener { response ->
                    val responseCode = JSONObject(response).getJSONObject("meta").optString("code").toString()
                    if (!responseCode.startsWith("2")) return@Listener

                    val data = JSONObject(response).getJSONObject("data").toString()
                    val type = object : TypeToken<Vendors>() {}.type

                    val responseObject: Vendors = Gson().fromJson(data, type)
                    val entityList: MutableList<VendorDocument> = mutableListOf()

                    responseObject.documents.forEach { entityList.add(JsonToVenDocMapper().transform(it)) }
                    vendorDao.insertDocs(entityList.toList())
                    syncDataBase()
                    //progressLiveData.postValue(false)
                },
                Response.ErrorListener {
                    Log.d("VENDOR", "Vendor error" + it)
                }
        )

        VolleySingleton.getInstance(KimlicApp.applicationContext()).requestQueue.add(vendorsRequest)
    }

    fun countries(): List<Country> {
        val countries = mutableListOf<Country>()
        var hasNextLine = true

        try {
            val reader = BufferedReader(InputStreamReader(KimlicApp.applicationContext().assets.open("countries.dat")))
            while (hasNextLine) {
                val line: String? = reader.readLine()
                line?.let {
                    val v = it.split(",")
                    val country = Country(v[0], v[1], v[2].toInt())
                    countries.add(country)
                }
                hasNextLine = line != null
            }
        } catch (e: IOException) {
            throw Exception("File not found")
        }
        return countries
    }

    fun vendorDocumentsLive() = vendorDao.selectLive()

    fun vendorDocuments() = vendorDao.select()


    private fun syncDataBase() {
        googleSignInAccount?.let {
            Handler().postDelayed({ SyncService.getInstance().backupDatabase(Prefs.currentAccountAddress, "kimlic.db", onSuccess = {}) }, 0)
        }
    }

    class Country(val country: String, val sh: String, val code: Int)
}


