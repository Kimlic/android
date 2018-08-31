package com.kimlic.mnemonic

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.widget.EditText
import butterknife.BindViews
import butterknife.ButterKnife
import com.kimlic.BackupUpdatingFragment
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.recovery.RecoveryViewModel
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
    private lateinit var recoveryModel: RecoveryViewModel
    private var timer: CountDownTimer? = null
    private var backupUpdatingFragment: BackupUpdatingFragment? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_passphrase)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        recoveryModel = ViewModelProviders.of(this).get(RecoveryViewModel::class.java)
        ButterKnife.bind(this)
        setupUI()
    }

    // Private

    private fun setupUI() {
        verifyBt.setOnClickListener {

            if (validEmptyFields())
                if (phrasesMatch()) {
                    showProgress()
                    // onError show popup error recovery activating
                    // onSuccess - show popup successful and finish
                    recoveryModel.backupProfile(onSuccess = {

                        Log.d("TAGRECOVERY", "All Files are restored successful")
                        Prefs.isRecoveryEnabled = true
                        hideProgress()
                        successful()
                    }, onError = {
                        Log.d("TAGRECOVERY", "E R R O R   B A C K U P ")
                    })

                } else
                    showPopup(message = getString(R.string.mnemonic_phrases_do_not_match))
        }

        backTv.setOnClickListener {
            PresentationManager.stage(this)
        }
        setupHints(hintList)
    }

    private fun setupHints(hintList: List<Int>) {
        for (i in 0 until hintList.size) titleList[i].hint = getString(R.string.th_word, hintList[i])
    }

    private fun successful() {
        val fragment = MnemonicSuccessfulFragment.newInstance()
        fragment.setCallback(object : BaseCallback {
            override fun callback() {
                PresentationManager.stage(this@MnemonicVerifyActivity)
            }
        })
        fragment.show(supportFragmentManager, MnemonicSuccessfulFragment.FRAGMENT_KEY)
    }

    private fun phrasesMatch(): Boolean {
        var noError = true
        val mnemonicList = model.user().mnemonic.split(" ")

        for (i in 0 until hintList.size) noError = mnemonicList[hintList[i] - 1] == editTextList[i].text.toString()

        return noError
    }

    private fun validEmptyFields(): Boolean {
        var noError = true

        editTextList.forEach {
            noError = if (it.text.length < 3) {
                it.error = getString(R.string.error); false
            } else {
                it.error = null; true
            }
        }
        return noError
    }

    // Progress

    private fun showProgress() {
        timer = object : CountDownTimer(0, 0) {
            override fun onFinish() {
                val bundle = Bundle()
                bundle.putString("title", "Backup")
                bundle.putString("subtitle", "Backup profile to Google Drive")
                backupUpdatingFragment = BackupUpdatingFragment.newInstance(bundle)
                backupUpdatingFragment?.show(supportFragmentManager, BackupUpdatingFragment.FRAGMENT_KEY)
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }

    private fun hideProgress() = runOnUiThread {
        if (backupUpdatingFragment != null) backupUpdatingFragment?.dismiss(); timer?.cancel()
    }
}