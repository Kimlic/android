package com.kimlic.service

import android.app.IntentService
import android.content.Intent
import com.kimlic.db.KimlicDB

class DocumentStatusSyncService: IntentService(DocumentStatusSyncService::class.simpleName) {


    // VAriables

    private var dataBase: KimlicDB? = null


    override fun onHandleIntent(intent: Intent?) {

    }
}