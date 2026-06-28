package com.quranapp.android.data.local

import android.content.Context

/**
 * Simple database manager placeholder.
 * Uses SharedPreferences for now. Can be upgraded to Room later.
 */
class DatabaseManager private constructor(private val context: Context) {

    companion object {
        private lateinit var instance: DatabaseManager

        fun initialize(context: Context) {
            if (!::instance.isInitialized) {
                instance = DatabaseManager(context.applicationContext)
            }
        }

        fun getInstance(): DatabaseManager = instance
    }
}
