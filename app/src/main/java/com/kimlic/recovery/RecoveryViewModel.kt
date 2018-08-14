package com.kimlic.recovery

import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService

class RecoveryViewModel : ViewModel() {

    // Variables
    private val TAG = this::class.java.simpleName
    private val syncService: SyncService = SyncService.getInstance()
    private var googleSignInAccount: GoogleSignInAccount? = null
    private var db: KimlicDB? = null

    // Public

    fun retrievePhoto(accountAddress: String) {
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())

        googleSignInAccount?.let {
            Handler().postDelayed({
                syncService.retrievePhotos(accountAddress = accountAddress)
            }, 0)
        }
    }

    init {
        db = KimlicDB.getInstance()
    }

    override fun onCleared() {
        db = null
        super.onCleared()
    }

    fun retrieveDatabase(accountAddress: String, onSuccess: () -> Unit, onError: () -> Unit) {
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())
        googleSignInAccount?.let {
            db!!.close()

            if (!db!!.isOpen) {
                Handler().postDelayed({
                    syncService.retrieveDataBase(accountAddress = accountAddress, dataBaseName = "kimlic.db", onSuccess = {
                        onSuccess()
//                        KimlicDB.getInstance()
                        Log.d(TAG, "Database restored successfully")
                    }, onError = onError)
                }, 10)
            }
        }
    }

}