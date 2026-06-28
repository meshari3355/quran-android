package com.quranapp.android.services

import android.content.Context
import com.quranapp.android.data.local.PreferencesManager
import com.quranapp.android.data.repository.PrayerRepository
import com.quranapp.android.models.ServerPrayerTimes
import com.quranapp.android.widgets.PrayerTimesWidgetProvider
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ReminderScheduler {
    private data class PrayerMoment(
        val name: String,
        val time: String,
        val shouldNotify: Boolean
    )

    suspend fun scheduleAll(context: Context) {
        val appContext = context.applicationContext
        val preferencesManager = PreferencesManager(appContext)
        if (!preferencesManager.getNotificationsEnabled()) {
            PrayerTimesWidgetProvider.updateAll(appContext)
            return
        }

        val notificationService = NotificationService(appContext)
        if (preferencesManager.getAzkarRemindersEnabled()) {
            notificationService.scheduleMorningAzkarNotification()
            notificationService.scheduleEveningAzkarNotification()
        }
        notificationService.scheduleQuranReminderNotification()
        notificationService.scheduleFridayKahfReminder()
        schedulePrayerTimes(appContext, notificationService, preferencesManager)
    }

    suspend fun schedulePrayerTimes(context: Context) {
        val appContext = context.applicationContext
        val preferencesManager = PreferencesManager(appContext)
        if (!preferencesManager.getNotificationsEnabled()) return
        schedulePrayerTimes(
            context = appContext,
            notificationService = NotificationService(appContext),
            preferencesManager = preferencesManager
        )
    }

    private suspend fun schedulePrayerTimes(
        context: Context,
        notificationService: NotificationService,
        preferencesManager: PreferencesManager
    ) {
        if (!preferencesManager.getPrayerNotificationsEnabled()) {
            PrayerTimesWidgetProvider.updateAll(context)
            return
        }

        runCatching {
            val prayerRepository = PrayerRepository(ApiService(), preferencesManager)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val result = prayerRepository.getPrayerTimesStream(
                preferencesManager.getLastPrayerLatitude(),
                preferencesManager.getLastPrayerLongitude(),
                currentDate,
                10
            ).first()

            result.getOrThrow().let { prayerTimes ->
                val moments = prayerMoments(prayerTimes)
                updateNextPrayer(preferencesManager, moments)
                val notificationTimes = moments
                    .filter { it.shouldNotify }
                    .associate { it.name to it.time }

                if (notificationTimes.isNotEmpty()) {
                    notificationService.schedulePrayerNotifications(
                        prayerTimes = notificationTimes,
                        reminderMinutesBefore = preferencesManager.getPrayerNotificationTime()
                    )
                }
                PrayerTimesWidgetProvider.updateAll(context)
            }
        }
    }

    private fun prayerMoments(prayerTimes: ServerPrayerTimes): List<PrayerMoment> {
        return listOf(
            Triple("الفجر", prayerTimes.getFajrTime(), true),
            Triple("الشروق", prayerTimes.getSunriseTime(), false),
            Triple("الظهر", prayerTimes.getDhuhrTime(), true),
            Triple("العصر", prayerTimes.getAsrTime(), true),
            Triple("المغرب", prayerTimes.getMaghribTime(), true),
            Triple("العشاء", prayerTimes.getIshaTime(), true)
        ).mapNotNull { (name, rawTime, shouldNotify) ->
            normalizePrayerTime(rawTime)?.let { time ->
                PrayerMoment(name, time, shouldNotify)
            }
        }
    }

    private fun updateNextPrayer(
        preferencesManager: PreferencesManager,
        moments: List<PrayerMoment>
    ) {
        if (moments.isEmpty()) return

        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val nextPrayer = moments.firstOrNull { moment ->
            minutesOfDay(moment.time) > currentMinutes
        } ?: moments.first()

        preferencesManager.saveNextPrayer(nextPrayer.name, nextPrayer.time)
    }

    private fun normalizePrayerTime(rawTime: String?): String? {
        val value = rawTime?.trim()?.split(" ")?.firstOrNull().orEmpty()
        val parts = value.split(":")
        if (parts.size < 2) return null

        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null

        return String.format(Locale.US, "%02d:%02d", hour, minute)
    }

    private fun minutesOfDay(time: String): Int {
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return hour * 60 + minute
    }
}
