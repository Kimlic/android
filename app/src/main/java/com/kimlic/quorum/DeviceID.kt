package com.kimlic.quorum

import android.content.Context
import java.util.*

object DeviceID {

    // Constants

    private const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

    // Variables

    private var uniqueID: String? = null

    // Public

    @Synchronized
    fun id(context: Context): String? {
        if (uniqueID != null)
            return uniqueID

        val sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE)
        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)

        if (uniqueID != null)
            return uniqueID

        uniqueID = UUID.randomUUID().toString()
        val editor = sharedPrefs.edit()
        editor.putString(PREF_UNIQUE_ID, uniqueID)
        editor.apply()

        return uniqueID
    }
}
