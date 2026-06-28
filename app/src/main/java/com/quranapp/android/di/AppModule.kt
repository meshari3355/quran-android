package com.quranapp.android.di

import android.content.Context
import com.quranapp.android.data.local.PreferencesManager
import com.quranapp.android.data.repository.AzkarRepository
import com.quranapp.android.data.repository.FatwaRepository
import com.quranapp.android.data.repository.HadithRepository
import com.quranapp.android.data.repository.PrayerRepository
import com.quranapp.android.data.repository.QuranRepository
import com.quranapp.android.data.repository.TafsirRepository
import com.quranapp.android.services.ApiService
import com.quranapp.android.services.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection module for providing singleton instances
 * across the application.
 *
 * This module provides:
 * - API Service for network calls
 * - Repository classes for data access abstraction
 * - Preferences Manager for local data persistence
 *
 * Following Google's official architecture guidelines:
 * https://developer.android.com/topic/architecture
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provide ApiService singleton instance
     * Used for making network requests to the backend API
     */
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiService()
    }

    /**
     * Provide PreferencesManager singleton instance
     * Used for storing user preferences and settings
     */
    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService {
        return NotificationService(context)
    }

    /**
     * Provide QuranRepository singleton instance
     * Handles all Quran-related data operations
     */
    @Provides
    @Singleton
    fun provideQuranRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): QuranRepository {
        return QuranRepository(apiService, preferencesManager)
    }

    /**
     * Provide PrayerRepository singleton instance
     * Handles all prayer time and Qibla direction data operations
     */
    @Provides
    @Singleton
    fun providePrayerRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): PrayerRepository {
        return PrayerRepository(apiService, preferencesManager)
    }

    /**
     * Provide AzkarRepository singleton instance
     * Handles all Azkar (remembrance) data operations
     */
    @Provides
    @Singleton
    fun provideAzkarRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): AzkarRepository {
        return AzkarRepository(apiService, preferencesManager)
    }

    /**
     * Provide HadithRepository singleton instance
     * Handles all Hadith data operations
     */
    @Provides
    @Singleton
    fun provideHadithRepository(apiService: ApiService): HadithRepository {
        return HadithRepository(apiService)
    }

    /**
     * Provide FatwaRepository singleton instance
     * Handles all Fatwa (Islamic legal opinions) data operations
     */
    @Provides
    @Singleton
    fun provideFatwaRepository(
        apiService: ApiService,
        preferencesManager: PreferencesManager
    ): FatwaRepository {
        return FatwaRepository(apiService, preferencesManager)
    }

    /**
     * Provide TafsirRepository singleton instance
     * Handles all Tafsir (Quran exegesis) data operations
     */
    @Provides
    @Singleton
    fun provideTafsirRepository(apiService: ApiService): TafsirRepository {
        return TafsirRepository(apiService)
    }
}
