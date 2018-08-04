package com.kimlic.phone

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper

import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.util.Log

import android.view.KeyEvent
import android.view.View
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
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.quorum.crypto.Sha
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_phone.*
import org.json.JSONObject
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.ExecutionException

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
        nextBt.setOnClickListener { managePhone() }
        backBt.setOnClickListener { finish() }
        countryEt.setOnClickListener { initDropList() }
    }

    private fun managePhone() {
        if (!isPhoneValid()) {
            phoneEt.error = getString(R.string.phone_is_not_valid); return
        }
        nextBt.isClickable = false
        showProgress()

        Thread(Runnable {
            val phone = phoneEt.text.toString().replace(" ", "")
            val quorumKimlic = QuorumKimlic.getInstance()
            var receiptPhone: TransactionReceipt? = null
            
            try {
                receiptPhone = quorumKimlic.setFieldMainData(Sha.sha256(phone), "phone")
            } catch (e: ExecutionException) {
                unableToProceed()
            } catch (e: InterruptedException) {
                unableToProceed()
            }
            if (receiptPhone != null && receiptPhone.transactionHash.isNotEmpty()) {
                val headers = mapOf(Pair("account-address", Prefs.currentAccountAddress))
                val params = mapOf(Pair("phone", phone))

                val request = KimlicRequest(Request.Method.POST, QuorumURL.phoneVerify.url, headers, params, Response.Listener { response ->
                    val responceCode = JSONObject(response).getJSONObject("meta").optString("code").toString()
                    if (responceCode.startsWith("2")) {
                        hideProgress();PresentationManager.phoneNumberVerify(this, phoneEt.text.toString())
                    } else
                        unableToProceed()
                }, Response.ErrorListener {
                    unableToProceed()
                })
                VolleySingleton.getInstance(this@PhoneActivity).addToRequestQueue(request)
            }
        }).start()
    }

    private fun initDropList() {
        val types = countriesList.map { it.country }.toList().toTypedArray()

        AlertDialog.Builder(this)
                .setItems(types, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        countryEt.text = Editable.Factory.getInstance().newEditable(countriesList.get(which).country)
                        phoneEt.text = Editable.Factory.getInstance().newEditable("+" + countriesList.get(which).code)
                        phoneEt.setSelection(phoneEt.text.length)
                    }
                }).show()
    }

    private fun unableToProceed() {
        hideProgress()
        runOnUiThread { nextBt.isClickable = true; showPopup(message = getString(R.string.unable_to_proceed_with_verification)) }
    }

    private fun showProgress() {
        timer = object : CountDownTimer(500, 500) {
            override fun onFinish() {
                blockchainUpdatingFragment = BlockchainUpdatingFragment.newInstance()
                blockchainUpdatingFragment?.show(supportFragmentManager, BlockchainUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() = runOnUiThread {
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss(); timer.let { it?.cancel() }
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


