package com.kimlic.email

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.kimlic.model.ProfileRepository

class EmailViewModel(application: Application) : AndroidViewModel(application) {

    // Variables

    private val repository = ProfileRepository.instance

    // Public

    fun emailVerify(email: String, onSuccess: () -> Unit, onError: () -> Unit) {
        repository.contactVerify("email", email, onSuccess, onError)
    }

    fun emailApprove(code: String, onSuccess: () -> Unit, onError: (code: String) -> Unit) {
        repository.contactApprove("email", code, onSuccess, onError)
    }
}