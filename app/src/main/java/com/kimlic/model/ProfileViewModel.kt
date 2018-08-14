package com.kimlic.model

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kimlic.db.entity.Address
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.preferences.Prefs
import java.util.*

class ProfileViewModel : ViewModel(), LifecycleObserver {

    // Variables

    private var risksLiveData: MutableLiveData<Boolean>? = object : MutableLiveData<Boolean>() {}
    private var repository: ProfileRepository

    // Database


    init {
        risksLiveData!!.value = (Prefs.isPasscodeEnabled && Prefs.isTouchEnabled)
        repository = ProfileRepository.instance
    }

    // Publick

    // User

    fun insertUser(user: User) = repository.insertUser(user)

    fun addUserName(accountAddress: String, firstName: String, lastName: String) = repository.addUserName(accountAddress = accountAddress, firstName = firstName, lastName = lastName)

    fun getUser(accountAddress: String) = repository.getUser(accountAddress)

    fun deleteUser(accountAddres: String) = repository.deleteUser(accountAddres)

    fun getUserLive() = repository.userLive(accountAddress = Prefs.currentAccountAddress)

    fun deleteUserContact(accountAddress: String, contactType: String) = repository.contactDelete(accountAddress, contactType)

    fun addUserPortraitPhoto(data: ByteArray) {
        val accountAddress = Prefs.currentAccountAddress
        val fileName = UUID.nameUUIDFromBytes(data).toString()
        repository.addUserPhoto(accountAddress, fileName, data)
    }

    fun saveDocumentAndPhoto(documentType: String, portraitData: ByteArray, frontData: ByteArray, backData: ByteArray) {
        val portritName: String = UUID.nameUUIDFromBytes(portraitData).toString()
        val frontName: String = UUID.nameUUIDFromBytes(frontData).toString()
        val backName: String = UUID.nameUUIDFromBytes(backData).toString()
        repository.addDocument(Prefs.currentAccountAddress, documentType, portritName, portraitData, frontName, frontData, backName, backData)

    }

    // Address

    fun addUserAddress(value: String, data: ByteArray) {
        val fileName = UUID.nameUUIDFromBytes(data).toString()
        repository.addUserAddress(accountAddress = Prefs.currentAccountAddress, value = value, fileName = fileName, data = data)
    }

    fun updateUserAddress(address: Address) = repository.addressUpdate(address = address)

    fun deleteAddres(addressId: Long) = repository.addressDelete(addressId)

    fun getUserAddressesLive(accountAddress: String) = repository.addressLive(accountAddress = accountAddress)

    //fun getUserAddress(accountAddress: String) = repository.address(accountAddress = accountAddress)

    fun getUserContactsLive(accountAddress: String) = repository.userContactsLive(accountAddress)

    // Documents

    fun getUserDocumentsLive() = repository.documentsLive(accountAddress = Prefs.currentAccountAddress)

    fun getUserDocuments() = repository.documents(accountAddress = Prefs.currentAccountAddress)

    fun getUserDocument(documentType: String) = repository.document(Prefs.currentAccountAddress, documentType = documentType)

    fun getUserDocumentPhotos(documentType: String) = repository.userDocumentPhotos(accountAddress = Prefs.currentAccountAddress, documentType = documentType)

    fun updateDocument(document: Document) = repository.updateDocument(document)

    fun deleteDocument(documentId: Long) = repository.documentDelete(documentId = documentId)

    // Contact

    fun addContact(accountAddress: String, contact: Contact) = repository.contactAdd(accountAddress, contact)

    //fun getUserAddressPhoto(accountAddress: String) = repository.userAddressPhoto(accountAddress = accountAddress)

    //fun addUserDocument(accountAddress: String, document: Document_): Long = repository.documentAdd(accountAddress = accountAddress, document = document)


    //fun addDocumentPhoto(vararg photos: Photo) = repository.addDocumentPhoto(photos = *photos)

    fun getRisksLiveData() = risksLiveData

    // SyncRequest

    fun syncProfile(accountAddress: String) = repository.syncProfile(accountAddress = accountAddress)

    fun senDoc(docType: String, country: String, url: String, onSuccess: () -> Unit, onError: () -> Unit) {
//        repository.sendDoc(docType, onSuccess = onSuccess, onError = onError)
//        repository.mySendoc_(docType, onSuccess = onSuccess, onError = onError)
        repository.sendDoc(documentType = docType, country = country, onSuccess = onSuccess, onError = onError)//, dynamicUrl = )
    }

    fun clearAllFiles() = repository.clearAllFiles()
}