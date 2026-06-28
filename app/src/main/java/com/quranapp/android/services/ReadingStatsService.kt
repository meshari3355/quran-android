package com.quranapp.android.services

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

// ===== Reading Statistics Models =====

data class DailyReadingStats(
    val date: Long,
    val pagesRead: Int = 0,
    val timeSpentSeconds: Long = 0L,
    val surrahsCompleted: List<Int> = emptyList(),
    val ayahsRead: Int = 0
) {
    val timeSpentMinutes: Long get() = timeSpentSeconds / 60
    val timeSpentHours: Double get() = timeSpentSeconds / 3600.0
}

data class WeeklyReadingStats(
    val startDate: Long,
    val endDate: Long,
    val totalPagesRead: Int = 0,
    val totalTimeSpentSeconds: Long = 0L,
    val uniqueSurrahsCompleted: Set<Int> = emptySet(),
    val daysWithReading: Int = 0
) {
    val totalTimeSpentHours: Double get() = totalTimeSpentSeconds / 3600.0
    val averagePagesPerDay: Double get() = if (daysWithReading > 0) totalPagesRead.toDouble() / daysWithReading else 0.0
    val averageTimePerDay: Double get() = if (daysWithReading > 0) totalTimeSpentSeconds.toDouble() / daysWithReading else 0.0
}

data class MonthlyReadingStats(
    val year: Int,
    val month: Int,
    val totalPagesRead: Int = 0,
    val totalTimeSpentSeconds: Long = 0L,
    val uniqueSurrahsCompleted: Set<Int> = emptySet(),
    val daysWithReading: Int = 0
) {
    val totalTimeSpentHours: Double get() = totalTimeSpentSeconds / 3600.0
    val averagePagesPerDay: Double get() = if (daysWithReading > 0) totalPagesRead.toDouble() / daysWithReading else 0.0
    val readingStreak: Int = 0
}

data class KhatmahProgress(
    val startDate: Long,
    val completedPages: Int = 0,
    val totalPages: Int = 604,
    val surrahsCompleted: List<Int> = emptyList()
) {
    val percentageComplete: Float get() = (completedPages.toFloat() / totalPages) * 100
    val pagesRemaining: Int get() = totalPages - completedPages
    val isComplete: Boolean get() = completedPages >= totalPages
}

data class ReadingAchievement(
    val id: String,
    val name: String,
    val nameAr: String,
    val description: String,
    val descriptionAr: String,
    val unlockedDate: Long? = null,
    val isUnlocked: Boolean = false
)

// ===== Reading Statistics Service =====

class ReadingStatsService(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) {
    companion object {
        private const val PREFIX_DAILY = "daily_stats_"
        private const val KEY_CURRENT_KHATMAH = "current_khatmah"
        private const val KEY_ALL_KHATMAHS = "all_khatmahs"
        private const val KEY_READING_STREAK = "reading_streak"
        private const val KEY_LAST_READING_DATE = "last_reading_date"
        private const val KEY_TOTAL_PAGES_READ = "total_pages_read"
        private const val KEY_TOTAL_TIME_SPENT = "total_time_spent"
        private const val KEY_ACHIEVEMENTS = "achievements"
    }

    // ===== Daily Reading Tracking =====

    suspend fun recordDailyReading(
        pagesRead: Int,
        timeSpentSeconds: Long,
        surrahsCompleted: List<Int> = emptyList()
    ): Result<DailyReadingStats> = withContext(Dispatchers.Default) {
        runCatching {
            val today = getTodayTimestamp()
            val stats = DailyReadingStats(
                date = today,
                pagesRead = pagesRead,
                timeSpentSeconds = timeSpentSeconds,
                surrahsCompleted = surrahsCompleted
            )

            saveDailyStats(stats)
            updateReadingStreak()
            updateTotalStats(pagesRead, timeSpentSeconds)

            stats
        }
    }

    suspend fun updateDailyReading(
        date: Long,
        pagesRead: Int,
        timeSpentSeconds: Long
    ): Result<DailyReadingStats> = withContext(Dispatchers.Default) {
        runCatching {
            val stats = DailyReadingStats(
                date = date,
                pagesRead = pagesRead,
                timeSpentSeconds = timeSpentSeconds
            )

            saveDailyStats(stats)
            stats
        }
    }

    suspend fun getDailyStats(date: Long = getTodayTimestamp()): Result<DailyReadingStats?> =
        withContext(Dispatchers.Default) {
            runCatching {
                val key = "$PREFIX_DAILY$date"
                val json = sharedPreferences.getString(key, null)
                if (json != null) {
                    gson.fromJson(json, DailyReadingStats::class.java)
                } else {
                    null
                }
            }
        }

    // ===== Weekly Statistics =====

