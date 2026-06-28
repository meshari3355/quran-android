package com.quranapp.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.quranapp.android.services.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuranApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        private lateinit var instance: QuranApplication

        fun getInstance(): QuranApplication = instance

        const val PRAYER_NOTIFICATION_CHANNEL_ID = "prayer_notifications"
        const val AZKAR_NOTIFICATION_CHANNEL_ID = "azkar_notifications"
        const val GENERAL_NOTIFICATION_CHANNEL_ID = "general_notifications"
        const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_notifications"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
        scheduleDefaultReminders()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val prayerChannel = NotificationChannel(
            PRAYER_NOTIFICATION_CHANNEL_ID,
            "مواقيت الصلاة",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "إشعارات مواقيت الصلاة"
            enableVibration(true)
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(prayerChannel)

        val azkarChannel = NotificationChannel(
            AZKAR_NOTIFICATION_CHANNEL_ID,
            "تذكير الأذكار",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "تذكير بأذكار الصباح والمساء"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(azkarChannel)

        val generalChannel = NotificationChannel(
            GENERAL_NOTIFICATION_CHANNEL_ID,
            "إشعارات عامة",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "إشعارات عامة للتطبيق"
        }
        notificationManager.createNotificationChannel(generalChannel)

        val downloadChannel = NotificationChannel(
            DOWNLOAD_NOTIFICATION_CHANNEL_ID,
            "التحميلات",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "إشعارات تقدم التحميل"
        }
        notificationManager.createNotificationChannel(downloadChannel)
    }

    private fun scheduleDefaultReminders() {
        appScope.launch {
            ReminderScheduler.scheduleAll(this@QuranApplication)
        }
    }
}
