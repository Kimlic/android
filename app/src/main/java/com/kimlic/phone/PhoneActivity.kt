package com.kimlic.phone

import android.os.Bundle
import android.os.Handler
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.activity_phone.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

open class PhoneActivity : BaseActivity() {

    // Variables

    private lateinit var countriesList: List<Country>
    private var handler: Handler?

    // Init

    init {
        handler = Handler()
    }

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(phoneEt)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler = null
    }

    // Private

    private fun setupUI() {
        countriesList = readCountries()

        phoneEt.addTextChangedListener(object : PhoneNumberFormattingTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)

                handler?.post(Runnable {
                    var country: String = "" //getString(R.string.country)
                    var stringtoCheck = if (s!!.startsWith("+")) s.substring(1) else s
                    countriesList.forEach { if (stringtoCheck.startsWith(it.code.toString())) country = it.country }
                    runOnUiThread { countryEt.text = Editable.Factory.getInstance().newEditable(country) }
                })
            }
        })

        nextBt.setOnClickListener {
            if (isPhoneValid()) {
                phoneEt.setError(null)
                // TODO use phone number
                PresentationManager.phoneNumberVerify(this)
            } else
                phoneEt.setError(getString(R.string.phone_is_not_valid))
        }
    }

    private fun isPhoneValid() = phoneEt.text.toString().matches("^[+]?[0-9]{10,13}\$".toRegex())

    private fun readCountries(): List<Country> {
        val countries = mutableListOf<Country>()
        var hasNextLine = true

        try {
            val reader = BufferedReader(InputStreamReader(assets.open("countries.dat")))
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