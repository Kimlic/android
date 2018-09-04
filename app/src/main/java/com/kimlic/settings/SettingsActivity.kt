package com.kimlic.settings

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import butterknife.ButterKnife
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
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    // Constants

    private val PASSCODE_REQUEST_CODE = 42
    private val GOOGLE_SIGNE_IN_REQUEST_CODE = 108

    // Variables

    private lateinit var settingsList: MutableList<Setting>
    private val adapter: SettingsAdapter = SettingsAdapter()
    private lateinit var model: ProfileViewModel
    private lateinit var recoveryModel: RecoveryViewModel
    private var timer: CountDownTimer? = null
    private var backupUpdatingFragment: BackupUpdatingFragment? = null

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        recoveryModel = ViewModelProviders.of(this).get(RecoveryViewModel::class.java)
        ButterKnife.bind(this)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        initSettingsList()
        adapter.setSettingsList(settingsList)
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
        signoutBt.setOnClickListener { signOut() }
        backBt.setOnClickListener { finish() }
    }

    private fun setupAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recycler.layoutManager = layoutManager
        recycler.hasFixedSize()

        adapter.onItemClick = object : OnItemClick {
            override fun onClick(view: View, position: Int) {
                when (view.tag) {
                    "passcode" ->
                        if (Prefs.isPasscodeEnabled) PresentationManager.passcodeDisable(this@SettingsActivity)
                        else PresentationManager.passcode(this@SettingsActivity)
                    "touch" -> {
                        if (!Prefs.isPasscodeEnabled) {
                            passcodeForResult(); return
                        }

                        if (Prefs.isTouchEnabled) PresentationManager.touchDisable(this@SettingsActivity)
                        else PresentationManager.touchCreate(this@SettingsActivity)
                    }
                    "drive" -> {
                        when (Prefs.isDriveActive) {
                            true -> {
                                // Удаляем профиль
                                recoveryModel.removeProfile(
                                        onSuccess = {
                                            SyncService.signOut(this@SettingsActivity)
                                            Prefs.isDriveActive = false
                                            Log.d("TAGB", "Profile is removed")
                                        },
                                        onError = { Log.d("TAGB", "Profile is not removed") })

                            }
                            false -> {
                                // Бэкапим профиль
                                if (GoogleSignIn.getLastSignedInAccount(this@SettingsActivity) == null) SyncService.signIn(this@SettingsActivity, GOOGLE_SIGNE_IN_REQUEST_CODE)
                                else
                                    backupProfile()

                            }

                        }
                    }
                    "recovery" -> PresentationManager.recoveryEnable(this@SettingsActivity)
                    "terms" -> PresentationManager.termsReview(this@SettingsActivity)
                    "about" -> PresentationManager.about(this@SettingsActivity)
                    "change" -> PresentationManager.passcodeChange(this@SettingsActivity)
                }
            }
        }
        initSettingsList()
        adapter.setSettingsList(settingsList)
        recycler.adapter = adapter
    }

    private fun initSettingsList() {
        settingsList = mutableListOf(
                SwitchSetting(getString(R.string.passcode), getString(R.string.protect_my_id), "passcode", AppConstants.settingSwitch.intKey, Prefs.isPasscodeEnabled),
                SwitchSetting(getString(R.string.enable_touch_id), getString(R.string.use_my_touch_id), "touch", AppConstants.settingSwitch.intKey, Prefs.isTouchEnabled),
                SwitchSetting("Google drive sync", "Backup profile to google Drive", "drive", AppConstants.settingSwitch.intKey, Prefs.isDriveActive),
                IntentSetting(getString(R.string.account_recovery), getString(R.string.back_up_your_credentials), "recovery", AppConstants.settingIntent.intKey),
                IntentSetting(getString(R.string.terms_and_conditions), getString(R.string.last_modified_23_july_2017), "terms", AppConstants.settingIntent.intKey),
                IntentSetting(getString(R.string.about_kimlic), "", "about", AppConstants.settingIntent.intKey))

        val passcodeChange = IntentSetting(getStringValue(R.string.change_passcode), "", "change", AppConstants.settingIntent.intKey)

        if (!Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag == "change")
            settingsList.removeAt(1)

        if (Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag != "change")
            settingsList.add(1, passcodeChange)
    }

    private fun backupProfile() {
        showProgress()
        recoveryModel.backupProfile(
                onSuccess = {
                    hideProgress()
                    Prefs.isDriveActive = true
                    showPopup("Success!", "Your profile synchronization is active")
                },
                onError = {
                    hideProgress()
                    showPopup("Error", "Synchronizing error")
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