    suspend fun getWeeklyStats(endDate: Long = getTodayTimestamp()): Result<WeeklyReadingStats> =
        withContext(Dispatchers.Default) {
            runCatching {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = endDate
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val weekStart = calendar.timeInMillis

                val dailyStats = mutableListOf<DailyReadingStats>()
                var currentDate = weekStart
                while (currentDate <= endDate) {
                    val stats = getDailyStats(currentDate).getOrNull()
                    if (stats != null) {
                        dailyStats.add(stats)
                    }
                    currentDate += 86400000 // 1 day in milliseconds
                }

                val totalPages = dailyStats.sumOf { it.pagesRead }
                val totalTime = dailyStats.sumOf { it.timeSpentSeconds }
                val surrahs = dailyStats.flatMap { it.surrahsCompleted }.toSet()

                WeeklyReadingStats(
                    startDate = weekStart,
                    endDate = endDate,
                    totalPagesRead = totalPages,
                    totalTimeSpentSeconds = totalTime,
                    uniqueSurrahsCompleted = surrahs,
                    daysWithReading = dailyStats.size
                )
            }
        }

    // ===== Monthly Statistics =====

    suspend fun getMonthlyStats(year: Int, month: Int): Result<MonthlyReadingStats> =
        withContext(Dispatchers.Default) {
            runCatching {
                val calendar = Calendar.getInstance()
                calendar.set(year, month - 1, 1)

                val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val dailyStats = mutableListOf<DailyReadingStats>()

                for (day in 1..daysInMonth) {
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    val stats = getDailyStats(calendar.timeInMillis).getOrNull()
                    if (stats != null) {
                        dailyStats.add(stats)
                    }
                }

                val totalPages = dailyStats.sumOf { it.pagesRead }
                val totalTime = dailyStats.sumOf { it.timeSpentSeconds }
                val surrahs = dailyStats.flatMap { it.surrahsCompleted }.toSet()

                MonthlyReadingStats(
                    year = year,
                    month = month,
                    totalPagesRead = totalPages,
                    totalTimeSpentSeconds = totalTime,
                    uniqueSurrahsCompleted = surrahs,
                    daysWithReading = dailyStats.size
                )
            }
        }

    // ===== Khatmah Tracking =====

    suspend fun startNewKhatmah(): Result<KhatmahProgress> = withContext(Dispatchers.Default) {
        runCatching {
            val khatmah = KhatmahProgress(
                startDate = System.currentTimeMillis(),
                completedPages = 0
            )

            saveCurrentKhatmah(khatmah)
            khatmah
        }
    }

    suspend fun updateKhatmahProgress(pagesCompleted: Int, surrahsCompleted: List<Int>): Result<KhatmahProgress> =
        withContext(Dispatchers.Default) {
            runCatching {
                val current = getCurrentKhatmah().getOrNull()
                    ?: throw Exception("No active khatmah")

                val updated = current.copy(
                    completedPages = pagesCompleted,
                    surrahsCompleted = surrahsCompleted
                )

                saveCurrentKhatmah(updated)
                updated
            }
        }

    suspend fun completeCurrentKhatmah(): Result<KhatmahProgress> = withContext(Dispatchers.Default) {
        runCatching {
            val current = getCurrentKhatmah().getOrThrow()
            val completed = current.copy(completedPages = current.totalPages)

            // Save to completed khatmahs list
            saveCompletedKhatmah(completed)

            // Start new khatmah
            startNewKhatmah().getOrThrow()

            completed
        }
    }

    suspend fun getCurrentKhatmah(): Result<KhatmahProgress> = withContext(Dispatchers.Default) {
        runCatching {
            val json = sharedPreferences.getString(KEY_CURRENT_KHATMAH, null)
            if (json != null) {
                gson.fromJson(json, KhatmahProgress::class.java)
            } else {
                startNewKhatmah().getOrThrow()
            }
        }
    }

