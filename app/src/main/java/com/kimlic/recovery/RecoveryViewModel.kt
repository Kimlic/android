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
    private val syncService: SyncServise = SyncServise.getInstance()
    private var googleSignInAccount: GoogleSignInAccount? = null
    private var db: KimlicDB? = null

    // Public

    fun retrivePhoto(accountAddres: String) {
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())

        googleSignInAccount?.let {
            Handler().postDelayed({
                syncService.retrivePhotos(accountAddress = accountAddres, appFolder = false)
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

    fun retriveDatabase(accountAddress: String, onSuccess: () -> Unit) {
        db!!.openHelper.close()
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(KimlicApp.applicationContext())
        googleSignInAccount?.let {
            db!!.close()

            if (!db!!.isOpen) {
                Log.d(TAG, "Database is closed - " + db!!.isOpen)
                Handler().postDelayed({
                    syncService.retriveDataBase(accountAddress = accountAddress, dataBaseName = "kimlic.db", appFolder = false, onSuccess = {
                        onSuccess()
                        KimlicDB.getInstance()
                        Log.d(TAG, "Database restored successfully")
                    })
                }, 10000)
            }
        }
    }

}