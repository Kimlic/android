package com.kimlic.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class KimlicMessagingService : FirebaseMessagingService() {

    // Variables

    // Live

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)


//        Thread(Runnable {
//            Looper.prepare()
//            val profileRepository = ProfileRepository.instance; profileRepository.syncProfile(Prefs.currentAccountAddress) }).start()


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("TAGFIREBASE", "From: ${remoteMessage?.getFrom()}");

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
}