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
 * BroadcastReceiver for Friday Surah Al-Kahf reminder.
 * Triggered every Friday to remind the user to read Surah Al-Kahf.
 */
class FridayKahfReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = NotificationChannels.QURAN_REMINDERS
        const val NOTIFICATION_ID = 4000
        const val SURAH_KAHF_ID = 18
        const val SURAH_KAHF_PAGE = 293
    }

    override fun onReceive(context: Context, intent: Intent) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "quran_reader")
            putExtra("surah_id", SURAH_KAHF_ID)
            putExtra("page", SURAH_KAHF_PAGE)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("يوم الجمعة - سورة الكهف")
            .setContentText("لا تنسَ قراءة سورة الكهف في هذا اليوم المبارك")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
