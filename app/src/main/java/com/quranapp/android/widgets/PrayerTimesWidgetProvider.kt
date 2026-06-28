package com.quranapp.android.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.quranapp.android.MainActivity
import com.quranapp.android.R
import com.quranapp.android.data.local.PreferencesManager

class PrayerTimesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = android.content.ComponentName(context, PrayerTimesWidgetProvider::class.java)
            appWidgetManager.getAppWidgetIds(componentName).forEach { appWidgetId ->
                updateWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val preferencesManager = PreferencesManager(context)
            val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)

            views.setTextViewText(R.id.widget_next_prayer_name, preferencesManager.getNextPrayerName())
            views.setTextViewText(R.id.widget_next_prayer_time, preferencesManager.getNextPrayerTime())
            views.setTextViewText(R.id.widget_subtitle, "الصلاة القادمة")

            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "prayer_times")
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                8001,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
