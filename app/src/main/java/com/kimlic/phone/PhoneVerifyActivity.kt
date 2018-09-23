package com.kimlic.phone

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.BaseDialogFragment
import com.kimlic.R
import com.kimlic.db.entity.Contact
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_phone_verify.*

class PhoneVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.digit1Et, R.id.digit2Et, R.id.digit3Et, R.id.digit4Et)
    lateinit var digitsList: List<@JvmSuppressWildcards EditText>

    // Variables

    private lateinit var fragment: BaseDialogFragment
    private lateinit var phone: String
    private lateinit var code: StringBuilder
    private lateinit var model: ProfileViewModel
    private lateinit var phoneModel: PhoneViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verify)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        phoneModel = ViewModelProviders.of(this).get(PhoneViewModel::class.java)

        ButterKnife.bind(this)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(digit1Et)
    }

    // Private

    private fun setupUI() {
        verifyBt.setOnClickListener { managePin() }
        backBt.setOnClickListener { finish() }
        changeTv.setOnClickListener { PresentationManager.phoneNumber(this) }

        phone = intent.extras?.getString("phone", "").orEmpty()
        titleTv.text = Editable.Factory.getInstance().newEditable(this.getString(R.string.code_sent_to, phone))

        showSoftKeyboard(digit1Et)
        setupDigitListener()

        digitsList[3].setOnEditorActionListener(TextView.OnEditorActionListener { _, _, event ->
            if (event!!.keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard(); return@OnEditorActionListener true
            }
            false
        })
    }

    private fun managePin() {
        if (pinEntered()) {
            progressBar.visibility = View.VISIBLE
            verifyBt.isClickable = false
            code = StringBuilder()
            digitsList.forEach { code.append(it.text.toString()) }


            phoneModel.emailApprove(code.toString(),
                    onSuccess = { insertPhone(phone);Prefs.authenticated = true; successful() },
                    onError = {
                        when (it.first().toString()) {
                            "4" -> unableVerifyCode()
                            else -> unableToProceed()
                        }
                    })
        } else showToast(getString(R.string.pin_is_not_enterd))
    }

    private fun unableToProceed() {
        progressBar.visibility = View.GONE
        verifyBt.isClickable = true
        showPopup(message = getString(R.string.unable_to_proceed_with_verification))
    }

    private fun unableVerifyCode() {
        progressBar.visibility = View.GONE
        verifyBt.isClickable = true
        digitsList.forEach { it.text.clear() }
        showPopup(message = getString(R.string.unable_to_verify_the_code))
    }

    private fun pinEntered(): Boolean {
        var count = 0
        digitsList.forEach { it -> if (!it.text.isEmpty()) count++ }

        return (count == 4)
    }

    private fun insertPhone(phone: String) {
        val phoneContact = Contact(value = phone, type = "phone", approved = true)
        model.addContact(Prefs.currentAccountAddress, phoneContact)
    }

    private fun successful() {
        fragment = PhoneSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {

            override fun callback() {
                finishAffinity(); PresentationManager.stage(this@PhoneVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, PhoneSuccessfulFragment.FRAGMENT_KEY)
    }

    private fun setupDigitListener() {
        digitsList.forEach {
            it.setOnKeyListener(object : View.OnKeyListener {
                val position = Integer.valueOf(it.tag.toString())

                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event!!.action != KeyEvent.ACTION_DOWN)
                        return false

                    if (keyCode == KeyEvent.KEYCODE_DEL)
                        moveBack()

                    return false
                }

                private fun moveBack() {
                    digitsList.elementAt(position).background = resources.getDrawable(R.drawable.square_edittext_background_trasparent, null)
                    digitsList.elementAt(position).text = Editable.Factory.getInstance().newEditable("")

                    if (position > 0) digitsList.elementAt(position - 1).requestFocus()
                    refreshState()
                }
            })

            it.addTextChangedListener(object : TextWatcher {
                val position = it.tag.toString().toInt()

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    when (s.toString().length) {
                        //0 -> moveBack()
                        1 -> moveNext()
                        2 -> {
                            it.text = Editable.Factory.getInstance().newEditable(s!![count - 1].toString()); moveNext()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                private fun moveNext() {
                    if (position < 3) digitsList.elementAt(position + 1).requestFocus()

                    refreshState()
                }

                private fun moveBack() {
                    if (position > 0) digitsList.elementAt(position - 1).requestFocus()

                    refreshState()
                }
            })
        }
    }

    private fun refreshState() {
        digitsList.forEach {
            if (it.text.toString() == "") {
                it.background = resources.getDrawable(R.drawable.square_edittext_background_trasparent, null)
            } else {
                it.background = resources.getDrawable(R.drawable.square_edittext_background_dark, null)
            }
        }
    }
}