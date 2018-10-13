package com.kimlic.stage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.util.Log
import com.kimlic.KimlicApp
import com.kimlic.preferences.Prefs
import com.kimlic.service.CompanyDetailsSyncService
import com.kimlic.service.ServiceUtil

class SyncViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    // Life

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun runCompanySyncService() {
        Log.d("TAGSYNCVIEWMODEL", "sync inside!!!!!!!!!!!!!!!!! in onResume")

        if (!ServiceUtil.isMyServiceRunning(getApplication(), CompanyDetailsSyncService::class.java.simpleName) && Prefs.needCompanySync) {
            Log.d("TAGSYNCVIEWMODEL", "sync inside if")
            val syncIntent = Intent(getApplication(), CompanyDetailsSyncService::class.java)
            getApplication<KimlicApp>().startService(syncIntent)
        }
    }
}