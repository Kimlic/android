package com.kimlic.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.kimlic.KimlicApp
import com.kimlic.utils.AppConstants

open class BasePreferences(context: Context) {

    private val TAG = this.javaClass.simpleName!!
    private val mSharedPreferences: SharedPreferences
    private val mEditor: SharedPreferences.Editor

    init {
        Log.d(TAG, "initializing")
        mSharedPreferences = KimlicApp.applicationContext().getSharedPreferences(AppConstants.appPreferences.key, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()
    }

    fun setInt(key: String, value: Int) = mEditor.putInt(key, value).apply()
    fun getInt(key: String): Int = mSharedPreferences.getInt(key, -1)

    fun setString(key: String, value: String) = mEditor.putString(key, value).apply()
    fun getString(key: String) = mSharedPreferences.getString(key, "")

    fun setLong(key: String, value: Long) = mEditor.putLong(key, value).apply()
    fun getLong(key: String) = mSharedPreferences.getLong(key, -1L)

    fun setBoolean(key: String, value: Boolean) = mEditor.putBoolean(key, value).apply()
    fun getBoolean(key: String) = mSharedPreferences.getBoolean(key, false)

}