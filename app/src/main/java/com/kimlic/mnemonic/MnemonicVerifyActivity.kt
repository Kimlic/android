package com.kimlic.mnemonic

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.support.design.widget.TextInputLayout
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import butterknife.BindViews
import butterknife.ButterKnife
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kimlic.BackupUpdatingFragment
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.SyncService
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.preferences.Prefs
import com.kimlic.recovery.RecoveryViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import kotlinx.android.synthetic.main.activity_verify_passphrase.*

class MnemonicVerifyActivity : BaseActivity() {

    // Constants

    companion object {
        private const val GOOGLE_SIGNE_IN_REQUEST_CODE = 109
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GOOGLE_SIGNE_IN_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) return

                backupProfile()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        //super.onSaveInstanceState(outState, outPersistentState)
    }

    // Private

    private fun setupUI() {
        verifyBt.setOnClickListener {
            if (validEmptyFields())
                if (phrasesMatch()) {
                    Prefs.isRecoveryEnabled = true
                    if (GoogleSignIn.getLastSignedInAccount(this) == null) {
                        SyncService.signIn(this, GOOGLE_SIGNE_IN_REQUEST_CODE)
                    } else backupProfile()
                    //successful()
                } else showPopupImmersive(getString(R.string.error), getString(R.string.mnemonic_phrases_do_not_match))
        }

        backTv.setOnClickListener { PresentationManager.stage(this) }
        setupHints(hintList)

        editTextList.forEach {
            it.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(); return@OnEditorActionListener true
                }
                false
            })

        }
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

        for (i in 0 until hintList.size) noError = mnemonicList[hintList[i] - 1] == editTextList[i].text.toString().toLowerCase()

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

    private fun backupProfile() {
        showProgress()
        recoveryModel.backupProfile(
                onSuccess = {
                    hideProgress()
                    Prefs.isDriveActive = true
                    successful()
                    //showPopupImmersive(getString(R.string.success_), getString(R.string.your_profile_synchronization_is_active))// -> From here shows succsessful fragment
                },
                onError = {
                    hideProgress()
                    showPopupImmersive(getString(R.string.error_), getString(R.string.synchronizing_error))
                }
        )
    }

    private fun showProgress() {
        timer = object : CountDownTimer(0, 0) {
            override fun onFinish() {
                val bundle = Bundle()
                bundle.putString(AppConstants.TITLE.key, getString(R.string.backup))
                bundle.putString(AppConstants.SUBTITLE.key, getString(R.string.backup_profile_to_google_drive))
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