package com.kimlic.vendors

import android.os.Handler
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
import com.kimlic.db.entity.Company
import com.kimlic.db.entity.VendorDocument
import com.kimlic.preferences.Prefs
import com.kimlic.utils.mappers.CompanyMapper
import com.kimlic.utils.mappers.JsonToVenDocMapper
import com.kimlic.vendors.entity.Company_
import com.kimlic.vendors.entity.Vendors
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class VendorsRepository private constructor() {

    // Variables

    private var googleSignInAccount: GoogleSignInAccount? = null
    private var db: KimlicDB = KimlicDB.getInstance()!!
    private var vendorDao = db.vendorDao()

    // New request to Vendors - saves RP docs to database. database provides this docs by LiveData

    fun rpDocumentsRequest(accountAddress: String, url: String, onError: () -> Unit) {
        val headers = mapOf(Pair("account-address", accountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"), Pair("Content-Type", "application/json"))

        val vendorsRequest = KimlicJSONRequest(GET, url + KimlicApi.VENDORS.path, headers, JSONObject(),
                Response.Listener { it ->
                    if (!it.getJSONObject("headers").optString("statusCode").toString().startsWith("2")) {
                        onError()
                        return@Listener
                    }

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

    fun companyDetailsRequest(accountAddress: String, url: String, onSuccess: (company: Company) -> Unit, onError: () -> Unit) {
        val headers = mapOf(Pair("account-address", accountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"), Pair("Content-Type", "application/json"))
        val companyDetailsRequest = KimlicJSONRequest(GET, url + KimlicApi.COMPANY.path, headers, JSONObject(),
                Response.Listener {
                    if (!it.getJSONObject("headers").optString("statusCode").toString().startsWith("2")) {
                        onError()
                        return@Listener
                    }

                    val companyJson = it.getJSONObject("data").getJSONObject("company").toString()
                    val type = object : TypeToken<Company_>() {}.type
                    val company_: Company_ = Gson().fromJson(companyJson, type)
                    val company = CompanyMapper().transform(company_)
                    onSuccess(company)
                },
                Response.ErrorListener { error ->
                    onError()
                }
        )
        VolleySingleton.getInstance(KimlicApp.applicationContext()).requestQueue.add(companyDetailsRequest)
    }

    // Clear all vendors from db on start of activity
    fun clearVendorsDocs() {
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


    // Vendor Documents

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