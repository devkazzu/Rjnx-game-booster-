package com.example.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.example.data.TelemetryBus
import com.example.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamingAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            scope.launch {
                val db = AppDatabase.getDatabase(applicationContext)
                val game = db.gameDao().getGameByPackage(packageName)
                if (game != null && !game.isHidden) {
                    TelemetryBus.setActiveGame(game.packageName, game.title)
                    GamingForegroundService.start(applicationContext)
                }
            }
        }
    }

    override fun onInterrupt() {}
}
