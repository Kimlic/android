package com.kimlic.settings

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kimlic.BackupUpdatingFragment
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.db.SyncService
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.passcode.PasscodeActivity
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.recovery.RecoveryViewModel
import com.kimlic.utils.AppConstants
import com.kimlic.utils.BaseCallback
import com.kimlic.utils.Settings
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    // Companion

    companion object {
        const val PASSCODE_REQUEST_CODE = 42
        const val GOOGLE_SIGNE_IN_REQUEST_CODE = 108
    }

    // Variables

    private lateinit var settingsList: MutableList<Setting>
    private val adapter: SettingsAdapter = SettingsAdapter()
    private lateinit var model: ProfileViewModel
    private lateinit var recoveryModel: RecoveryViewModel
    private var timer: CountDownTimer? = null
    private var backupUpdatingFragment: BackupUpdatingFragment? = null
    private var deleteWarningFragment: DeleteWarningFragment? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        recoveryModel = ViewModelProviders.of(this).get(RecoveryViewModel::class.java)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        initSettingsList()
        adapter.setSettingsList(settingsList)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        //super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PASSCODE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK)
                    PresentationManager.touchCreate(this@SettingsActivity)
            }
            GOOGLE_SIGNE_IN_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) return

                backupProfile()
            }
        }
    }

    // Private

    private fun setupUI() {
        setupAdapter()
        backBt.setOnClickListener { finish() }

        deleteBt.setOnClickListener {
            deleteWarningFragment = DeleteWarningFragment.newInstance()
            deleteWarningFragment!!.setCallback(object : BaseCallback {
                override fun callback() {
                    signOut()
                }
            })
            deleteWarningFragment!!.show(supportFragmentManager, DeleteWarningFragment.FRAGMENT_KEY)
        }
    }

    private fun setupAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recycler.layoutManager = layoutManager
        recycler.hasFixedSize()

        adapter.onItemClick = object : OnItemClick {
            override fun onClick(view: View, position: Int) {
                when (view.tag) {
                    Settings.PASSCODE.tag ->
                        if (Prefs.isPasscodeEnabled) PresentationManager.passcodeDisable(this@SettingsActivity)
                        else PresentationManager.passcode(this@SettingsActivity, 45)// unused request code
                    Settings.FINGERPRINT.tag -> {
                        if (!Prefs.isPasscodeEnabled) {
                            passcodeForResult(); return
                        }

                        if (Prefs.isTouchEnabled) PresentationManager.touchDisable(this@SettingsActivity)
                        else PresentationManager.touchCreate(this@SettingsActivity)
                    }
                    Settings.DRIVE.tag -> {
                        when (Prefs.isDriveActive) {
                            true -> gDriveWarningPopupImmersive()
                            false -> {
                                if (GoogleSignIn.getLastSignedInAccount(this@SettingsActivity) == null) SyncService.signIn(this@SettingsActivity, GOOGLE_SIGNE_IN_REQUEST_CODE)
                                else
                                    backupProfile()
                            }
                        }
                    }
                    Settings.RECOVERY.tag -> PresentationManager.recoveryEnable(this@SettingsActivity)
                    Settings.TERMS.tag -> PresentationManager.termsReview(this@SettingsActivity)
                    Settings.PRIVACY.tag -> PresentationManager.privacyReview(this@SettingsActivity)
                    Settings.ABOUT.tag -> PresentationManager.about(this@SettingsActivity)
                    Settings.CHANGE.tag -> PresentationManager.passcodeChange(this@SettingsActivity)
                }
            }
        }
        refreshSettingsList()
        recycler.adapter = adapter
    }

    private fun refreshSettingsList() {
        initSettingsList()
        adapter.setSettingsList(settingsList)
    }

    private fun initSettingsList() {
        settingsList = mutableListOf(
                SwitchSetting(getString(R.string.passcode), getString(R.string.protect_my_id), Settings.PASSCODE.tag, Prefs.isPasscodeEnabled),
                SwitchSetting(getString(R.string.enable_fingerprint), getString(R.string.use_my_fingerprint_to_access_kimlic), Settings.FINGERPRINT.tag, Prefs.isTouchEnabled),
                SwitchSetting(getString(R.string.google_drive_sync), getString(R.string.backup_profile_to_google_drive), Settings.DRIVE.tag, Prefs.isDriveActive),
                IntentSetting(getString(R.string.account_recovery), getString(R.string.back_up_your_credentials), Settings.RECOVERY.tag),
                IntentSetting(getString(R.string.terms_and_conditions), getString(R.string.last_modified_23_july_2017), Settings.TERMS.tag),
                IntentSetting(getString(R.string.privacy_policy), getString(R.string.last_modified_23_july_2017), Settings.PRIVACY.tag),
                IntentSetting(getString(R.string.about_kimlic), "", Settings.ABOUT.tag))

        val passcodeChange = IntentSetting(getStringValue(R.string.change_passcode), "", Settings.CHANGE.tag)

        if (!Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag == Settings.CHANGE.tag)
            settingsList.removeAt(1)

        if (Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag != Settings.CHANGE.tag)
            settingsList.add(1, passcodeChange)
    }

    private fun backupProfile() {
        showProgress()
        recoveryModel.backupProfile(
                onSuccess = {
                    hideProgress()
                    Prefs.isDriveActive = true
                    refreshSettingsList()
                    showPopupImmersive(getString(R.string.success_), getString(R.string.your_profile_synchronization_is_active))
                },
                onError = {
                    hideProgress()
                    refreshSettingsList()
                    showPopupImmersive(getString(R.string.error_), getString(R.string.synchronizing_error))
                }
        )
    }

    private fun passcodeForResult() {
        val intent = Intent(this, PasscodeActivity::class.java)
        intent.putExtra("action", "set")
        startActivityForResult(intent, PASSCODE_REQUEST_CODE)
    }

    private fun signOut() {
        model.deleteUser(Prefs.currentAccountAddress)
        Prefs.clear()
        clearAllFiles()
        QuorumKimlic.destroyInstance()
        SyncService.signOut(this)
        PresentationManager.signUpRecovery(this)
    }

    private fun clearAllFiles() = model.clearAllFiles()

    // Progress

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

    private fun gDriveWarningPopupImmersive() {
        val builder = getImmersivePopupBuilder()
        builder
                .setTitle(getString(R.string.warning_))
                .setMessage(getString(R.string.if_you_disable_google_drive_sync))
                .setPositiveButton(getString(R.string.disable)) { dialog, _ ->
                    recoveryModel.removeProfile(
                            onSuccess = {
                                SyncService.signOut(this@SettingsActivity)
                                Prefs.isDriveActive = false
                                refreshSettingsList()
                            },
                            onError = {
                                Prefs.isDriveActive = false
                            }
                    )
                    dialog?.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    refreshSettingsList()
                }
                .setOnDismissListener { refreshSettingsList() }
                .setCancelable(true)

        val dialog = builder.create()
        dialog.show()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }
}