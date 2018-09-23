package com.kimlic.settings

import com.kimlic.utils.AppConstants

open class Setting(val title: String, val summary: String? = null, val tag: String, val type: Int, var state: Boolean = true)

class SwitchSetting(title: String, summary: String, tag: String, state: Boolean) : Setting(title, summary, tag, AppConstants.SETTINGS_SWITCH.intKey, state)

class IntentSetting(title: String, summary: String, tag: String) : Setting(title, summary, tag, AppConstants.SETTINGS_INTENT.intKey)