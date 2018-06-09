package com.kimlic.profile_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import butterknife.OnClick
import com.kimlic.BaseActivity
import com.kimlic.BaseFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.passcode.PasscodeSuccessfullFragment
import com.kimlic.phone.PhoneSuccessfullFragment
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.fragment_email_verify.*

class EmailVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.digit1Et, R.id.digit2Et, R.id.digit3Et, R.id.digit4Et)
    lateinit var mDigitListE: List<@JvmSuppressWildcards EditText>

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_email_verify)

        ButterKnife.bind(this)
        setupUI()
    }

    // Private

    private fun setupUI() {
        verifyBt.setOnClickListener {
            if (pinEntered())
                successfull()

            else
                showToast("Pins are NOT entered")
        }

        changeTv.setOnClickListener { showToast("Cancel") }

        showSoftKeyboard(digit1Et)
    }

    private fun pinEntered(): Boolean {
        var count = 0
        mDigitListE.forEach { it -> if (!it.text.isEmpty()) count++ }

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
}