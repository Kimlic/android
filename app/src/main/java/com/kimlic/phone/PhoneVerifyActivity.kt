package com.kimlic.phone

import android.os.Bundle
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.BaseDialogFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_phone_verify.*

class PhoneVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.digit1Et, R.id.digit2Et, R.id.digit3Et, R.id.digit4Et)
    lateinit var mDigitList: List<@JvmSuppressWildcards EditText>

    // Variables

    private lateinit var fragment: BaseDialogFragment

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verify)

        ButterKnife.bind(this)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(digit1Et)
    }

    // Private

    private fun setupUI() {

        // OnClick
        verifyBt.setOnClickListener {
            if (pinEntered()) {

                if (true) {// If pin is accepted
                    successfull()
                }
            } else showToast("Pin is not entered")
        }
        changeTv.setOnClickListener { PresentationManager.phoneNumber(this) }
        showSoftKeyboard(digit1Et)
    }

    private fun pinEntered(): Boolean {
        var count = 0
        mDigitList.forEach { it -> if (!it.text.isEmpty()) count++ }

        return (count == 4)
    }

    private fun successfull() {
        fragment = PhoneSuccessfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@PhoneVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, PhoneSuccessfullFragment.FRAGMENT_KEY)
    }

}