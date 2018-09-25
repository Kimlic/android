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
    private var vendorRequestStatus: SingleLiveEvent<String> = SingleLiveEvent()
    private val timeQueue = ArrayDeque<Long>(listOf(1500L, 2000L, 2000L))

    // Public

    /*
    * Clear previous information about vendor documents
    * */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun clearVendors() = vendorsRepository.clearVendorsDocs()

    /*
    * Request to RP. Url gets from QR code. Repository saves Data to DB
    * */

    fun rpDocuments(url: String) = vendorsRepository.rpDocumentsRequest(Prefs.currentAccountAddress, url, onError = { retryRpRequest(url) })

    fun vendorsDocumentsToVerify() = vendorsRepository.vendorDocumentsLive()

    fun vendorRequestStatus() = vendorRequestStatus

    fun countries() = vendorsRepository.countries()

    // Private

    private fun retryRpRequest(url: String) {
        timeQueue.poll()?.let { Handler().postDelayed({ rpDocuments(url) }, it) } ?: vendorRequestStatus.postValue("server error")
    }
}