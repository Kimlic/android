package com.kimlic.recovery

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Handler
import android.util.Log
import com.kimlic.API.DoAsync
import com.kimlic.KimlicApp
import com.kimlic.R
import com.kimlic.db.KimlicDB
import com.kimlic.model.ProfileRepository
import com.kimlic.preferences.Prefs
import com.kimlic.quorum.QuorumKimlic
import java.io.File
import java.util.*

class RecoveryViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    // Variables

    private var profileRepository: ProfileRepository = ProfileRepository.instance
    private var recoveryRepository: RecoveryRepository = RecoveryRepository.instance

    private lateinit var photoQueue: ArrayDeque<File>
    private lateinit var photoRetrieveQueue: ArrayDeque<String>

    private var timerQueue = ArrayDeque<Long>(listOf(2000L, 4000L))

    // Public

    fun initNewUserRegistration(onSuccess: () -> Unit, onError: () -> Unit) {
        profileRepository.initNewUserRegistration(onSuccess, onError)
    }

    // Recovery

    fun recoveryProfile(mnemonic: String, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        photoRetrieveQueue = ArrayDeque()

        QuorumKimlic.destroyInstance()
        val quorumKimlic = QuorumKimlic.createInstance(mnemonic, getApplication())
        val accountAddress = quorumKimlic.walletAddress

        recoveryRepository.recoveryDataBase(accountAddress,
                onSuccess = {
                    recoveryRepository.allPhotos(accountAddress).forEach { photoRetrieveQueue.add(it) }
                    recoveryPhoto(accountAddress,
                            onSuccess = {
                                Prefs.authenticated = true
                                Prefs.currentAccountAddress = accountAddress
                                Prefs.isRecoveryEnabled = true
                                KimlicDB.getInstance()
                                quorumRequest(onSuccess = { onSuccess() }, onError = { onError(getApplication<KimlicApp>().getString(R.string.server_error)) })
                            },
                            onError = { onError(getApplication<KimlicApp>().getString(R.string.photo_recovery_error)) })
                }, onError = { onError(getApplication<KimlicApp>().getString(R.string.profile_not_found)) })
    }

    private fun recoveryPhoto(accountAddress: String, onSuccess: () -> Unit, onError: () -> Unit) {
        photoRetrieveQueue.poll()?.let {
            DoAsync().execute(Runnable {
                recoveryRepository.recoveryPhoto(accountAddress, it, { recoveryPhoto(accountAddress, onSuccess, onError) }, onError)
            })
        } ?: onSuccess()
    }

    // Backup profile

    fun backupProfile(onSuccess: () -> Unit, onError: () -> Unit) {
        Log.d("TAGBACKUP", "in backup")
        photoQueue = ArrayDeque()
        val rootFilesDir = File(getApplication<KimlicApp>().filesDir.toString())
        val files = rootFilesDir.listFiles()

        files.filter { !it.isDirectory }.forEach { photoQueue.add(it) }
        Log.d("TAGBACKUPPHOTOS", "photos = $photoQueue")
        recoveryRepository.backupDatabase(Prefs.currentAccountAddress, onSuccess = {
            Log.d("TAGBACKUPPHOTOS", "photos = onSuccess backupDatabase")
            backupPhotos(Prefs.currentAccountAddress, onSuccess, onError)
        }, onError = { onError })
    }

    private fun backupPhotos(accountAddress: String, onSuccess: () -> Unit, onError: () -> Unit) {
        photoQueue.poll()?.let {
            //DoAsync().execute(Runnable {
            recoveryRepository.backupPhoto(accountAddress, it.absolutePath.toString(), { backupPhotos(accountAddress, onSuccess, onError) }, onError)
            //})
        } ?: onSuccess()
    }

    // Remove profile

    fun removeProfile(onSuccess: () -> Unit, onError: () -> Unit) {
        // Need to remove profile

        recoveryRepository.removeProfile(Prefs.currentAccountAddress, onSuccess = onSuccess, onError = onError)
    }

    // Quorum request helpers

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun resetQueue() {
        timerQueue = ArrayDeque(listOf(2000L, 4000L))
    }

    private fun quorumRequest(onSuccess: () -> Unit, onError: () -> Unit) {
        profileRepository.quorumRequest(Prefs.currentAccountAddress, onSuccess, onError = { retryQuorumRequest(onSuccess, onError) })
    }

    private fun retryQuorumRequest(onSuccess: () -> Unit, onError: () -> Unit) = timerQueue.poll()?.let { Handler().postDelayed({ quorumRequest(onSuccess, onError) }, it) }
            ?: onError()

    fun allPhotos() = recoveryRepository.allPhotos(accountAddress = Prefs.currentAccountAddress)
}