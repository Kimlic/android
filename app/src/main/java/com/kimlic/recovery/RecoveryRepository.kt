package com.kimlic.recovery

import android.annotation.SuppressLint
import com.kimlic.API.DoAsync
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService

class RecoveryRepository {

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = RecoveryRepository()
    }

    // Companion

    companion object {
        val instance: RecoveryRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Holder.INSTANCE }
    }

    // Variables

    private var db: KimlicDB? = null

    // Init

    init {
        db = KimlicDB.getInstance()
    }

    // Public

    fun allPhotos(accountAddress: String) = db!!.photoDao().profilePhotos(accountAddress)

    fun recoveryDataBase(accountAddress: String, onSuccess: () -> Unit, onError: () -> Unit) {
        DoAsync().execute(Runnable {
            db!!.close()
            SyncService.getInstance().retrieveDataBase(accountAddress, "kimlic.db", onSuccess = onSuccess, onError = onError)
        })
    }

    fun recoveryPhoto(accountAddress: String, fileName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        DoAsync().execute(Runnable { SyncService.getInstance().retrieveFile(accountAddress, fileName, onSuccess, onError) })
    }

    fun backupDatabase(accountAddress: String, onSuccess: () -> Unit) {
        DoAsync().execute(Runnable { SyncService.getInstance().backupDatabase(accountAddress, "kimlic.db", onSuccess = onSuccess) })
    }

    fun backupPhoto(accountAddress: String, filePath: String, onSuccess: () -> Unit, onError: () -> Unit) {
        DoAsync().execute(Runnable { SyncService.getInstance().backupFile(accountAddress = accountAddress, filePath = filePath, fileDescription = SyncService.DATABASE_DECRIPTION, onSuccess = onSuccess, onError = onError) })
    }
}