package com.kimlic.phone

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.crypto.Sha
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_phone.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class PhoneActivity : BaseActivity() {

    // Variables

    private lateinit var countriesList: List<Country>
    private var handler: Handler? = null
    private var countryCode = 0
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null
    private var timer: CountDownTimer? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        handler = Handler(Looper.getMainLooper())

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(phoneEt)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler = null
        timer = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // Private

    private fun setupUI() {
        blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
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
                    val stringToCheck = if (s.startsWith("+")) s.substring(1) else s

                    countriesList.forEach { if (stringToCheck.startsWith(it.code.toString())) country = it.country }

                    runOnUiThread { countryEt.text = Editable.Factory.getInstance().newEditable(country) }
                })
            }
        })
        phoneEt.setOnClickListener { phoneEt.setSelection(phoneEt.text.length) }

        phoneEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    return true
                }
                return false
            }
        })
        nextBt.setOnClickListener {
            managePhone()
        }
    }

    private fun managePhone() {
        if (!isPhoneValid()) {
            phoneEt.error = getString(R.string.phone_is_not_valid)
            return
        }

        val phone = phoneEt.text.toString().replace(" ", "")
        val receiptPhone = QuorumKimlic.getInstance().setAccountFieldMainData(Sha.sha256(phone), "phone")

        val headers = mapOf(Pair("account-address", QuorumKimlic.getInstance().walletAddress))
        val params = mapOf(Pair("phone", phone))

        val request = KimlicRequest(Request.Method.POST, QuorumURL.phoneVerify.url, headers, params, Response.Listener { response ->
            val responceCode = JSONObject(response).getJSONObject("meta").optString("code").toString()
            Log.e("AAAAA", JSONObject(response).toString())

            if (responceCode.startsWith("2")) {
                PresentationManager.phoneNumberVerify(this, phoneEt.text.toString())
            } else
                unableToProceed()
        }, Response.ErrorListener {
            Log.e(TAG, "ERR" + String(it.networkResponse.data))
        })
        VolleySingleton.getInstance(this@PhoneActivity).addToRequestQueue(request)

        // Create new appUser with id = 0



//        // TODO: SHOW PROGRESS
//        //showProgress()
//        nextBt.isClickable = false
//        phoneEt.error = null
//        val phone = phoneEt.text.toString().replace(" ", "")
//        Log.d("TAGPHONE","in tag phone")
//        Thread(object : Runnable {
//            override fun run() {
//                Log.e(TAG, "PHONE: $phone")
//                try {
//            val receiptPhone = QuorumKimlic.getInstance().setAccountFieldMainData(Sha.sha256("+380997762791"), "phone")
//                    //val receiptPhone = QuorumKimlic.getInstance().setAccountFieldMainData(Sha.sha256(phone), "phone")
//                    Log.e("TAGReceipt", receiptPhone.toString())
//                } catch (e: Exception) {
//                    // TODO: SHOW ERROR
//                    unableToProceed()
//                }
//
////                    if (receiptPhone != null && receiptPhone.transactionHash.isNotEmpty()) {
//                val headers = mapOf(Pair("account-address", QuorumKimlic.getInstance().walletAddress))
//                val params = mapOf(Pair("phone", phone))
//                Log.e(TAG, "HEADERS: " + headers.toString())
//                Log.e(TAG, "PARAMS: " + params.toString())
//
//                val request = KimlicRequest(Request.Method.POST, QuorumURL.phoneVerify.url, headers, params, Response.Listener { response ->
//                    Log.e(TAG, "RESP: " + response)
////      hideProgress()
//                    // TODO: HIDE PROGRESS
//                    val responceCode = JSONObject(response).getJSONObject("meta").optString("code").toString()
//
//                    if (responceCode.startsWith("2")) {
//                        Log.d("TAGQUORUM", "Quorum responce = starts with 2")
////        nextBt.isClickable = true
////        PresentationManager.phoneNumberVerify(this@PhoneActivity, phoneEt.text.toString())
//                    } else
//                        unableToProceed()
//                }, Response.ErrorListener {
//                    // TODO: SHOW ERROR
//                    Log.e(TAG, "ERR" + String(it.networkResponse.data))
//                })
//                VolleySingleton.getInstance(this@PhoneActivity).addToRequestQueue(request)
////  } else unableToProceed()
//            }
//        }).start()
    }

    private fun unableToProceed() {
        runOnUiThread { hideProgress() }
        nextBt.isClickable = true
        showPopup(message = getString(R.string.unable_to_proceed_with_verification))
    }

    private fun showProgress() {
        object : CountDownTimer(1000, 1000) {
            override fun onFinish() {
                blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
                blockchainUpdatingFragment?.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() = runOnUiThread {
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss()
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


