package com.quranapp.android.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.quranapp.android.widgets.PrayerTimesWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

// ===== Notification IDs =====

object NotificationIds {
    const val PRAYER_TIMES = 1001
    const val MORNING_AZKAR = 1002
    const val EVENING_AZKAR = 1003
    const val QURAN_REMINDER = 1004
    const val FRIDAY_KAHF = 1005
}

// ===== Notification Channels =====

object NotificationChannels {
    const val PRAYER_TIMES = "prayer_notifications"
    const val AZKAR_REMINDERS = "azkar_notifications"
    const val QURAN_REMINDERS = "general_notifications"
    const val DOWNLOADS = "download_notifications"
}

// ===== Notification Data Models =====

data class PrayerNotification(
    val prayerName: String,
    val prayerTime: String,
    val cityName: String? = null
)

data class AzkarNotification(
    val title: String,
    val content: String,
    val type: AzkarType
)

enum class AzkarType {
    MORNING, EVENING
}

data class QuranReminderNotification(
    val title: String,
    val content: String,
    val lastPage: Int? = null
)

// ===== Notification Service =====

class NotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannels()
    }

    // ===== Channel Creation =====

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Prayer Times Channel
            val prayerChannel = android.app.NotificationChannel(
                NotificationChannels.PRAYER_TIMES,
                "مواقيت الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات وقت الصلاة والتنبيه قبلها"
                enableVibration(true)
                setShowBadge(true)
            }

            // Azkar Reminders Channel
            val azkarChannel = android.app.NotificationChannel(
                NotificationChannels.AZKAR_REMINDERS,
                "تذكير الأذكار",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تذكير بأذكار الصباح والمساء"
                enableVibration(true)
            }

            // Quran Reading Reminders Channel
            val quranChannel = android.app.NotificationChannel(
                NotificationChannels.QURAN_REMINDERS,
                "تذكير قراءة القرآن",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تذكير حسب آخر قراءة للقرآن"
                enableVibration(false)
            }

            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(azkarChannel)
            notificationManager.createNotificationChannel(quranChannel)
        }
    }

    // ===== Show Notifications =====

    fun showPrayerNotification(notification: PrayerNotification) {
        val contentText = notification.cityName?.let { city ->
            "${notification.prayerTime} in $city"
        } ?: notification.prayerTime

        val notif = NotificationCompat.Builder(context, NotificationChannels.PRAYER_TIMES)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("${notification.prayerName} Prayer")
            .setContentText(contentText)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(NotificationIds.PRAYER_TIMES, notif)
    }

    fun showAzkarNotification(notification: AzkarNotification) {
        val notificationId = when (notification.type) {
            AzkarType.MORNING -> NotificationIds.MORNING_AZKAR
            AzkarType.EVENING -> NotificationIds.EVENING_AZKAR
        }

        val notif = NotificationCompat.Builder(context, NotificationChannels.AZKAR_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.content))
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationId, notif)
    }

    fun showQuranReminderNotification(notification: QuranReminderNotification) {
        val contentText = notification.lastPage?.let { page ->
            "${notification.content}\nLast read: Page $page"
        } ?: notification.content

        val notif = NotificationCompat.Builder(context, NotificationChannels.QURAN_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notification.title)
            .setContentText(contentText)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NotificationIds.QURAN_REMINDER, notif)
    }

    fun showFridayKahfReminder() {
        val notif = NotificationCompat.Builder(context, NotificationChannels.QURAN_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Surah Al-Kahf")
            .setContentText("It is recommended to read Surah Al-Kahf on Fridays")
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NotificationIds.FRIDAY_KAHF, notif)
    }

    // ===== Notification Scheduling =====

    suspend fun schedulePrayerNotifications(
        prayerTimes: Map<String, String>,
        reminderMinutesBefore: Int = 5
    ): Result<Unit> =
        withContext(Dispatchers.Default) {
            runCatching {
                for ((prayerName, timeStr) in prayerTimes) {
                    try {
                        val (hours, minutes) = parseTimeString(timeStr)
                        val prayerTime = nextOccurrence(hours, minutes)
                        val reminderTime = prayerTime - TimeUnit.MINUTES.toMillis(reminderMinutesBefore.toLong())

                        if (reminderMinutesBefore > 0 && reminderTime > System.currentTimeMillis()) {
                            schedulePrayerAlarm(
                                requestCode = requestCodeFor(prayerName, "before"),
                                triggerAtMillis = reminderTime,
                                prayerName = prayerName,
                                prayerTime = timeStr,
                                kind = "before",
                                reminderMinutesBefore = reminderMinutesBefore
                            )
                        }

                        schedulePrayerAlarm(
                            requestCode = requestCodeFor(prayerName, "time"),
                            triggerAtMillis = prayerTime,
                            prayerName = prayerName,
                            prayerTime = timeStr,
                            kind = "time",
                            reminderMinutesBefore = reminderMinutesBefore
                        )
                    } catch (e: Exception) {
                        // Log error but continue
                    }
                }
            }
        }

    suspend fun scheduleMorningAzkarNotification(hourOfDay: Int = 6, minute: Int = 0): Result<Unit> =
        withContext(Dispatchers.Default) {
            runCatching {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                val intent = Intent(context, AzkarNotificationReceiver::class.java).apply {
                    action = "com.quranapp.android.MORNING_AZKAR"
                    putExtra("azkar_type", "MORNING")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    "morning_azkar".hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                setAlarm(calendar.timeInMillis, pendingIntent)
            }
        }

    suspend fun scheduleEveningAzkarNotification(hourOfDay: Int = 18, minute: Int = 0): Result<Unit> =
        withContext(Dispatchers.Default) {
            runCatching {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                val intent = Intent(context, AzkarNotificationReceiver::class.java).apply {
                    action = "com.quranapp.android.EVENING_AZKAR"
                    putExtra("azkar_type", "EVENING")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    "evening_azkar".hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                setAlarm(calendar.timeInMillis, pendingIntent)
            }
        }

    suspend fun scheduleQuranReminderNotification(hourOfDay: Int = 20, minute: Int = 0): Result<Unit> =
        withContext(Dispatchers.Default) {
            runCatching {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                val intent = Intent(context, QuranReminderReceiver::class.java).apply {
                    action = "com.quranapp.android.QURAN_REMINDER"
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    "quran_reminder".hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                setAlarm(calendar.timeInMillis, pendingIntent)
            }
        }

    suspend fun scheduleFridayKahfReminder(hourOfDay: Int = 9, minute: Int = 0): Result<Unit> =
        withContext(Dispatchers.Default) {
            runCatching {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                }

                val intent = Intent(context, FridayKahfReceiver::class.java).apply {
                    action = "com.quranapp.android.FRIDAY_KAHF"
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    "friday_kahf".hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                setAlarm(calendar.timeInMillis, pendingIntent)
            }
        }

    // ===== Notification Cancellation =====

    fun cancelPrayerNotifications() {
        notificationManager.cancel(NotificationIds.PRAYER_TIMES)
    }

    fun cancelAzkarNotifications() {
        notificationManager.cancel(NotificationIds.MORNING_AZKAR)
        notificationManager.cancel(NotificationIds.EVENING_AZKAR)
    }

    fun cancelQuranReminder() {
        notificationManager.cancel(NotificationIds.QURAN_REMINDER)
    }

    fun cancelFridayKahf() {
        notificationManager.cancel(NotificationIds.FRIDAY_KAHF)
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    fun updatePrayerTimesWidget() {
        PrayerTimesWidgetProvider.updateAll(context)
    }

    // ===== Helper Methods =====

    private fun parseTimeString(timeStr: String): Pair<Int, Int> {
        val cleaned = timeStr.trim().split(" ").first()
        val parts = cleaned.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return Pair(hours, minutes)
    }

    private fun nextOccurrence(hourOfDay: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun schedulePrayerAlarm(
        requestCode: Int,
        triggerAtMillis: Long,
        prayerName: String,
        prayerTime: String,
        kind: String,
        reminderMinutesBefore: Int
    ) {
        val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = "com.quranapp.android.PRAYER_NOTIFICATION.$kind.$prayerName"
            putExtra(PrayerNotificationReceiver.EXTRA_PRAYER_NAME, prayerName)
            putExtra(PrayerNotificationReceiver.EXTRA_PRAYER_TIME, prayerTime)
            putExtra(PrayerNotificationReceiver.EXTRA_KIND, kind)
            putExtra(PrayerNotificationReceiver.EXTRA_MINUTES_BEFORE, reminderMinutesBefore)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setAlarm(triggerAtMillis, pendingIntent)
    }

    private fun setAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            return
        }
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    private fun requestCodeFor(prayerName: String, kind: String): Int {
        return "$prayerName:$kind".hashCode()
    }
}

// Notification Receivers are defined in their own files:
// PrayerNotificationReceiver.kt, AzkarNotificationReceiver.kt,
// QuranReminderReceiver.kt, FridayKahfReceiver.kt
