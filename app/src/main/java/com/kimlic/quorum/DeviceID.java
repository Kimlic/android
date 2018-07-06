package com.kimlic.quorum;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class DeviceID {

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public synchronized static String id(Context context) {
        if (uniqueID != null)
            return uniqueID;

        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

        if (uniqueID != null)
            return uniqueID;

        uniqueID = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREF_UNIQUE_ID, uniqueID);
        editor.apply();

        return uniqueID;
    }
}
