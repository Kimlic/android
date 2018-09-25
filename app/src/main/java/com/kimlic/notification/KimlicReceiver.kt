//package com.kimlic.notification
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//
//class KimlicReceiver : BroadcastReceiver(){
//
//    // Constants
//
//    private val channeId = "kimlic.notification.chanel"
//
//    override fun onReceive(context: Context?, intent: Intent?) {
//
//
//        Log.d("TAGRECEIVER", "messagr recived!!!!")
//    }
//
//
////    fun sendNotification() {
////
////        val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
////        val actionIntent = Intent(this, SettingsActivity::class.java)
////        val actionPendingIntent = PendingIntent.getActivity(this, 0, actionIntent, 0)
////
////        val taskStackBuilder = TaskStackBuilder.create(this)
//////        taskStackBuilder.addParentStack() // Add Activity class to srtack
////
////        val builder = NotificationCompat.Builder(this, chanel_id)
////                .setContentTitle("This is notification")
////                .setContentText("This is content text")
////                .setSmallIcon(R.drawable.ic_doc_icon)
////                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                .setAutoCancel(true)
////                .setContentIntent(actionPendingIntent)
////                .setSound(alarmSoundUri)
////                .setChannelId(chanel_id)
////
////
////        val notification = builder.build()
////
////
////        val manager = KimlicApp.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        manager.notify(1, notification)
////
////    }
////
////
////    private fun createNotificationChannel() {
////        val chanel_id = "kimlic.notification.chanel"
////
////        // Create the NotificationChannel, but only on API 26+ because
////        // the NotificationChannel class is new and not in the support library
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////
////            val name = chanel_id//getString(R.string.channel_name)
////            val description = "Chanel description"
////            val importance = NotificationManager.IMPORTANCE_DEFAULT
////
////            val channel = NotificationChannel(chanel_id, name, importance)
////
////            channel.description = description
////            // Register the channel with the system; you can't change the importance
////            // or other notification behaviors after this
////            val notificationManager = getSystemService(NotificationManager::class.java)
////            notificationManager!!.createNotificationChannel(channel)
////        }
////    }
//
//}