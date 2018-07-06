package com.kimlic.phone

import android.os.Bundle
import android.os.Handler
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.Sha
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_phone.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class PhoneActivity : BaseActivity() {

    // Variables

    private lateinit var countriesList: List<Country>
    private var handler: Handler?
    private var countryCode = 0

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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // Private

    private fun setupUI() {
        countriesList = countries()

        phoneEt.addTextChangedListener(object : PhoneNumberFormattingTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)

                if (!s!!.startsWith("+") && !s.contains("+")) {
                    phoneEt.text = Editable.Factory.getInstance().newEditable("+" + phoneEt.text)
                    phoneEt.setSelection(phoneEt.text.length)
                }

                handler?.post(Runnable {
                    var country: String = ""
                    countryCode = 0
                    val stringtoCheck = if (s.startsWith("+")) s.substring(1) else s

                    countriesList.forEach { if (stringtoCheck.startsWith(it.code.toString())) country = it.country }

                    runOnUiThread { countryEt.text = Editable.Factory.getInstance().newEditable(country) }
                })
            }
        })
        phoneEt.setOnClickListener { phoneEt.setSelection(phoneEt.text.length) }

        phoneEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //managePhone()
                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        nextBt.setOnClickListener { managePhone() }
    }

    private fun managePhone() {
        if (isPhoneValid()) {
            phoneEt.setError(null)

            val phone = phoneEt.text.toString().replace(" ", "")
            val params = emptyMap<String, String>().toMutableMap()
            val headers = emptyMap<String, String>().toMutableMap()

            val quorumKimlic = QuorumKimlic.getInstance()
            val address = quorumKimlic.address

            val receiptPhone = quorumKimlic.setAccountFieldMainData(Sha.sha256(phone), "phone")

            headers.put("account-address", address)
            params.put("phone", phone)

            val request = KimlicRequest(Request.Method.POST, QuorumURL.phoneVerify.url,
                    Response.Listener<String> { response ->
                        val responceCode = JSONObject(response).getJSONObject("meta").optString("code").toString()
                        
                        if (responceCode.startsWith("2")) PresentationManager.phoneNumberVerify(this@PhoneActivity, phone)
                    },
                    Response.ErrorListener { showToast("onError") }
            )

            request.requestHeaders = headers
            request.requestParasms = params

            VolleySingleton.getInstance(this).addToRequestQueue(request)
        } else
            phoneEt.setError(getString(R.string.phone_is_not_valid))
    }

    private fun isPhoneValid(): Boolean {
        val list = phoneEt.text.toString().split(" ")
        var phone: String = ""
        list.forEach { phone = phone + it }
        return phone.matches("^[+]?[0-9]{10,13}\$".toRegex())
    }

    private fun countries(): List<Country> {
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