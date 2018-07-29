package com.kimlic

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs

class ProfileViewModel : ViewModel() {

    // Variables

    private var risksLiveData: MutableLiveData<Boolean>?

    // Database

    private var db: KimlicDB

    init {
        db = KimlicDB.getInstance()!!

        risksLiveData = object : MutableLiveData<Boolean>() {}
        risksLiveData!!.value = (Prefs.isPasscodeEnabled && Prefs.isTouchEnabled)
    }

    //Life

    override fun onCleared() {
        super.onCleared()
    }

    // Publick

    fun insertUser(user: User) {
        db.userDao().insert(user)
    }

    fun getUser(accountAddress: String): User {
        return db.userDao().select(accountAddress = accountAddress)
    }

    fun dropUser(accountAddres: String) {
        db.userDao().delete(accountAddres)
    }

    fun addUserContact(accountAddress: String, contact: Contact) {
        val user = db.userDao().select(accountAddress)
        val userId = user.id
        contact.userId = userId
        db.contactDao().insert(contact)
    }

    fun dropUserContact(accountAddress: String, contactType: String){
        db.contactDao().drop(accountAddress = accountAddress, type = contactType)
    }

    fun addUserPhoto(accountAddress: String, fileName: String) {
        val user = db.userDao().select(accountAddress = accountAddress)
        user.portraitFile = fileName
        db.userDao().update(user = user)
    }

    fun addUserAddress(accountAddress: String, address: Address) {
        val user = db.userDao().select(accountAddress)
        val userId = user.id
        address.userId = userId
        db.addressDao().insert(address = address)
    }

    fun getUserLive(accountAddress: String) = db.userDao().selectLive(accountAddress = accountAddress)

    fun getUserAddressesLive(accountAddress: String) = db.addressDao().selectLive(accountAddress)

    fun getUserContactsLive(accountAddress: String) = db.contactDao().selectLive(accountAddress = accountAddress)

    fun getUserDocumentsLive(accountAddress: String) = db.documentDao().selectLive(accountAddress = accountAddress)

    fun addUserDocument(accountAddress: String, document: Document): Long {
        val user = db.userDao().select(accountAddress = accountAddress)
        val userId = user.id
        document.userId = userId
        return db.documentDao().insert(document)
    }

    fun addUserName(accountAddress: String, firstName: String, lastName: String) {
        val user = db.userDao().select(accountAddress = accountAddress)
        user.firstName = firstName
        user.lastName = lastName
        db.userDao().update(user)
    }

    fun dropDocument(documentId: Long) {
        db.documentDao().delete(id = documentId)
    }

    fun addPhotosForDocument(vararg photos: Photo) {
        db.photoDao().insert(photos.asList())
    }

    fun getRisksLiveData() = risksLiveData

}