package com.kimlic.vendors

import android.arch.lifecycle.*
import android.os.Handler
import com.kimlic.db.entity.Company
import com.kimlic.model.SingleLiveEvent
import com.kimlic.preferences.Prefs
import java.util.*

class VendorsViewModel : ViewModel(), LifecycleObserver {

    // Variables

    private val vendorsRepository = VendorsRepository.instance
    private var vendorRequestStatus: SingleLiveEvent<String> = SingleLiveEvent()
    private var companyDetails = MutableLiveData<Company>()
    private val timeQueueVendors = ArrayDeque<Long>(listOf(1500L, 2000L, 2000L))
    private val timeQueueCompanyDetails = ArrayDeque<Long>(listOf(1000L, 1000L, 1000L))

    // Public

    /*
    * Clear previous information about vendor documents
    * */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun clearVendors() = vendorsRepository.clearVendorsDocs()

    /*
    * Request to RP. Url gets from QR code. Repository saves Data to DB
    * */

    fun rpDocumentsRequest(url: String) = vendorsRepository.rpDocumentsRequest(Prefs.currentAccountAddress, url, onError = { retryVendorsRequest(url) })

    fun vendorsDocumentsLive() = vendorsRepository.vendorDocumentsLive()

    fun vendorsDocuments() = vendorsRepository.vendorDocuments()

    /*
    * Company information request
    * */

    fun rpDetailsRequest(url: String) {
        vendorsRepository.companyDetailsRequest(Prefs.currentAccountAddress, url,
                onSuccess = { company ->
                    companyDetails.postValue(company)
                }, onError = { retryCompanyRequest(url) })
    }

    fun rpDetailsLive() = companyDetails

    fun commonRequestStatus() = vendorRequestStatus

    fun countries() = vendorsRepository.countries()

    // Private

    private fun retryVendorsRequest(url: String) {
        timeQueueVendors.poll()?.let { Handler().postDelayed({ rpDocumentsRequest(url) }, it) } ?: vendorRequestStatus.postValue("server error")
    }

    private fun retryCompanyRequest(url: String) {
        timeQueueCompanyDetails.poll()?.let { Handler().postDelayed({ rpDocumentsRequest(url) }, it) } ?: vendorRequestStatus.postValue("server error")
    }
}