package com.kimlic.email

import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import butterknife.BindViews
import butterknife.ButterKnife
import com.android.volley.Request
import com.android.volley.Response
import com.kimlic.API.KimlicRequest
import com.kimlic.API.VolleySingleton
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.phone.PhoneSuccessfullFragment
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_email_verify.*
import org.json.JSONObject

class EmailVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.digit1Et, R.id.digit2Et, R.id.digit3Et, R.id.digit4Et)
    lateinit var digitsList: List<@JvmSuppressWildcards EditText>

    // Variables

    private var currentHolder = 0
    private lateinit var code: StringBuilder
    private lateinit var email: String

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verify)

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

        cancelTv.setOnClickListener { finish(); showToast("change Email") }
        setupDigitListner()

        digitsList[3].setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(); return true
                }
                return false
            }
        })
    }

    private fun managePins() {
        if (pinEntered()) {
            progressBar.visibility = View.VISIBLE
            verifyBt.isClickable = false
            code = StringBuilder()
            digitsList.forEach { code.append(it.text.toString()) }

            val quorumKimlic = QuorumKimlic.getInstance()
            val address = quorumKimlic.address

            val params = emptyMap<String, String>().toMutableMap(); params.put("code", code.toString())
            val headers = emptyMap<String, String>().toMutableMap(); headers.put("account-address", address)

            val request = KimlicRequest(Request.Method.POST, QuorumURL.emailVerifyApprove.url,
                    Response.Listener<String> { response ->
                        val responceCode = JSONObject(response).getJSONObject("meta").optString("code").toString()
                        val status = JSONObject(response).getJSONObject("data").optString("status").toString()

                        if (responceCode.startsWith("2") && status.equals("ok")) {
                            Prefs.userEmail = email;
                            Prefs.isUserEmailAccepted = true
                            verifyBt.isClickable = true
                            successfull()
                        } else {
                            verifyBt.isClickable = true
                            digitsList.forEach { it.text.clear() }
                            showPopup(message = getString(R.string.unable_to_verify_the_code))
                        }
                    },
                    Response.ErrorListener { unableToProceed() }
            )

            request.requestHeaders = headers
            request.requestParasms = params

            VolleySingleton.getInstance(this).addToRequestQueue(request)

        } else showToast(getString(R.string.pin_is_not_enterd))
    }

    private fun pinEntered(): Boolean {
        var count = 0
        digitsList.forEach { it -> if (!it.text.isEmpty()) count++ }

        return (count == 4)
    }

    private fun successfull() {
        val fragment = EmailSuccesfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@EmailVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, PhoneSuccessfullFragment.FRAGMENT_KEY)
    }

    private fun unableToProceed() {
        runOnUiThread { progressBar.visibility = View.GONE }
        verifyBt.isClickable = true
        showPopup(message = getString(R.string.unable_to_proceed_with_verification))
    }

    private fun setupDigitListner() {
        digitsList.forEach {

            it.setOnKeyListener(object : View.OnKeyListener {
                val position = Integer.valueOf(it.tag.toString())

                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event!!.getAction() != KeyEvent.ACTION_DOWN)
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
                        digitsList.elementAt(position).background = resources.getDrawable(R.drawable.square_edittext_background_dark)
                        if (position < 3) digitsList.elementAt(position + 1).requestFocus()
                    }
                }

                private fun moveBack() {
                    digitsList.elementAt(position).background = resources.getDrawable(R.drawable.square_edittext_background_trasparent)
                    digitsList.elementAt(position).text = Editable.Factory.getInstance().newEditable("")

                    if (position > 0) digitsList.elementAt(position - 1).requestFocus()
                }
            })
        }
    }
}