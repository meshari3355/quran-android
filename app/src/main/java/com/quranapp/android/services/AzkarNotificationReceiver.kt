package com.quranapp.android.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.quranapp.android.MainActivity
import com.quranapp.android.R

/**
 * BroadcastReceiver for Azkar (remembrance) reminders.
 * Triggered by AlarmManager for morning and evening Azkar.
 */
class AzkarNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_AZKAR_TYPE = "azkar_type"
        const val TYPE_MORNING = "MORNING"
        const val TYPE_EVENING = "EVENING"
        const val CHANNEL_ID = NotificationChannels.AZKAR_REMINDERS
        const val NOTIFICATION_ID_BASE = 2000
    }

    override fun onReceive(context: Context, intent: Intent) {
        val azkarType = (intent.getStringExtra(EXTRA_AZKAR_TYPE) ?: TYPE_MORNING).uppercase()

        val title = when (azkarType) {
            TYPE_MORNING -> "أذكار الصباح"
            TYPE_EVENING -> "أذكار المساء"
            else -> "الأذكار"
        }

        val message = when (azkarType) {
            TYPE_MORNING -> "لا تنسَ أذكار الصباح 🌅"
            TYPE_EVENING -> "لا تنسَ أذكار المساء 🌙"
            else -> "حان وقت الأذكار"
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "azkar_main")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_BASE,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + azkarType.hashCode(), notification)
    }
}
