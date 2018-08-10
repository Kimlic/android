package com.kimlic.mnemonic

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_verify_passphrase.*

class MnemonicVerifyActivity : BaseActivity() {

    // Binding

    @BindViews(R.id.phrase1Et, R.id.phrase2Et, R.id.phrase3Et, R.id.phrase4Et)
    lateinit var editTextList: List<@JvmSuppressWildcards EditText>

    @BindViews(R.id.phrase1Til, R.id.phrase2Til, R.id.phrase3Til, R.id.phrase4Til)
    lateinit var titleList: List<@JvmSuppressWildcards TextInputLayout>

    // Variables

    private val hintList: List<Int> = listOf(2, 4, 7, 11)
    private lateinit var model: ProfileViewModel

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_passphrase)
        ButterKnife.bind(this)

        setupUI()
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        verifyBt.setOnClickListener {
            if (validEmptyFields()) {
                if (phrasesMatch()) {
                    Prefs.isRecoveryEnabled = true
                    successfull()
                } else
                    showPopup(message = getString(R.string.mnemonic_phrases_do_not_match))
//                    errorPopup(getString(R.string.mnemonic_phrases_do_not_match))

            }
        }

        backTv.setOnClickListener {
            Prefs.isRecoveryEnabled = false
            PresentationManager.stage(this)
        }
        setupHints(hintList)
    }

    private fun setupHints(hintList: List<Int>) {
        for (i in 0 until hintList.size) {
            titleList[i].hint = getString(R.string.th_word, hintList[i])
        }
    }

    private fun successfull() {
        val fragment = MnemonicSuccessfullFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@MnemonicVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, MnemonicSuccessfullFragment.FRAGMENT_KEY)
    }

    private fun phrasesMatch(): Boolean {
        var noError = true
//        val mnemonicList = KimlicDB.getInstance()!!.userDao().select(Prefs.currentId).mnemonic.split(" ")
        val mnemonicList = model.getUser(Prefs.currentAccountAddress).mnemonic.split(" ")

        for (i in 0 until hintList.size) noError = mnemonicList[hintList[i] - 1].equals(editTextList[i].text.toString())

        return noError
    }

    private fun validEmptyFields(): Boolean {
        var noError = true

        editTextList.forEach {
            if (it.text.length < 3) {
                it.setError("error"); noError = false
            } else {
                it.setError(null); noError = true
            }
        }
        return noError
    }
}
