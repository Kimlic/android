//package com.kimlic.firebase
//
//import android.util.Log
//import com.google.firebase.iid.FirebaseInstanceId
//import com.google.firebase.iid.FirebaseInstanceIdService
//
//class Firebase : FirebaseInstanceIdService() {
//
//    override fun onTokenRefresh() {
//        val refreshedToken = FirebaseInstanceId.getInstance().token
//        Log.d(LOG_TAG, "Refreshed token: " + refreshedToken!!)
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        //    sendRegistrationToServer(refreshedToken);
//    }
//
//    companion object {
//
//        private val LOG_TAG = Firebase::class.java.simpleName
//    }
//}
