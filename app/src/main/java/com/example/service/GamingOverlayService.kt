package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.TelemetryBus
import com.example.data.preferences.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Floating, draggable in-game HUD.
 *
 * It is built from plain Android views rather than a `ComposeView`: a view attached directly to
 * [WindowManager] has no `ViewTreeLifecycleOwner` / `SavedStateRegistryOwner`, which is exactly
 * why hosting Compose here used to render an empty (invisible) panel.
 */
class GamingOverlayService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var renderJob: Job? = null

    companion object {
        const val CHANNEL_ID = "rjnx_overlay_channel"
        const val NOTIFICATION_ID = 1002

        /** @return false when the caller first needs the "display over other apps" permission. */
        fun start(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                return false
            }
            val intent = Intent(context, GamingOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            return true
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, GamingOverlayService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!canDrawOverlays()) {
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(NOTIFICATION_ID, buildNotification())
        if (overlayView == null) {
            createOverlayView()
        }
        observeTelemetry()
        return START_STICKY
    }

    private fun canDrawOverlays(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

    private fun createOverlayView() {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 24
            y = 160
        }

        val panel = buildPanel(params)
        overlayView = panel
        try {
            windowManager?.addView(panel, params)
        } catch (_: Exception) {
            // Permission revoked at runtime or the window token is not usable: nothing to draw.
            stopSelf()
        }
    }

    private fun buildPanel(params: WindowManager.LayoutParams): LinearLayout {
        val density = resources.displayMetrics.density
        fun Int.dp(): Int = (this * density).toInt()

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12.dp(), 8.dp(), 12.dp(), 8.dp())
            background = GradientDrawable().apply {
                setColor(0xE605060A.toInt())
                cornerRadius = 14f * density
                setStroke(1.dp(), 0x66FF0055)
            }
            addView(hudText("RJNX // LIVE HUD", 0xFFFF0055.toInt(), 9f, bold = true))
            addView(hudText("-- FPS", 0xFF00F0FF.toInt(), 15f, bold = true, id = R.id.overlay_fps))
            addView(
                hudText("CPU --%   RAM --%", 0xFFF0F4FC.toInt(), 9f, bold = false, id = R.id.overlay_cpu_ram)
            )
            addView(
                hudText("TEMP --C   PING --ms", 0xFF8A94B2.toInt(), 9f, bold = false, id = R.id.overlay_net)
            )
            attachDragHandling(this, params)
        }
    }

    private fun hudText(
        text: String,
        color: Int,
        sizeSp: Float,
        bold: Boolean,
        id: Int = View.NO_ID
    ): TextView = TextView(this).apply {
        this.text = text
        this.id = id
        setTextColor(color)
        includeFontPadding = false
        setTypeface(
            Typeface.MONOSPACE,
            if (bold) Typeface.BOLD else Typeface.NORMAL
        )
        setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = if (bold) 0 else (2 * resources.displayMetrics.density).toInt()
        }
    }

    private fun attachDragHandling(view: View, params: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var touchX = 0f
        var touchY = 0f
        var dragged = false

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    dragged = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - touchX).toInt()
                    params.y = initialY + (event.rawY - touchY).toInt()
                    dragged = true
                    try {
                        windowManager?.updateViewLayout(view, params)
                    } catch (_: Exception) {
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (dragged) persistOverlayPosition(params.x, params.y)
                    true
                }
                else -> false
            }
        }
    }

    private fun persistOverlayPosition(x: Int, y: Int) {
        serviceScope.launch { DataStoreManager(applicationContext).saveOverlayPosition(x, y) }
    }

    private fun observeTelemetry() {
        if (renderJob?.isActive == true) return
        renderJob = serviceScope.launch {
            TelemetryBus.stats
                .distinctUntilChanged()
                .collect { stats ->
                    val root = overlayView ?: return@collect
                    root.findViewById<TextView>(R.id.overlay_fps)?.text =
                        "%d FPS".format(stats.currentFps)
                    root.findViewById<TextView>(R.id.overlay_cpu_ram)?.text =
                        "CPU %d%%   RAM %d%%".format(stats.cpuUsagePercent, stats.ramUsagePercent)
                    root.findViewById<TextView>(R.id.overlay_net)?.text =
                        "TEMP %.1f C   PING %d ms".format(stats.batteryTempC, stats.pingMs)
                }
        }
    }

    private fun buildNotification(): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.overlay_notification_title))
            .setContentText(getString(R.string.overlay_notification_text))
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentIntent(openAppIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.overlay_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.overlay_channel_description)
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_SECRET
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        renderJob?.cancel()
        serviceScope.cancel()
        overlayView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (_: Exception) {
            }
        }
        overlayView = null
        windowManager = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
