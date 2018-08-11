package com.kimlic.settings

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.model.ProfileViewModel
import com.kimlic.passcode.PasscodeActivity
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File

class SettingsActivity : BaseActivity() {

    // Constants

    private val PASSCODE_REQUEST_CODE = 42

    // Variables

    private lateinit var settingsList: MutableList<Setting>
    private val adapter: SettingsAdapter
    private lateinit var model: ProfileViewModel

    // Init

    init {
        adapter = SettingsAdapter()
    }

    // Life

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
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
                if (resultCode == Activity.RESULT_OK) {
                    PresentationManager.touchCreate(this@SettingsActivity)
                }
            }
        }
    }

    // Private

    private fun setupUI() {
        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        setupAdapter()
        signoutBt.setOnClickListener {
            model.dropUser(accountAddres = Prefs.currentAccountAddress)
            Prefs.clear()
            clearAllFiles()
            QuorumKimlic.destroyInstance()
            PresentationManager.signupRecovery(this)
        }
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
                IntentSetting(getString(R.string.account_recovery), getString(R.string.back_up_your_credentials), "recovery", AppConstants.settingIntent.intKey),
                IntentSetting(getString(R.string.terms_and_conditions), getString(R.string.last_modified_23_july_2017), "terms", AppConstants.settingIntent.intKey),
                IntentSetting(getString(R.string.about_kimlic), "", "about", AppConstants.settingIntent.intKey))

        val passcodeChange = IntentSetting(getStringValue(R.string.change_passcode), "", "change", AppConstants.settingIntent.intKey)

        if (!Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag == "change")
            settingsList.removeAt(1)

        if (Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag != "change")
            settingsList.add(1, passcodeChange)
    }

    private fun passcodeForResult() {
        val intent = Intent(this, PasscodeActivity::class.java)
        intent.putExtra("action", "set")
        startActivityForResult(intent, PASSCODE_REQUEST_CODE)
    }

    private fun clearAllFiles() {
        val rootFilesDir = File(filesDir.toString())
        val files = rootFilesDir.listFiles()
        files.forEach { it.delete() }
    }
}

