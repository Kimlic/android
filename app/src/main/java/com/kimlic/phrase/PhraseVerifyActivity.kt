package com.kimlic.phrase

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_verify_passphrase.*

class PhraseVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.phrase1Et, R.id.phrase2Et, R.id.phrase3Et, R.id.phrase4Et)
    lateinit var editTextList: List<@JvmSuppressWildcards EditText>

    @BindViews(R.id.phrase1Til, R.id.phrase2Til, R.id.phrase3Til, R.id.phrase4Til)
    lateinit var titleList: List<@JvmSuppressWildcards TextInputLayout>

    // Mocks

    private val mockHintList: List<String> = listOf("5th word", "7th word", "12th word", "17th word")

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_passphrase)
        ButterKnife.bind(this)

        setupUI()
    }

    // Private

    private fun setupUI() {
        verifyBt.setOnClickListener {
            // use entered words to verify
            Prefs.isRecoveryEnabled = true
            successfull()
        }

        backTv.setOnClickListener {
            // Temporary
            Prefs.isRecoveryEnabled = false
            PresentationManager.stage(this)
        }
        setupHints(mockHintList)
    }

    private fun setupHints(hintList: List<String>) {
        for (i in 0 until hintList.size) {
            titleList[i].hint = hintList[i]
        }
    }

    private fun successfull() {
        val fragment = MnemonicSuccessfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@PhraseVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, MnemonicSuccessfullFragment.FRAGMENT_KEY)
    }

}
