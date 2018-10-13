package com.kimlic.service

import android.app.ActivityManager
import android.content.Context

class ServiceUtil {

    companion object {

        fun isMyServiceRunning(context: Context, serviceName: String): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName == service.service.className) {
                    return true
                }
            }
            return false
        }
    }
}