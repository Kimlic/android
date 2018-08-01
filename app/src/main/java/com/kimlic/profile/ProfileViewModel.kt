package com.kimlic.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.*
import com.kimlic.preferences.Prefs

class ProfileViewModel : ViewModel() {

    // Variables

    private var risksLiveData: MutableLiveData<Boolean>?
    private var repository: ProfileRepository

    // Database

    private var db: KimlicDB


    init {
        db = KimlicDB.getInstance()!!

        risksLiveData = object : MutableLiveData<Boolean>() {}
        risksLiveData!!.value = (Prefs.isPasscodeEnabled && Prefs.isTouchEnabled)

        repository = ProfileRepository.instance
    }

    //Life

    override fun onCleared() {
        super.onCleared()
    }

    // Publick

    fun insertUser(user: User) = repository.insertUser(user)

    fun getUser(accountAddress: String) = repository.getUser(accountAddress)

    fun dropUser(accountAddres: String) = repository.deleteUser(accountAddres)

    fun addUserContact(accountAddress: String, contact: Contact) = repository.contactAdd(accountAddress, contact)

    fun deleteUserContact(accountAddress: String, contactType: String) = repository.contactDelete(accountAddress, contactType)

    fun addUserPhoto(accountAddress: String, fileName: String) = repository.addUserPhoto(accountAddress, fileName)

    fun addUserPhotoPreview(accountAddress: String, fileName: String) = repository.addUserPhotoPreview(accountAddress, fileName)

    fun addUserAddress(accountAddress: String, address: Address): Long = repository.addUserAddress(accountAddress, address)

    fun updateUserAddress(address: Address) = repository.addressUpdate(address = address)

    fun deleteAddres(addressId: Long) = repository.addressDelete(addressId)

    fun getUserLive(accountAddress: String) = repository.userLive(accountAddress = accountAddress)

    fun getUserAddressesLive(accountAddress: String) = repository.addressLive(accountAddress = accountAddress)

    fun getUserAddress(accountAddress: String) = repository.address(accountAddress = accountAddress)

    fun getUserContactsLive(accountAddress: String) = repository.userContactsLive(accountAddress)

    fun getUserDocumentsLive(accountAddress: String) = repository.documentsLive(accountAddress = accountAddress)

    fun getUserDocumentPhotos(accountAddress: String, documentType: String) = repository.userDocumentPhotos(accountAddress = accountAddress, documentType = documentType)

    fun getUserAddressPhoto(accountAddress: String) = repository.userAddressPhoto(accountAddress = accountAddress)

    fun addUserDocument(accountAddress: String, document: Document): Long = repository.documentAdd(accountAddress = accountAddress, document = document)

    fun addUserName(accountAddress: String, firstName: String, lastName: String) = repository.addUserName(accountAddress = accountAddress, firstName = firstName, lastName = lastName)

    fun dropDocument(documentId: Long) = repository.documentDelete(documentId = documentId)

    fun addDocumentPhoto(vararg photos: Photo) = repository.addDocumentPhoto(photos = *photos)

    fun getRisksLiveData() = risksLiveData

}