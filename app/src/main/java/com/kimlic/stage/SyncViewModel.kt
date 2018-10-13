package com.kimlic.stage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import com.kimlic.KimlicApp
import com.kimlic.preferences.Prefs
import com.kimlic.service.CompanyDetailsSyncService
import com.kimlic.service.ServiceUtil

class SyncViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    // Life

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun runCompanySyncService() {
        if (!ServiceUtil.isMyServiceRunning(getApplication(), CompanyDetailsSyncService::class.java.simpleName) && Prefs.needCompanySyncCount > 0) {
            val syncIntent = Intent(getApplication(), CompanyDetailsSyncService::class.java)
            getApplication<KimlicApp>().startService(syncIntent)
        }
    }
}