    suspend fun getCompletedKhatmahs(): Result<List<KhatmahProgress>> = withContext(Dispatchers.Default) {
        runCatching {
            val json = sharedPreferences.getString(KEY_ALL_KHATMAHS, null)
            if (json != null) {
                val type = com.google.gson.reflect.TypeToken.getParameterized(
                    List::class.java,
                    KhatmahProgress::class.java
                ).type
                gson.fromJson(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun getKhatmahCount(): Result<Int> = withContext(Dispatchers.Default) {
        runCatching {
            getCompletedKhatmahs().getOrThrow().size
        }
    }

    // ===== Reading Streak =====

    suspend fun getReadingStreak(): Result<Int> = withContext(Dispatchers.Default) {
        runCatching {
            sharedPreferences.getInt(KEY_READING_STREAK, 0)
        }
    }

    suspend fun calculateReadingStreak(): Result<Int> = withContext(Dispatchers.Default) {
        runCatching {
            var streak = 0
            var currentDate = getTodayTimestamp()

            while (true) {
                val stats = getDailyStats(currentDate).getOrNull()
                if (stats != null && stats.pagesRead > 0) {
                    streak++
                    currentDate -= 86400000 // Go back 1 day
                } else {
                    break
                }
            }

            sharedPreferences.edit().putInt(KEY_READING_STREAK, streak).apply()
            streak
        }
    }

    // ===== Total Statistics =====

    suspend fun getTotalPagesRead(): Result<Int> = withContext(Dispatchers.Default) {
        runCatching {
            sharedPreferences.getInt(KEY_TOTAL_PAGES_READ, 0)
        }
    }

    suspend fun getTotalTimeSpent(): Result<Long> = withContext(Dispatchers.Default) {
        runCatching {
            sharedPreferences.getLong(KEY_TOTAL_TIME_SPENT, 0L)
        }
    }

    // ===== Achievements =====

    suspend fun getAchievements(): Result<List<ReadingAchievement>> = withContext(Dispatchers.Default) {
        runCatching {
            val achievements = listOf(
                ReadingAchievement(
                    "first_page",
                    "First Page",
                    "الصفحة الأولى",
                    "Read your first page",
                    "اقرأ صفحتك الأولى"
                ),
                ReadingAchievement(
                    "one_hour",
                    "One Hour",
                    "ساعة واحدة",
                    "Spend 1 hour reading",
                    "اقضِ ساعة واحدة في القراءة"
                ),
                ReadingAchievement(
                    "one_day_streak",
                    "One Day Streak",
                    "يوم واحد",
                    "Read for 1 consecutive day",
                    "اقرأ ليوم واحد متتالي"
                ),
                ReadingAchievement(
                    "one_week_streak",
                    "One Week Streak",
                    "أسبوع واحد",
                    "Read for 7 consecutive days",
                    "اقرأ لمدة 7 أيام متتالية"
                ),
                ReadingAchievement(
                    "first_surah",
                    "First Surah",
                    "السورة الأولى",
                    "Complete your first surah",
                    "أكمل سورتك الأولى"
                ),
                ReadingAchievement(
                    "first_khatmah",
                    "First Khatmah",
                    "الختمة الأولى",
                    "Complete your first complete reading",
                    "أكمل قراءة القرآن كاملاً"
                )
            )

            val json = sharedPreferences.getString(KEY_ACHIEVEMENTS, null)
            if (json != null) {
                val unlockedIds = gson.fromJson(json, Array<String>::class.java).toSet()
                achievements.map { achievement ->
                    achievement.copy(
                        isUnlocked = achievement.id in unlockedIds,
                        unlockedDate = if (achievement.id in unlockedIds) System.currentTimeMillis() else null
                    )
                }
            } else {
                achievements
            }
        }
    }

    suspend fun unlockAchievement(achievementId: String): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            val json = sharedPreferences.getString(KEY_ACHIEVEMENTS, null)
            val unlockedIds = if (json != null) {
                gson.fromJson(json, Array<String>::class.java).toMutableList()
            } else {
                mutableListOf()
            }

            if (!unlockedIds.contains(achievementId)) {
                unlockedIds.add(achievementId)
                sharedPreferences.edit()
                    .putString(KEY_ACHIEVEMENTS, gson.toJson(unlockedIds.toTypedArray()))
                    .apply()
            }
        }
    }

    // ===== Private Methods =====

    private fun saveDailyStats(stats: DailyReadingStats) {
        val key = "$PREFIX_DAILY${stats.date}"
        val json = gson.toJson(stats)
        sharedPreferences.edit().putString(key, json).apply()
    }

    private fun saveCurrentKhatmah(khatmah: KhatmahProgress) {
        val json = gson.toJson(khatmah)
        sharedPreferences.edit().putString(KEY_CURRENT_KHATMAH, json).apply()
    }

    private fun saveCompletedKhatmah(khatmah: KhatmahProgress) {
        val json = sharedPreferences.getString(KEY_ALL_KHATMAHS, null)
        val khatmahs = if (json != null) {
            val type = com.google.gson.reflect.TypeToken.getParameterized(
                List::class.java,
                KhatmahProgress::class.java
            ).type
            (gson.fromJson<List<KhatmahProgress>>(json, type) ?: emptyList()).toMutableList()
        } else {
            mutableListOf()
        }

        khatmahs.add(khatmah)
        sharedPreferences.edit().putString(KEY_ALL_KHATMAHS, gson.toJson(khatmahs)).apply()
    }

    private fun updateReadingStreak() {
        val today = getTodayTimestamp()
        val lastReadingDate = sharedPreferences.getLong(KEY_LAST_READING_DATE, 0L)

        val daysDifference = (today - lastReadingDate) / 86400000

        val currentStreak = sharedPreferences.getInt(KEY_READING_STREAK, 0)
        val newStreak = if (daysDifference <= 1) currentStreak + 1 else 1

        sharedPreferences.edit()
            .putInt(KEY_READING_STREAK, newStreak)
            .putLong(KEY_LAST_READING_DATE, today)
            .apply()
    }

    private fun updateTotalStats(pagesRead: Int, timeSpentSeconds: Long) {
        val totalPages = sharedPreferences.getInt(KEY_TOTAL_PAGES_READ, 0) + pagesRead
        val totalTime = sharedPreferences.getLong(KEY_TOTAL_TIME_SPENT, 0L) + timeSpentSeconds

        sharedPreferences.edit()
            .putInt(KEY_TOTAL_PAGES_READ, totalPages)
            .putLong(KEY_TOTAL_TIME_SPENT, totalTime)
            .apply()
    }

    private fun getTodayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun resetAllStats() {
        sharedPreferences.edit().clear().apply()
    }
}
