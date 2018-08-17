package com.kimlic.email

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.entity.Contact
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.phone.PhoneSuccessfulFragment
import com.kimlic.preferences.Prefs
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_email_verify.*

class EmailVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.digit1Et, R.id.digit2Et, R.id.digit3Et, R.id.digit4Et)
    lateinit var digitsList: List<@JvmSuppressWildcards EditText>

    // Variables

    private var currentHolder = 0
    private lateinit var code: StringBuilder
    private lateinit var email: String
    private lateinit var model: ProfileViewModel
    private lateinit var emailViewModel: EmailViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verify)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        emailViewModel = ViewModelProviders.of(this).get(EmailViewModel::class.java)

        ButterKnife.bind(this)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(digit1Et)
    }

    // Private

    private fun setupUI() {

        email = intent.extras.getString("email", "")
        titleTv.text = Editable.Factory.getInstance().newEditable(this.getString(R.string.code_sent_to_email, email))

        digitsList[currentHolder].requestFocus()
        verifyBt.setOnClickListener { managePins() }

        changeTv.setOnClickListener { finish() }
        backBt.setOnClickListener { finish() }

        setupDigitListener()

        digitsList[3].setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(); return@OnEditorActionListener true
            }
            false
        })
    }

    private fun managePins() {
        if (!pinEntered()) showToast(getString(R.string.pin_is_not_enterd))
        else {
            progressBar.visibility = View.VISIBLE
            verifyBt.isClickable = false
            code = StringBuilder()
            digitsList.forEach { code.append(it.text.toString()) }

            emailViewModel.emailApprove(code.toString(),
                    onSuccess = { insertEmail(email); successful() },
                    onError = {
                        when (it.first().toString()) {
                            "4" -> unableVerifyCode()
                            else -> unableToProceed()
                        }
                    })
        }
    }

    private fun insertEmail(email: String) {
        val emailContact = Contact(value = email, type = "email", approved = true)
        model.addContact(Prefs.currentAccountAddress, emailContact)
    }

    private fun pinEntered(): Boolean {
        var count = 0
        digitsList.forEach { it -> if (!it.text.isEmpty()) count++ }
        return (count == 4)
    }

    private fun successful() {
        val fragment = EmailSuccesfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@EmailVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, PhoneSuccessfulFragment.FRAGMENT_KEY)
    }

    private fun unableToProceed() {
        progressBar.visibility = View.GONE
        verifyBt.isClickable = true
        showPopup(message = getString(R.string.unable_to_proceed_with_verification))
    }

    private fun unableVerifyCode() {
        progressBar.visibility - View.GONE
        verifyBt.isClickable = true
        digitsList.forEach { it.text.clear() }
        showPopup(message = getString(R.string.unable_to_verify_the_code))
    }

    private fun setupDigitListener() {
        digitsList.forEach {

            it.setOnKeyListener(object : View.OnKeyListener {
                val position = Integer.valueOf(it.tag.toString())

                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event!!.action != KeyEvent.ACTION_DOWN)
                        return false

                    when (keyCode) {
                        KeyEvent.KEYCODE_0 -> moveNext(0)
                        KeyEvent.KEYCODE_1 -> moveNext(1)
                        KeyEvent.KEYCODE_2 -> moveNext(2)
                        KeyEvent.KEYCODE_3 -> moveNext(3)
                        KeyEvent.KEYCODE_4 -> moveNext(4)
                        KeyEvent.KEYCODE_5 -> moveNext(5)
                        KeyEvent.KEYCODE_6 -> moveNext(6)
                        KeyEvent.KEYCODE_7 -> moveNext(7)
                        KeyEvent.KEYCODE_8 -> moveNext(8)
                        KeyEvent.KEYCODE_9 -> moveNext(9)
                        KeyEvent.KEYCODE_DEL -> moveBack()
                    }
                    return false
                }

                private fun moveNext(keyCode: Int) {
                    if (position < 4) {
                        digitsList.elementAt(position).text = Editable.Factory.getInstance().newEditable(keyCode.toString())
                        digitsList.elementAt(position).background = resources.getDrawable(R.drawable.square_edittext_background_dark, null)

                        if (position < 3) digitsList.elementAt(position + 1).requestFocus()
                    }
                }

                private fun moveBack() {
                    digitsList.elementAt(position).background = resources.getDrawable(R.drawable.square_edittext_background_trasparent, null)
                    digitsList.elementAt(position).text = Editable.Factory.getInstance().newEditable("")

                    if (position > 0) digitsList.elementAt(position - 1).requestFocus()
                }
            })
        }
    }
}