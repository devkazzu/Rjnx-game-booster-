package com.example.utils

import android.app.NotificationManager
import android.content.Context

class NotificationCleanerManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun isDndPermissionGranted(): Boolean {
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun enableGamingDnd() {
        if (isDndPermissionGranted()) {
            try {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            } catch (_: Exception) {}
        }
    }

    fun disableGamingDnd() {
        if (isDndPermissionGranted()) {
            try {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            } catch (_: Exception) {}
        }
    }
}
