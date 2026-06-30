package com.quranapp.android.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.quranapp.android.MainActivity
import com.quranapp.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver for prayer time notifications.
 * Triggered by AlarmManager at scheduled prayer times.
 */
class PrayerNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_PRAYER_TIME = "prayer_time"
        const val EXTRA_KIND = "notification_kind"
        const val EXTRA_MINUTES_BEFORE = "minutes_before"
        const val CHANNEL_ID = NotificationChannels.PRAYER_TIMES
        const val NOTIFICATION_ID_BASE = 1000
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""
        val kind = intent.getStringExtra(EXTRA_KIND) ?: "time"
        val minutesBefore = intent.getIntExtra(EXTRA_MINUTES_BEFORE, 5)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "prayer_times")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_BASE,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (kind) {
            "before" -> "اقترب وقت صلاة $prayerName"
            "iqama" -> "حان وقت إقامة صلاة $prayerName"
            else -> "حان وقت صلاة $prayerName"
        }
        val message = when (kind) {
            "before" -> "باقي $minutesBefore دقائق على صلاة $prayerName - $prayerTime"
            "iqama" -> "مرّت 15 دقيقة على أذان $prayerName."
            else -> "حان الآن وقت صلاة $prayerName - $prayerTime"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + "$prayerName:$kind".hashCode(), notification)

        if (kind == "iqama") {
            reschedulePrayerTimes(context)
        }
    }

    private fun reschedulePrayerTimes(context: Context) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                ReminderScheduler.schedulePrayerTimes(context.applicationContext)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
