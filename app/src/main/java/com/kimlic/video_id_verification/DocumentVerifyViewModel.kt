package com.kimlic.video_id_verification

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log

class DocumentVerifyViewModel : ViewModel() {

    val documentType = MutableLiveData<String>()

    init {
        Log.d("TAGVIEWMODEL", "initializinng" + this.hashCode())
    }

    fun setDocumentType(documentType: String) {
        this.documentType.value = documentType
    }

}