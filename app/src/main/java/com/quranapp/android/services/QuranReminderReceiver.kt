package com.quranapp.android.services

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.quranapp.android.MainActivity
import com.quranapp.android.R
import com.quranapp.android.data.local.PreferencesManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * BroadcastReceiver for daily Quran reading reminders.
 * Encourages the user to maintain their daily Quran reading habit.
 */
class QuranReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = NotificationChannels.QURAN_REMINDERS
        const val NOTIFICATION_ID = 3000
        private const val REQUEST_CODE = 3000
    }

    override fun onReceive(context: Context, intent: Intent) {
        val preferencesManager = PreferencesManager(context)
        val daysSinceLastReading = daysSince(preferencesManager.getLastReadingTimestamp())
        scheduleNextReminder(context)

        if (daysSinceLastReading < 1) return

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "quran_reader")
            putExtra("surah_id", preferencesManager.getLastReadSurah())
            putExtra("page", preferencesManager.getLastReadPage())
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when {
            daysSinceLastReading >= 10 -> "عد إلى وردك القرآني"
            daysSinceLastReading >= 5 -> "مرّت $daysSinceLastReading أيام بلا قراءة"
            else -> "تذكير بقراءة القرآن"
        }
        val message = when {
            daysSinceLastReading >= 10 -> "افتح آخر موضع قراءة وابدأ بخطوة صغيرة اليوم."
            daysSinceLastReading >= 5 -> "آخر قراءة كانت قبل $daysSinceLastReading أيام. ورد قصير يكفي للعودة."
            else -> "مرّ أكثر من 24 ساعة على آخر قراءة. لا تنسَ وردك اليومي."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun daysSince(lastReadingTimestamp: Long): Long {
        if (lastReadingTimestamp <= 0L) return 1L
        val now = System.currentTimeMillis()
        return TimeUnit.MILLISECONDS.toDays(now - lastReadingTimestamp).coerceAtLeast(0L)
    }

    private fun scheduleNextReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val reminderIntent = Intent(context, QuranReminderReceiver::class.java).apply {
            action = "com.quranapp.android.QURAN_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}
