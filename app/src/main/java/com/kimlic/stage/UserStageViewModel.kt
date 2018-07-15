package com.kimlic.stage

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.kimlic.db.KimlicDB
import com.kimlic.db.KimlicDB_Impl
import com.kimlic.db.User
import com.kimlic.preferences.Prefs

class UserStageViewModel : ViewModel() {

    init {
        Log.d("TAGVIEWMODEL", "initializing")
    }

    // Variables

    private var userLiveData = MutableLiveData<User>()

    // Live


    override fun onCleared() {
        super.onCleared()
    }

    // Public

    fun getUserLiveData(): LiveData<User> {
        return userLiveData//.value = KimlicDB.getInstance()!!.userDao().findById(Prefs.userId)

    }
}