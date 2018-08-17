package com.kimlic.vendors

import android.arch.lifecycle.*
import android.util.Log
import com.kimlic.db.entity.Document
import com.kimlic.model.ProfileRepository
import com.kimlic.preferences.Prefs

class VendorsViewModel : ViewModel(), LifecycleObserver {

    // Variables

    private val vendorsRepository = VendorsRepository.instance
    private val profileRepository = ProfileRepository.instance

    private var documentsForAdapter = object : MutableLiveData<List<Document>>() {}

    // public

    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    fun getDocumentsList() = vendorsRepository.initDocuments(accountAddress = Prefs.currentAccountAddress)


    fun getDocumentsForAdapter() = documentsForAdapter// List Document which are supported in chosen country

    //fun progress() = progressLiveData

    fun supportedDocuments(country: String) {
        val userDocuments = profileRepository.documents(Prefs.currentAccountAddress)
        val linkedDocs = userDocuments.map { it.type to it }.toMap()

        // Documents are supported in chosen country
        val supportedDocuments = mutableListOf<Document>()
        val vendorsDocuments = vendorsRepository.vendorDocuments()

        vendorsDocuments.forEach { document ->
            if (document.countries.contains(country.toUpperCase())) {
                when (document.type) {
                    "ID_CARD" -> supportedDocuments.add(Document(type = "id"))
                    "PASSPORT" -> supportedDocuments.add(Document(type = "passport"))
                    "DRIVERS_LICENSE" -> supportedDocuments.add(Document(type = "license"))
                    "RESIDENCE_PERMIT_CARD" -> supportedDocuments.add(Document(type = "permit"))
                }
            }
        }
        // Document user already have added
        documentsForAdapter.postValue(supportedDocuments)
    }

    fun countries() = vendorsRepository.countries()
}