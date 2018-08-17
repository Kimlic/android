package com.kimlic.phone

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.vendors.VendorsRepository
import kotlinx.android.synthetic.main.activity_phone.*

class PhoneActivity : BaseActivity() {

    // Variables

    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null
    private var handler: Handler? = null
    private var timer: CountDownTimer? = null
    private lateinit var countries: List<VendorsRepository.Country>
    private lateinit var phoneModel: PhoneViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        handler = Handler(Looper.getMainLooper())
        phoneModel = ViewModelProviders.of(this).get(PhoneViewModel::class.java)

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
        countries = phoneModel.countries()

        phoneEt.addTextChangedListener(object : PhoneNumberFormattingTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)

                if (!s!!.startsWith("+") && !s.contains("+")) {
                    phoneEt.text = Editable.Factory.getInstance().newEditable("+" + phoneEt.text)
                    phoneEt.setSelection(phoneEt.text.length)
                }

                handler?.post {
                    var country = ""
                    val stringToCheck = if (s.startsWith("+")) s.substring(1) else s

                    countries.forEach { if (stringToCheck.startsWith(it.code.toString())) country = it.country }

                    runOnUiThread { countryEt.text = Editable.Factory.getInstance().newEditable(country) }
                }
            }
        })

        phoneEt.setOnClickListener { phoneEt.setSelection(phoneEt.text.length) }
        phoneEt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })
        nextBt.setOnClickListener { managePhone() }
        countryEt.setOnClickListener { initDropList() }
        backBt.setOnClickListener { finish() }
    }

    private fun managePhone() {
        if (!isPhoneValid()) {
            phoneEt.error = getString(R.string.phone_is_not_valid); return
        }
        nextBt.isClickable = false
        showProgress()

        val phone = phoneEt.text.toString().replace(" ", "")

        phoneModel.verify(phone,
                onSuccess = { PresentationManager.phoneNumberVerify(this, phoneEt.text.toString()) },
                onError = { hideProgress(); unableToProceed() })
    }

    private fun initDropList() {
        val types = countries.map { it.country }.toList().toTypedArray()

        AlertDialog.Builder(this)
                .setItems(types) { _, which ->
                    countryEt.text = Editable.Factory.getInstance().newEditable(countries[which].country)
                    phoneEt.text = Editable.Factory.getInstance().newEditable("+" + countries[which].code)
                    phoneEt.setSelection(phoneEt.text.length)
                }.show()
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

    private fun hideProgress() {
        if (blockchainUpdatingFragment != null) blockchainUpdatingFragment?.dismiss(); timer?.cancel()
    }

    private fun isPhoneValid(): Boolean {
        val list = phoneEt.text.toString().split(" ")
        var phone = ""
        list.forEach { phone += it }
        return phone.matches("^[+]?[0-9]{10,13}\$".toRegex())
    }
}


