package com.kimlic.documents

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.kimlic.db.KimlicDB
import com.kimlic.db.entity.Document
import com.kimlic.preferences.Prefs

class DocumentVerifyChooseViewModel : ViewModel() {

    // Variables

    private val documentLiveData: LiveData<List<Document>> = KimlicDB.getInstance()!!.documentDao().selectByUserIdLive(Prefs.currentId)
    private val user = KimlicDB.getInstance()!!.userDao().select(Prefs.currentId)

    // Public

    fun getDocumentsLiveData(): LiveData<List<Document>> {
        return documentLiveData
    }

    fun getUser() = user

}