package com.kimlic.email

import android.os.Bundle
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
import com.kimlic.utils.QuorumURL
import kotlinx.android.synthetic.main.activity_email.*

class EmailActivity : BaseActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(emailEt)
    }

    // Private

    private fun setupUI() {
        nextBt.setOnClickListener {
            manageInput()
        }

        emailEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    manageInput(); hideKeyboard(); return true
                }
                return false
            }
        })

        backTv.setOnClickListener { finish() }
    }

    private fun manageInput() {

        if (!isEmailValid()) {
            emailEt.setError(null)

            val requestParams = emptyMap<String, String>().toMutableMap()
            val headers = emptyMap<String, String>().toMutableMap()

            headers.put("authorization", Prefs.authorization)
            headers.put("account-address", Prefs.accountAddress)
            headers.put("auth-secret-token", Prefs.authSecretCode)

            //params.put("email", emailEt.text.toString())
            requestParams.put("email", "babenkovladimirbmd@gmail.com")

            val request = KimlicRequest(Request.Method.POST, QuorumURL.emailVerify.url,
                    Response.Listener<String> { response ->
                        PresentationManager.emailVerify(this, emailEt.text.toString())
                        Log.d("TAGPOST", response.toString())
                    },
                    Response.ErrorListener {
                        Log.d("TAGPOST", "error")
                    }
            )

            request.setHeaders(headers)
            request.setParams(requestParams)

            VolleySingleton.getInstance(this).addToRequestQueue(request)
            // PresentationManager.emailVerify(this, emailEt.text.toString())

        } else {
            emailEt.setError("invalid")
        }
    }

    private fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEt.text.toString()).matches()
}