package com.kimlic.documents

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.model.ProfileRepository
import com.kimlic.preferences.Prefs
import com.kimlic.vendors.VendorsRepository

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    // Variables

    private val repository = VendorsRepository.instance
    private val profileRepository = ProfileRepository.instance

    // Public

    fun countries() = repository.countries()

    fun documents() = profileRepository.documentsLive(Prefs.currentAccountAddress)
}