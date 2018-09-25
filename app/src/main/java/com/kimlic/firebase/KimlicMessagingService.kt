package com.kimlic.firebase

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kimlic.model.ProfileRepository
import com.kimlic.preferences.Prefs

class KimlicMessagingService : FirebaseMessagingService() {

    // Variables

    private var profileRepository: ProfileRepository? = ProfileRepository.instance

    // Live

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val handler = Handler(Looper.getMainLooper())
        handler.post { ProfileRepository.instance.syncProfile(Prefs.currentAccountAddress) }

        // Check if message contains a data payload.
        if (remoteMessage?.getData()?.size != 0) {
            Log.d("TAGFIREBASE", "Message data payload: ${remoteMessage?.getData()}");

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage?.getNotification() != null) {
            Log.d("TAGFIREBASE", "Message Notification Body: " + remoteMessage.notification?.body);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onDestroy() {
        super.onDestroy()
        profileRepository = null
    }
}