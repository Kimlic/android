package com.kimlic.recovery

import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncServise

class RecoveryViewModel : ViewModel() {

    // Variables
    private val TAG = this::class.java.simpleName
    private val syncService: SyncServise = SyncServise.getInstance(appFolder = false)
    private var googleSignInAccount: GoogleSignInAccount? = null
    private var db: KimlicDB? = null

    // Public

    fun retrievePhoto(accountAddres: String) {
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())

        googleSignInAccount?.let {
            Handler().postDelayed({
                syncService.retrievePhotos(accountAddress = accountAddres, appFolder = false)
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
                    syncService.retriveDataBase(accountAddress = accountAddress, dataBaseName = "kimlic.db", onSuccess = {
                        onSuccess()
//                        KimlicDB.getInstance()
                        Log.d(TAG, "Database restored successfully")
                    }, onError = onError)
                }, 10)
            }
        }
    }

}