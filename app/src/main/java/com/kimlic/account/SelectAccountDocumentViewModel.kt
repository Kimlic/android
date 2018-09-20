package com.kimlic.account

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.vendors.VendorsRepository

class SelectAccountDocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val vendorRepository = VendorsRepository.instance

    fun vendorsDocumentsLive() = vendorRepository.vendorDocumentsLive()
}