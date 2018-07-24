package com.kimlic.stage

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.Address
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.User
import com.kimlic.preferences.Prefs

class UserStageViewModel : ViewModel() {

    // Variables

    private var risksLiveData: MutableLiveData<Boolean>?
    private var userLiveData: LiveData<User>?
    private var userContactsLiveData: LiveData<List<Contact>>
    private var addressLiveData: LiveData<Address>

    // Init

    init {
        risksLiveData = object : MutableLiveData<Boolean>() {}
        risksLiveData!!.setValue((Prefs.isPasscodeEnabled && Prefs.isTouchEnabled))
        userLiveData = KimlicDB.getInstance()!!.userDao().selectUserLive(Prefs.currentId)
        userContactsLiveData = KimlicDB.getInstance()!!.contactDao().selectLive(userId = Prefs.currentId)
        addressLiveData = KimlicDB.getInstance()!!.addressDao().selectLive(userId = Prefs.currentId)
    }

    // Live

    override fun onCleared() {
        userLiveData = null
        risksLiveData = null
        super.onCleared()
    }

    // Public

    fun getUserLiveData(): LiveData<User> {
        return userLiveData!!
    }

    fun getUserContactsLiveData(): LiveData<List<Contact>> {
        return userContactsLiveData
    }

    fun getRisksLiveData(): MutableLiveData<Boolean> {
        return risksLiveData!!
    }

    fun getAddressLiveData(): LiveData<Address> {
        return addressLiveData
    }
}