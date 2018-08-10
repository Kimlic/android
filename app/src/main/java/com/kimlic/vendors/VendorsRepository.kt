package com.kimlic.vendors

import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.KimlicApp
import com.kimlic.utils.QuorumURL
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

    fun getDocuments(accountAddress: String, responseListner: Response.Listener<String>, errorListner: Response.ErrorListener) {
        val headers = mapOf(Pair("account-address", accountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"))

        val vendorsRequest = KimlicRequest(GET, QuorumURL.vendors.url, headers, emptyMap(), responseListner, errorListner)

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

    class Country(val country: String, val sh: String, val code: Int)


}


