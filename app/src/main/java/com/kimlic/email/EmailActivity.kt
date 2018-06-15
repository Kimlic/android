package com.kimlic.email

import android.os.Bundle
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
            if (isEmailValid()) {
                emailEt.setError(null)
                PresentationManager.emailVerify(this)
            } else {
                emailEt.setError("invalid")
            }
        }

        backTv.setOnClickListener { finish() }
    }

    private fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEt.text.toString()).matches()
}