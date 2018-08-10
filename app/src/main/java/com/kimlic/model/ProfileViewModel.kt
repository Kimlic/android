package com.kimlic.model

import android.arch.lifecycle.*
import android.util.Log
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs
import com.kimlic.utils.UserPhotos

class ProfileViewModel : ViewModel(), LifecycleObserver {

    // Variables

    private var risksLiveData: MutableLiveData<Boolean>?
    private var repository: ProfileRepository

    // Database

   // private var db: KimlicDB

    init {
       // db = KimlicDB.getInstance()!!
        risksLiveData = object : MutableLiveData<Boolean>() {}
        risksLiveData!!.value = (Prefs.isPasscodeEnabled && Prefs.isTouchEnabled)
        repository = ProfileRepository.instance
    }

    //Life

    override fun onCleared() {
        //repository.syncDataBase_()
        super.onCleared()

    }

    // Publick

    fun insertUser(user: User) = repository.insertUser(user)

    fun getUser(accountAddress: String) = repository.getUser(accountAddress)

    fun dropUser(accountAddres: String) = repository.deleteUser(accountAddres)

    fun addUserContact(accountAddress: String, contact: Contact) = repository.contactAdd(accountAddress, contact)

    fun deleteUserContact(accountAddress: String, contactType: String) = repository.contactDelete(accountAddress, contactType)

    fun addUserPortraitPhoto(data: ByteArray) {
        val accountAddress = Prefs.currentAccountAddress
        val fileName = accountAddress + "_" + UserPhotos.stagePortrait.fileName
        repository.addUserPhoto(accountAddress, fileName, data)
    }

    fun saveDocumentAndPhoto(documentType: String, portraitData: ByteArray, frontData: ByteArray, backData: ByteArray) {
        val prefix = Prefs.currentAccountAddress + "_"
        var portritName: String = ""
        var frontName: String = ""
        var backName: String = ""

        when (documentType) {
            "passport" -> {
                portritName = prefix + UserPhotos.passportPortrait.fileName; frontName = prefix + UserPhotos.passportFront.fileName; backName = prefix + UserPhotos.passportBack.fileName
            }
            "id" -> {
                portritName = prefix + UserPhotos.idPortrait.fileName; frontName = prefix + UserPhotos.idFront.fileName; backName = prefix + UserPhotos.idBack.fileName
            }
            "license" -> {
                portritName = prefix + UserPhotos.licensePortrait.fileName; frontName = prefix + UserPhotos.licensFront.fileName; backName = prefix + UserPhotos.licensBack.fileName
            }
            "permit" -> {
                portritName = prefix + UserPhotos.permitPortrait.fileName; frontName = prefix + UserPhotos.permitFront.fileName; backName = prefix + UserPhotos.permitBack.fileName
            }
        }
        repository.addDocument(Prefs.currentAccountAddress, documentType, portritName, portraitData, frontName, frontData, backName, backData)
    }

    fun addUserAddress(value: String, data: ByteArray) {
        val fileName = Prefs.currentAccountAddress + "_" + UserPhotos.bill.fileName
        repository.addUserAddress_(accountAddress = Prefs.currentAccountAddress, value = value, fileName = fileName, data = data)
    }

    fun updateUserAddress(address: Address) = repository.addressUpdate(address = address)

    fun deleteAddres(addressId: Long) = repository.addressDelete(addressId)

    fun getUserLive() = repository.userLive(accountAddress = Prefs.currentAccountAddress)

    fun getUserAddressesLive(accountAddress: String) = repository.addressLive(accountAddress = accountAddress)

    fun getUserAddress(accountAddress: String) = repository.address(accountAddress = accountAddress)

    fun getUserContactsLive(accountAddress: String) = repository.userContactsLive(accountAddress)

    fun getUserDocumentsLive() = repository.documentsLive(accountAddress = Prefs.currentAccountAddress)

    fun getUserDocumentPhotos(accountAddress: String, documentType: String) = repository.userDocumentPhotos(accountAddress = accountAddress, documentType = documentType)

    fun getUserAddressPhoto(accountAddress: String) = repository.userAddressPhoto(accountAddress = accountAddress)

    //fun addUserDocument(accountAddress: String, document: Document): Long = repository.documentAdd(accountAddress = accountAddress, document = document)

    fun addUserName(accountAddress: String, firstName: String, lastName: String) = repository.addUserName(accountAddress = accountAddress, firstName = firstName, lastName = lastName)

    fun deleteDocument(documentId: Long) = repository.documentDelete(documentId = documentId)

    //fun addDocumentPhoto(vararg photos: Photo) = repository.addDocumentPhoto(photos = *photos)

    fun getRisksLiveData() = risksLiveData

    // SyncRequest

    fun syncProfile(accountAddress: String) = repository.syncProfile(accountAddress = accountAddress)

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onStageActivityPause() {
        Log.d("TAG", "Lifecycle components on Lifecycle PAUSE!!!!")
        repository.syncDataBaseonPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onActivitiResume() {
        Log.d("TAG", "Lifecycle components on Lifecycle RESUME!!!")
    }

}