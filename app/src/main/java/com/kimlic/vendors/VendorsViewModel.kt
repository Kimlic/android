package com.kimlic.vendors

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import com.kimlic.db.entity.Document
import com.kimlic.model.ProfileRepository
import com.kimlic.model.SingleLiveEvent
import com.kimlic.preferences.Prefs
import com.kimlic.utils.AppDoc
import java.util.*

class VendorsViewModel : ViewModel() {

    // Variables

    private val vendorsRepository = VendorsRepository.instance
    private val profileRepository = ProfileRepository.instance

    private var vendorSupportedDocuments = object : MutableLiveData<List<Document>>() {}
    private var vendorRequestStatus: SingleLiveEvent<String> = SingleLiveEvent()
    private val timeQueue = ArrayDeque<Long>(listOf(4000L, 5000L, 10000L))

    // Public

    fun documentsListRequest(url: String) = vendorsRepository.initDocumentsRequest(Prefs.currentAccountAddress, url = url, onError = { retry(url) })

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

        vendorSupportedDocuments.postValue(supportedDocuments)
    }

    fun countries() = vendorsRepository.countries()

    // Private

    private fun retry(url: String) {
        timeQueue.poll()?.let { Handler().postDelayed({ documentsListRequest(url) }, it) } ?: vendorRequestStatus.postValue("server error")
    }
}