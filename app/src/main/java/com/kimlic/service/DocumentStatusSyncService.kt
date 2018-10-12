package com.kimlic.service

import android.app.IntentService
import android.content.Intent
import com.kimlic.API.KimlicApi
import com.kimlic.BuildConfig
import com.kimlic.db.KimlicDB
import com.kimlic.db.dao.DocumentDao
import com.kimlic.db.entity.Document
import com.kimlic.preferences.Prefs

class DocumentStatusSyncService : IntentService(DocumentStatusSyncService::class.java.simpleName) {

    // Constants

    val API_URL = BuildConfig.API_CORE_URL

    // Variables

    private var dataBase: KimlicDB? = null
    private lateinit var documentDao: DocumentDao
    private lateinit var unverifiedDocList: List<Document>

    // Live

    override fun onCreate() {
        super.onCreate()

        dataBase = KimlicDB.getInstance()
        documentDao = dataBase!!.documentDao()
    }

    override fun onHandleIntent(intent: Intent?) {

    }

    // Private

    private fun recursiveQueueRequest() {
        val url = API_URL + KimlicApi.PROFILE_SYNC.path
        val headers = mapOf(Pair("account-address", Prefs.currentAccountAddress), Pair("accept", "application/vnd.mobile-api.v1+json"), Pair("Content-Type", "application/json"))

    }
}