package com.kimlic.settings

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import butterknife.ButterKnife
import com.kimlic.BaseActivity
import com.kimlic.R
import com.kimlic.managers.PresentationManager
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppConstants
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    // Variables

    private lateinit var settingsList: MutableList<Setting>
    private val adapter: SettingsAdapter

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

    // Private

    private fun setupUI() {
        setupAdapter()
        signoutBt.setOnClickListener {
            Prefs.clear()
            PresentationManager.signupRecovery(this)
        }
        deleteBt.setOnClickListener { showToast("delete id Clicked") }
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
                        if (Prefs.isTouchEnabled) PresentationManager.touchDisable(this@SettingsActivity)
                        else PresentationManager.touchCreate(this@SettingsActivity)
                    }
                    "recovery" ->
                        // Vill be changed
                        if (!Prefs.isRecoveryEnabled) PresentationManager.recoveryEnable(this@SettingsActivity)
                //else PresentationManager.recoveryEnable(this@SettingsActivity)

                    "terms" -> PresentationManager.termsReview(this@SettingsActivity)
                    "privacy" -> PresentationManager.privacyReview(this@SettingsActivity)
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
                SwitchSetting(getString(R.string.passcode), getString(R.string.protect_your_kimlic), "passcode", AppConstants.settingSwitch.intKey, Prefs.isPasscodeEnabled),
                SwitchSetting(getString(R.string.enable_touch_id), getString(R.string.gets_access_accounts), "touch", AppConstants.settingSwitch.intKey, Prefs.isTouchEnabled),
                SwitchSetting(getString(R.string.account_recovery), getString(R.string.backed_up_kimlic_id), "recovery", AppConstants.settingSwitch.intKey, Prefs.isRecoveryEnabled),
                IntentSetting(getString(R.string.terms_and_conditions), "terms", AppConstants.settingIntent.intKey),
                IntentSetting(getString(R.string.privacy_policy), "privacy", AppConstants.settingIntent.intKey))

        val passcodeChange = IntentSetting(getStringValue(R.string.change_passcode), "change", AppConstants.settingIntent.intKey)

        if (!Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag == "change")
            settingsList.removeAt(1)

        if (Prefs.isPasscodeEnabled && settingsList.elementAt(1).tag != "change")
            settingsList.add(1, passcodeChange)
    }
}

