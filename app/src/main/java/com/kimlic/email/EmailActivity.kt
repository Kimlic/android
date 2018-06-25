package com.kimlic.email

import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
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
                if (event!!.keyCode == KeyEvent.KEYCODE_ENTER) {
                    manageInput(); hideKeyboard(); return true
                }
                return false
            }
        })

        backTv.setOnClickListener { finish() }
    }

    private fun manageInput() {
        if (isEmailValid()) {
            emailEt.setError(null)
            PresentationManager.emailVerify(this)
        } else {
            emailEt.setError("invalid")
        }
    }

    private fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEt.text.toString()).matches()
}