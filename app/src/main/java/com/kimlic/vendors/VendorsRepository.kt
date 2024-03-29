package com.kimlic.vendors

import android.os.Handler
import android.util.Log
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.DoAsync
import com.kimlic.API.KimlicApi
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService
import com.kimlic.db.entity.VendorDocument
import com.kimlic.preferences.Prefs
import com.kimlic.utils.mappers.JsonToVenDocMapper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class VendorsRepository private constructor() {

    // Variables

    private var googleSignInAccount: GoogleSignInAccount? = null
    private var db: KimlicDB = KimlicDB.getInstance()!!
    private var vendorDao = db.vendorDao()

    // Public
    // Previous version - work now
    fun initDocumentsRequest(accountAddress: String, url: String, onError: () -> Unit) {
        val headers = mapOf(Pair("account-address", accountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"))

        val vendorsRequest = KimlicJSONRequest(GET, url + KimlicApi.VENDORS.path, headers, JSONObject(),
                Response.Listener { it ->
                    if (!it.getJSONObject("meta").optString("code").toString().startsWith("2")) {
                        onError()
                        return@Listener
                    }

                    val type = object : TypeToken<Vendors>() {}.type
                    val data = it.getJSONObject("data").toString()

                    val responseObject: Vendors = Gson().fromJson(data, type)
                    val entityList: MutableList<VendorDocument> = mutableListOf()

                    responseObject.documents.forEach { entityList.add(JsonToVenDocMapper().transform(it)) }
                    vendorDao.insertDocs(entityList.toList())
                    syncDataBase()
                },
                Response.ErrorListener { _ ->
                    onError()
                })

        DoAsync().execute(Runnable { VolleySingleton.getInstance(KimlicApp.applicationContext()).requestQueue.add(vendorsRequest) })
    }

    // New request to Vendors - saves RP docs to database
    fun rpDocumentsRequest(accountAddress: String, url: String, onError: () -> Unit) {
        val headers = mapOf(Pair("account-address", accountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"))
        val vendorsRequest = KimlicJSONRequest(GET, url + KimlicApi.VENDORS.path, headers, JSONObject(),
                Response.Listener { it ->
                    if (!it.getJSONObject("meta").optString("code").toString().startsWith("2")) {
                        onError()
                        return@Listener
                    }
                    Log.d("TAGRPREQUEST", "request")
                    val type = object : TypeToken<Vendors>() {}.type
                    val data = it.getJSONObject("data").toString()

                    val responseObject: Vendors = Gson().fromJson(data, type)
                    val entityList: MutableList<VendorDocument> = mutableListOf()

                    responseObject.documents.forEach { entityList.add(JsonToVenDocMapper().transform(it)) }
                    vendorDao.insertDocs(entityList.toList())
                    //syncDataBase()
                },
                Response.ErrorListener { _ ->
                    onError()
                })

        DoAsync().execute(Runnable { VolleySingleton.getInstance(KimlicApp.applicationContext()).requestQueue.add(vendorsRequest) })

    }

    // Clear all vendors from db on start of activity
    fun clearVendorsDocs() {
        Log.d("TAGVENDOR", "in remover vendors info from db")
        vendorDao.deleteAll()
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

    // Is going to be user for
    fun vendorDocumentsLive() = vendorDao.selectLive()

    fun vendorDocuments() = vendorDao.select()

    // Private

    private fun syncDataBase() {
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())
        if (googleSignInAccount != null && Prefs.isDriveActive)
            Handler().postDelayed({ SyncService.getInstance().backupDatabase(Prefs.currentAccountAddress, "kimlic.db", onSuccess = {}, onError = {}) }, 0)

    }

    // Holder

    private object HOLDER {
        val INSTANCE = VendorsRepository()
    }

    // Companion

    companion object {
        val instance: VendorsRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { HOLDER.INSTANCE }
    }

    // Inner class

    class Country(val country: String, val sh: String, val code: Int)
}