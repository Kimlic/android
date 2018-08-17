package com.kimlic.phone

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.model.ProfileRepository
import com.kimlic.vendors.VendorsRepository

class PhoneViewModel(application: Application) : AndroidViewModel(application) {

    // Variables

    private val vendorRepository: VendorsRepository = VendorsRepository.instance
    private val profileRepository: ProfileRepository = ProfileRepository.instance

    // Public

    fun verify(phone: String, onSuccess: () -> Unit, onError: () -> Unit) {
        profileRepository.phoneVerify(phone, onSuccess, onError)
    }

    fun countries() = vendorRepository.countries()
}