package com.kimlic.settings

open class Setting(val title: String, val summary: String? = null, val tag: String, val type: Int, var state: Boolean = true)

class SwitchSetting(title: String, summary: String, tag: String, type: Int, state: Boolean) : Setting(title, summary, tag, type, state)

class IntentSetting(title: String, summary: String, tag: String, type: Int) : Setting(title = title, tag = tag, type = type, summary = summary)