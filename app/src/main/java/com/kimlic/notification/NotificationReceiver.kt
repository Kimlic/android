//package com.kimlic.notification
//import android.app.AlarmManager
//import android.app.Notification
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import com.kimlic.R
//import java.util.*
//
//
//class NotificationReceiver : BroadcastReceiver() {
//
//    companion object {
//        var NOTIFICATION_ID = "notification-id"
//        var NOTIFICATION = "notification"
//
//        fun scheduleTimeNotification(date: Date, prayer: Prayer, ctx: Context) {
//            val builder = Notification.Builder(ctx)
//            builder.setContentTitle("Scheduled Notification")
//            builder.setContentText("Notification")
//            builder.setSmallIcon(R.mipmap.ic_launcher)
//            val notification = builder.build()
//
//            val notificationIntent = Intent(ctx, NotificationReceiver::class.java)
//            notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1)
//            notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification)
//            val pendingIntent = PendingIntent.getBroadcast(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            alarmManager.set(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
//        }
//    }
//
//    override fun onReceive(ctx: Context?, intent: Intent?) {
//        val i = Intent(ctx!!.getApplicationContext(), AlertDialogActivity::class.java)
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        ctx.startActivity(i)
////    val notificationManager = ctx!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////
////    val notification = intent!!.getParcelableExtra<Parcelable>(NOTIFICATION) as Notification
////    val id = intent.getIntExtra(NOTIFICATION_ID, 0)
////    notificationManager.notify(id, notification)
////
////    Toast.makeText(ctx, "Alarm went off", Toast.LENGTH_LONG).show()
//    }
//}