package com.kimlic.recovery

import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kimlic.KimlicApp
import com.kimlic.db.KimlicDB
import com.kimlic.db.SyncService
import com.kimlic.model.ProfileRepository

class RecoveryViewModel : ViewModel() {

    // Variables

    private val TAG = this::class.java.simpleName

    private val syncService: SyncService = SyncService.getInstance()
    private var googleSignInAccount: GoogleSignInAccount? = null
    private var db: KimlicDB? = null
    private var repository: ProfileRepository = ProfileRepository.instance

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

    fun initNewUserRegistration(onSuccess: () -> Unit, onError: () -> Unit) {
        repository.initNewUserRegistration(onSuccess, onError)
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
                    syncService.retrieveDataBase(accountAddress, "kimlic.db",
                            onSuccess = { onSuccess(); Log.d(TAG, "Database restored successfully") },
                            onError = onError)
                }, 10)
            }
        }
    }
}