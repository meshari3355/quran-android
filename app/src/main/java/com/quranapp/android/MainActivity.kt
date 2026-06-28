package com.quranapp.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.quranapp.android.ui.navigation.AppNavigation
import com.quranapp.android.ui.navigation.Screen
import com.quranapp.android.ui.theme.QuranTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestNotificationPermissionIfNeeded()

        val initialRoute = routeForIntent(intent)

        setContent {
            QuranTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        initialRoute = initialRoute
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun routeForIntent(intent: Intent?): String {
        return when (intent?.getStringExtra("navigate_to")) {
            "prayer_times" -> Screen.PrayerTimes.route
            "qibla" -> Screen.Qibla.route
            "azkar_main" -> Screen.AzkarMain.route
            "quran_reader" -> {
                val surahId = intent.getIntExtra("surah_id", 1)
                val page = intent.getIntExtra("page", 1)
                Screen.QuranReader.createRoute(surahId, page)
            }
            "quran" -> Screen.SurahList.route
            else -> Screen.Home.route
        }
    }
}
