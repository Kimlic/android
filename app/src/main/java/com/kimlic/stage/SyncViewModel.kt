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

/*
* View model class is used to synchronize company status information.
* Implementing LifecycleObserver interface. This class applied as observer to StageActivity, AccountsStageFragment
* */

class SyncViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    // Life

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun runCompanySyncService() {
        Log.d("TAGSYNCSERVICE", "in on resume in service")
        if (!ServiceUtil.isMyServiceRunning(getApplication(), CompanyDetailsSyncService::class.java.simpleName) ) {//&& Prefs.needCompanySyncCount > 0
            val syncIntent = Intent(getApplication(), CompanyDetailsSyncService::class.java)
            getApplication<KimlicApp>().startService(syncIntent)
        }
    }


    fun runDocumentStatusSyncRequest(){

    }
}