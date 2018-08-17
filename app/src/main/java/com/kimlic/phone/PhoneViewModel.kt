package com.kimlic.phone

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.model.ProfileRepository
import com.kimlic.vendors.VendorsRepository

class PhoneViewModel(application: Application) : AndroidViewModel(application) {

    // Variables

    private val vendorRepository: VendorsRepository = VendorsRepository.instance
    private val repository: ProfileRepository = ProfileRepository.instance

    // Public

    fun verify(phone: String, onSuccess: () -> Unit, onError: () -> Unit) {
        repository.contactVerify("phone", phone, onSuccess, onError)
    }

    fun emailApprove(code: String, onSuccess: () -> Unit, onError: (code: String) -> Unit) {
        repository.contactApprove("phone", code, onSuccess, onError)
    }

    fun countries() = vendorRepository.countries()
}