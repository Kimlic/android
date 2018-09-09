package com.kimlic.documents

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.vendors.VendorsRepository

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    // Variables

    private val repository = VendorsRepository.instance

    // Public

    fun countries() = repository.countries()
}