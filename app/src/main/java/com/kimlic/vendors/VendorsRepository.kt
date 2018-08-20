package com.kimlic.vendors

import android.os.Handler
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimlic.API.DoAsync
import com.kimlic.API.KimlicJSONRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.BuildConfig
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

    // Constants

    private val VENDORS_URL = BuildConfig.VENDORS_URL

    // Variables

    private var googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())
    private var db: KimlicDB = KimlicDB.getInstance()!!
    private var vendorDao = db.vendorDao()


    // Public

    fun initDocuments(accountAddress: String, onError: () -> Unit) {
        val url = VENDORS_URL + QuorumURL.VENDORS.url
        val headers = mapOf(Pair("account-address", accountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"))

        val vendorsRequest = KimlicJSONRequest(GET, url, headers, JSONObject(),
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

    // Private

    private fun syncDataBase() {
        googleSignInAccount?.let {
            Handler().postDelayed({ SyncService.getInstance().backupDatabase(Prefs.currentAccountAddress, "kimlic.db", onSuccess = {}) }, 0)
        }
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