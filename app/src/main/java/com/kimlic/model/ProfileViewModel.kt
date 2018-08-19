package com.kimlic.model

import android.arch.lifecycle.*
import android.os.Handler
import com.kimlic.db.entity.Address
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.preferences.Prefs
import com.kimlic.vendors.VendorsRepository
import java.util.*

class ProfileViewModel : ViewModel(), LifecycleObserver {

    // Variables

    private var risksLiveData: MutableLiveData<Boolean>? = object : MutableLiveData<Boolean>() {}
    private var repository: ProfileRepository
    private var vendorsRepository: VendorsRepository
    private var timerQueue = ArrayDeque<Long>(listOf(2000L, 4000L))

    // Database

    init {
        risksLiveData!!.postValue(Prefs.isPasscodeEnabled && Prefs.isTouchEnabled)
        repository = ProfileRepository.instance
        vendorsRepository = VendorsRepository.instance
    }

    // Public

    // User

    fun addUserName(accountAddress: String, firstName: String, lastName: String) = repository.addUserName(accountAddress = accountAddress, firstName = firstName, lastName = lastName)

    fun updateUser(user: User) = repository.updateUser(user)

    fun user(accountAddress: String) = repository.getUser(accountAddress)

    fun deleteUser(accountAddress: String) = repository.deleteUser(accountAddress)

    fun userLive() = repository.userLive(accountAddress = Prefs.currentAccountAddress)

    fun deleteUserContact(accountAddress: String, contactType: String) = repository.contactDelete(accountAddress, contactType)

    fun addUserPortraitPhoto(data: ByteArray) {
        val accountAddress = Prefs.currentAccountAddress
        val fileName = UUID.nameUUIDFromBytes(data).toString()
        repository.addUserPhoto(accountAddress, fileName, data)
    }

    fun saveDocumentAndPhoto(documentType: String, portraitData: ByteArray, frontData: ByteArray, backData: ByteArray) {
        val portraitName: String = UUID.nameUUIDFromBytes(portraitData).toString()
        val frontName: String = UUID.nameUUIDFromBytes(frontData).toString()
        val backName: String = UUID.nameUUIDFromBytes(backData).toString()
        repository.addDocument(Prefs.currentAccountAddress, documentType, portraitName, portraitData, frontName, frontData, backName, backData)

    }

    // Address

    fun addUserAddress(value: String, data: ByteArray) {
        val fileName = UUID.nameUUIDFromBytes(data).toString()
        repository.addUserAddress(accountAddress = Prefs.currentAccountAddress, value = value, fileName = fileName, data = data)
    }

    fun updateUserAddress(address: Address) = repository.addressUpdate(address = address)

    fun deleteAddress(addressId: Long) = repository.addressDelete(addressId)

    fun userAddressLive() = repository.addressLive(Prefs.currentAccountAddress)

    //fun getUserAddress(accountAddress: String) = repository.address(accountAddress = accountAddress)

    fun userContactsLive() = repository.userContactsLive(Prefs.currentAccountAddress)

    // Documents

    fun userDocumentsLive() = repository.documentsLive(accountAddress = Prefs.currentAccountAddress)

    fun userDocuments() = repository.documents(accountAddress = Prefs.currentAccountAddress)

    fun userDocument(documentType: String) = repository.document(Prefs.currentAccountAddress, documentType = documentType)

    fun userDocumentPhotos(documentType: String) = repository.userDocumentPhotos(accountAddress = Prefs.currentAccountAddress, documentType = documentType)

    fun updateDocument(document: Document) = repository.updateDocument(document)

    fun deleteDocument(documentId: Long) = repository.documentDelete(documentId = documentId)

    // Contact

    fun addContact(accountAddress: String, contact: Contact) = repository.contactAdd(accountAddress, contact)

    //fun getUserAddressPhoto(accountAddress: String) = repository.userAddressPhoto(accountAddress = accountAddress)

    fun getRisksLiveData() = risksLiveData

    // Sync request

    fun syncProfile() = repository.syncProfile(accountAddress = Prefs.currentAccountAddress)

    // Quorum request

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun resetQueue() {
        timerQueue = ArrayDeque(listOf(2000L, 4000L))
    }

    fun quorumRequest(onSuccess: () -> Unit, onError: () -> Unit) {
        repository.quorumRequest(Prefs.currentAccountAddress, onSuccess, onError = { retryQuorumRequest(onSuccess, onError) })
    }

    private fun retryQuorumRequest(onSuccess: () -> Unit, onError: () -> Unit) = timerQueue.poll()?.let { Handler().postDelayed({ quorumRequest(onSuccess, onError) }, it) }
            ?: onError()

    fun senDoc(docType: String, country: String, url: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val countrySH = vendorsRepository.countries().filter { it.country == country }.first().sh.toUpperCase()
        repository.sendDoc(documentType = docType, countrySH = countrySH, onSuccess = onSuccess, onError = onError)//, dynamicUrl = "insert url fro qr Coed")
    }

    fun clearAllFiles() = repository.clearAllFiles()
}