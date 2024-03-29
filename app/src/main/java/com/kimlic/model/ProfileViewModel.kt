package com.kimlic.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Handler
import com.kimlic.db.entity.Address
import com.kimlic.db.entity.Contact
import com.kimlic.db.entity.Document
import com.kimlic.db.entity.User
import com.kimlic.documents.DocState
import com.kimlic.preferences.Prefs
import com.kimlic.vendors.VendorsRepository
import java.util.*

class ProfileViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    // Variables

    private var repository: ProfileRepository = ProfileRepository.instance
    private var vendorsRepository: VendorsRepository = VendorsRepository.instance
    private var timerQueue = ArrayDeque<Long>(listOf(2000L, 4000L))

    // Public

    // User

    fun updateUserName(firstName: String, lastName: String) = repository.addUserName(Prefs.currentAccountAddress, firstName, lastName)

    fun updateUser(user: User) = repository.updateUser(user)

    fun user() = repository.getUser(Prefs.currentAccountAddress)

    fun deleteUser(accountAddress: String) = repository.deleteUser(accountAddress)

    fun userLive() = repository.userLive(Prefs.currentAccountAddress)

    fun deleteUserContact(accountAddress: String, contactType: String) = repository.contactDelete(accountAddress, contactType)

    fun addUserPortraitPhoto(data: ByteArray) {
        val accountAddress = Prefs.currentAccountAddress
        val fileName = UUID.nameUUIDFromBytes(data).toString()
        repository.addUserPhoto(accountAddress, fileName, data)
    }

    fun saveDocumentAndPhoto(documentType: String, country: String, portraitData: ByteArray, frontData: ByteArray, backData: ByteArray) {
        val portraitName: String = UUID.nameUUIDFromBytes(portraitData).toString()
        val frontName: String = UUID.nameUUIDFromBytes(frontData).toString()
        val backName: String = UUID.nameUUIDFromBytes(backData).toString()
        val countryIso = ""//vendorsRepository.countries().filter { it.country == country }.first().sh.toUpperCase()
        repository.addDocument(Prefs.currentAccountAddress, documentType, country, countryIso, portraitName, portraitData, frontName, frontData, backName, backData)

    }

    fun userPortraitPhotos() = repository.portraitPhotosLive(Prefs.currentAccountAddress)

    // Tokens

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun tokensBalance() = repository.tokenBalanceRequest(Prefs.currentAccountAddress)

    // Address

    fun addUserAddress(value: String, data: ByteArray) {
        val fileName = UUID.nameUUIDFromBytes(data).toString()
        repository.addUserAddress(Prefs.currentAccountAddress, value, fileName, data)
    }

    fun updateUserAddress(address: Address) = repository.addressUpdate(address)

    fun deleteAddress(addressId: Long) = repository.addressDelete(addressId)

    fun userAddressLive() = repository.addressLive(Prefs.currentAccountAddress)

    //fun getUserAddress(accountAddress: String) = repository.address(accountAddress = accountAddress)

    fun userContactsLive() = repository.userContactsLive(Prefs.currentAccountAddress)

    fun userContacts() = repository.userContacts(Prefs.currentAccountAddress)

    // Documents

    fun userDocumentsLive() = repository.documentsLive(accountAddress = Prefs.currentAccountAddress)

    fun userDocuments() = repository.documents(accountAddress = Prefs.currentAccountAddress)

    fun userDocument(documentType: String) = repository.document(Prefs.currentAccountAddress, documentType = documentType)

    fun userDocumentPhotos(documentType: String) = repository.userDocumentPhotos(Prefs.currentAccountAddress, documentType)

    fun updateDocument(document: Document) = repository.updateDocument(document)

    fun deleteDocument(documentId: Long) = repository.documentDelete(documentId = documentId)

    //fun states() = repository.documentStates(Prefs.currentAccountAddress)

    fun hasDocumentInProgress(): Boolean {
        val states = repository.documentStates(Prefs.currentAccountAddress)
        return states.contains(DocState.CREATED.state) || states.contains(DocState.VERIFIED.state)
    }

    // Contact

    fun addContact(accountAddress: String, contact: Contact) = repository.contactAdd(accountAddress, contact)

    //fun getUserAddressPhoto(accountAddress: String) = repository.userAddressPhoto(accountAddress = accountAddress)

    // Sync request

    fun syncProfile() = repository.syncProfile(Prefs.currentAccountAddress)

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
        val countrySH = vendorsRepository.countries().first { it.country == country }.sh.toUpperCase()
        repository.sendDoc(documentType = docType, countrySH = countrySH, onSuccess = onSuccess, onError = onError, url = url)
    }

    // Backup

    fun clearAllFiles() = repository.clearAllFiles()
}