package com.kimlic.email

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.kimlic.BaseActivity
import com.kimlic.BlockchainUpdatingFragment
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import kotlinx.android.synthetic.main.activity_email.*

class EmailActivity : BaseActivity() {

    // Variables

    private lateinit var emailModel: EmailViewModel
    private var blockchainUpdatingFragment: BlockchainUpdatingFragment? = null
    private var timer: CountDownTimer? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)
        emailModel = ViewModelProviders.of(this).get(EmailViewModel::class.java)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        showSoftKeyboard(emailEt)
    }

    // Private

    private fun setupUI() {
        nextBt.setOnClickListener { manageInput() }

        emailEt.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

        backTv.setOnClickListener { finish() }
        backBt.setOnClickListener { finish() }
    }

    private fun manageInput() {
        if (!isEmailValid()) emailEt.error = getString(R.string.invalid)
        else {
            showProgress()
            nextBt.isClickable = false
            emailEt.error = null

            val email = emailEt.text.toString()

            emailModel.emailVerify(email,
                    onSuccess = { hideProgress(); PresentationManager.emailVerify(this@EmailActivity, emailEt.text.toString()) },
                    onError = { unableToProceed() })
        }
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
        nextBt.isClickable = true
    }

    private fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEt.text.toString()).matches()

    private fun unableToProceed() {
        hideProgress()
        runOnUiThread { nextBt.isClickable = true; showPopup(message = getString(R.string.unable_to_proceed_with_werification)) }
    }
}