package com.kimlic.vendors

import android.arch.lifecycle.*
import android.os.Handler
import com.kimlic.db.entity.Document
import com.kimlic.model.ProfileRepository
import com.kimlic.model.SingleLiveEvent
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppDoc
import java.util.*

class VendorsViewModel : ViewModel(), LifecycleObserver {

    // Variables

    private val vendorsRepository = VendorsRepository.instance
    private val profileRepository = ProfileRepository.instance

    private var vendorSupportedDocuments = object : MutableLiveData<List<Document>>() {}
    private var vendorRequestStatus: SingleLiveEvent<String> = SingleLiveEvent()
    private val timeQueue = ArrayDeque<Long>(listOf(4000L, 5000L, 10000L))

    // public

    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    fun getDocumentsList() = vendorsRepository.initDocuments(Prefs.currentAccountAddress, onError = { retry() })

    private fun retry() {
        timeQueue.poll()?.let { Handler().postDelayed({ getDocumentsList() }, it) } ?: vendorRequestStatus.postValue("server error")
    }

    fun vendorRequestStatus() = vendorRequestStatus

    fun getDocumentsForAdapter() = vendorSupportedDocuments// List Document which are supported in chosen country

    fun supportedDocuments(country: String) {
        val userDocuments = profileRepository.documents(Prefs.currentAccountAddress)
        val linkedDocs = userDocuments.map { it.type to it }.toMap()

        // Documents are supported in chosen country
        val supportedDocuments = mutableListOf<Document>()
        val vendorsDocuments = vendorsRepository.vendorDocuments()

        vendorsDocuments.forEach { document ->
            if (document.countries.contains(country.toUpperCase())) {
                when (document.type) {
                    "ID_CARD" -> supportedDocuments.add(Document(type = AppDoc.ID_CARD.type))
                    "PASSPORT" -> supportedDocuments.add(Document(type = AppDoc.PASSPORT.type))
                    "DRIVERS_LICENSE" -> supportedDocuments.add(Document(type = AppDoc.DRIVERS_LICENSE.type))
                    "RESIDENCE_PERMIT_CARD" -> supportedDocuments.add(Document(type = AppDoc.RESIDENCE_PERMIT_CARD.type))
                }
            }
        }
        // Document user already have added
        vendorSupportedDocuments.postValue(supportedDocuments)
    }

    fun countries() = vendorsRepository.countries()
}