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

    //private var documentsLiveData = object : MutableLiveData<Vendors>() {}
    private var documentsForAdapter = object : MutableLiveData<List<Document>>() {}
    //private var progressLiveData: SingleLiveEvent<Boolean> = object : SingleLiveEvent<Boolean>() {}
    //private var responseObject: Vendors? = null

    //private val vendorDocumentsLive = object : MutableLiveData<List<Document_>>() {}

    // public


    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    fun getDocumentslist() {
        vendorsRepository.initDocuments(accountAddress = Prefs.currentAccountAddress)
        Log.d("TAGVENDOR", "in repository on start")
    }

//    fun getVendorsDocuments(): LiveData<List<VendorDocument>> {
//
//        return documentsLiveData.value.documents
//    }

    fun getDocumentsForAdapter() = documentsForAdapter// List Document which are supported in chosen country

    //fun progress() = progressLiveData

    fun getSupportedDocuments_PostThemToAdaptersResult(country: String) {
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

    fun countriesList() = vendorsRepository.countries()
}