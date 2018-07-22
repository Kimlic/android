package com.kimlic.stage

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.User
import com.kimlic.preferences.Prefs

class UserStageViewModel : ViewModel() {

    // Variables

    //private var userLiveData: LiveData<User>?
    private var risksLiveData: MutableLiveData<Boolean>?
    // New DB Imlementing

    private var userLiveData: LiveData<User>
    private var user1ContactLiveData: LiveData<List<Contact>>

    // Init

    init {
        //userLiveData = KimlicDB.getInstance()!!.userDao().findByIdLive(Prefs.currentId)
        risksLiveData = object : MutableLiveData<Boolean>() {}
        risksLiveData!!.setValue((Prefs.isPasscodeEnabled && Prefs.isTouchEnabled))

        // New DB implementation
        userLiveData = KimlicDB.getInstance()!!.userDao1().selectUserByIdLive(Prefs.currentId)
        user1ContactLiveData = KimlicDB.getInstance()!!.userDao1().selectUserContactsLive(Prefs.currentId)
    }

    // Live

    override fun onCleared() {
        //userLiveData = null
        risksLiveData = null
        super.onCleared()
    }

    // Public

//    fun getUserLiveData(): LiveData<User> {
//        return userLiveData!!
//    }

    // New DB implementation
    fun getUser1LiveData(): LiveData<User> {
        return userLiveData
    }

    fun getUserContactLiveData(): LiveData<List<Contact>> {
        return user1ContactLiveData
    }

    fun getRisksLiveData(): MutableLiveData<Boolean> {
        return risksLiveData!!
    }
}