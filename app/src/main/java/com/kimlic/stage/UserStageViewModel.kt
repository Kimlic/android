package com.kimlic.stage

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.kimlic.db.KimlicDB
import com.kimlic.db.User
import com.kimlic.preferences.Prefs

class UserStageViewModel : ViewModel() {

    // Variables

    private var userLiveData: LiveData<User>?

    init {
        Log.d("TAGVIEWMODEL", "view model initializeng")
        userLiveData = KimlicDB.getInstance()!!.userDao().findByIdLive(Prefs.userId)
    }

    // Live

    override fun onCleared() {
        // Clear resources
        super.onCleared()
        userLiveData = null
    }

    // Public

    fun getUserLiveData(): LiveData<User> {
        return userLiveData!!
    }
}