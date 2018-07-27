package com.kimlic.stage

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs

class UserStageViewModel : ViewModel() {

    // Variables

    private var risksLiveData: MutableLiveData<Boolean>?
    private var userLiveData: LiveData<User>?
    private var userContactsLiveData: LiveData<List<Contact>>
    private var addressLiveData: LiveData<Address>
    private var documentsLiveData: LiveData<List<Document>>

    // Init

    init {
        risksLiveData = object : MutableLiveData<Boolean>() {}
        risksLiveData!!.setValue((Prefs.isPasscodeEnabled && Prefs.isTouchEnabled))
        userLiveData = KimlicDB.getInstance()!!.userDao().selectLive(Prefs.currentId)
        userContactsLiveData = KimlicDB.getInstance()!!.contactDao().selectLive(userId = Prefs.currentId)
        addressLiveData = KimlicDB.getInstance()!!.addressDao().selectLive(userId = Prefs.currentId)
        documentsLiveData = KimlicDB.getInstance()!!.documentDao().selectByUserIdLive(Prefs.currentId)
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

    fun getDocumentsLiveData(): LiveData<List<Document>> {
        return documentsLiveData
    }
}