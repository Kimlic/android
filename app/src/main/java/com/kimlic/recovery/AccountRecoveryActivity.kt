package com.kimlic.recovery

import android.os.Bundle
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_account_recovery.*

class AccountRecoveryActivity : BaseActivity() {

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_recovery)

        setupUI()
    }

    // Private

    private fun setupUI() {
        backTv.setOnClickListener {
            PresentationManager.signupRecovery(this)
        }

        verifyBt.setOnClickListener {
            //TODO use pgraseList
            //getPhraseItemsList()
            successfull()
        }
    }

    private fun getPhraseList(): List<String> {
        val phraseText = phraseEt.text.toString()
        val phraseList: List<String> = phraseText.split(" ")
        return phraseList
    }

    private fun successfull() {
        val fragment = RecoverySuccesfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@AccountRecoveryActivity)
            }
        })
        fragment.show(supportFragmentManager, RecoverySuccesfullFragment.FRAGMENT_KEY)
    }
}
