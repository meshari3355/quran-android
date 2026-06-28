package com.quranapp.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Import design system and screen composables
import com.quranapp.android.ui.theme.AppDesign
import com.quranapp.android.ui.screens.home.HomeScreen
import com.quranapp.android.ui.screens.quran.SurahListScreen
import com.quranapp.android.ui.screens.quran.QuranReaderScreen
import com.quranapp.android.ui.screens.prayer.PrayerTimesScreen
import com.quranapp.android.ui.screens.prayer.QiblaScreen
import com.quranapp.android.ui.screens.azkar.AzkarScreen
import com.quranapp.android.ui.screens.azkar.TasbihScreen
import com.quranapp.android.ui.screens.settings.SettingsScreen
import com.quranapp.android.ui.screens.onboarding.OnboardingScreen
import com.quranapp.android.ui.screens.more.HadithPortalScreen
import com.quranapp.android.ui.screens.more.FatwaScreen
import com.quranapp.android.ui.screens.more.AudioLibraryScreen
import com.quranapp.android.ui.screens.more.IslamicLibraryScreen
import com.quranapp.android.ui.screens.more.ZakatCalculatorScreen
import com.quranapp.android.ui.screens.more.IslamicCalendarScreen
import com.quranapp.android.ui.screens.more.NearbyMosquesScreen
import com.quranapp.android.ui.screens.more.BookmarksScreen
import com.quranapp.android.ui.screens.more.ReadingStatsScreen
import com.quranapp.android.ui.screens.more.OfflineDownloadsScreen
import com.quranapp.android.ui.screens.more.MuslimToolsScreen
import com.quranapp.android.ui.screens.more.NawawiHadithScreen
import com.quranapp.android.ui.screens.quran.TafsirScreen
import com.quranapp.android.ui.screens.hadith.HadithBooksScreen
import com.quranapp.android.ui.screens.hadith.HadithChaptersScreen
import com.quranapp.android.ui.screens.hadith.HadithDetailScreen
import com.quranapp.android.ui.screens.hadith.HadithSearchScreen
import com.quranapp.android.ui.screens.azkar.AzkarListScreen
import com.quranapp.android.ui.screens.more.FatwaDetailScreen
import com.quranapp.android.ui.screens.more.NawawiDetailScreen

// Sealed class for navigation routes
sealed class Screen(val route: String) {
    // Main screens
    object Home : Screen("home")
    object SurahList : Screen("surah_list")
    object QuranReader : Screen("quran_reader/{surahId}/{page}") {
        fun createRoute(surahId: Int, page: Int = 1) = "quran_reader/$surahId/$page"
    }

    object Tafsir : Screen("tafsir/{surahId}/{ayahNumber}") {
        fun createRoute(surahId: Int, ayahNumber: Int) = "tafsir/$surahId/$ayahNumber"
    }

    // Azkar screens
    object AzkarMain : Screen("azkar_main")
    object AzkarList : Screen("azkar_list/{categoryId}") {
        fun createRoute(categoryId: Int) = "azkar_list/$categoryId"
    }

    object Tasbih : Screen("tasbih")

    // Prayer screens
    object PrayerTimes : Screen("prayer_times")
    object Qibla : Screen("qibla")

    // Muslim Tools screens
    object MuslimTools : Screen("muslim_tools")
    object HadithPortal : Screen("hadith_portal")
    object HadithBooks : Screen("hadith_books/{categoryId}") {
        fun createRoute(categoryId: Int) = "hadith_books/$categoryId"
    }

    object HadithChapters : Screen("hadith_chapters/{bookId}") {
        fun createRoute(bookId: Int) = "hadith_chapters/$bookId"
    }

    object HadithBabs : Screen("hadith_babs/{chapterId}") {
        fun createRoute(chapterId: Int) = "hadith_babs/$chapterId"
    }

    object HadithDetail : Screen("hadith_detail/{hadithId}") {
        fun createRoute(hadithId: Long) = "hadith_detail/$hadithId"
    }

    object HadithSearch : Screen("hadith_search")

    // Fatwa screens
    object FatwaList : Screen("fatwa_list")
    object FatwaDetail : Screen("fatwa_detail/{fatwaId}") {
        fun createRoute(fatwaId: Long) = "fatwa_detail/$fatwaId"
    }

    // Islamic tools screens
    object ZakatCalculator : Screen("zakat_calculator")
    object IslamicCalendar : Screen("islamic_calendar")
    object NearbyMosques : Screen("nearby_mosques")

    // Library screens
    object AudioLibrary : Screen("audio_library")
    object IslamicLibrary : Screen("islamic_library")

    // Nawawi Hadith screens
    object NawawiHadith : Screen("nawawi_hadith")
    object NawawiDetail : Screen("nawawi_detail/{hadithId}") {
        fun createRoute(hadithId: Long) = "nawawi_detail/$hadithId"
    }

    // Settings and preferences
    object Settings : Screen("settings")
    object Onboarding : Screen("onboarding")

    // Bookmarks and stats
    object Bookmarks : Screen("bookmarks")
    object ReadingStats : Screen("reading_stats")
    object OfflineDownloads : Screen("offline_downloads")
}

// Bottom navigation item data
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen,
    val contentDescription: String = label
)

val bottomNavItems = listOf(
    BottomNavItem(
        label = "الرئيسية",
        icon = Icons.Filled.Home,
        screen = Screen.Home,
        contentDescription = "Home"
    ),
    BottomNavItem(
        label = "القرآن",
        icon = Icons.Filled.Book,
        screen = Screen.SurahList,
        contentDescription = "Quran"
    ),
    BottomNavItem(
        label = "الأذكار",
        icon = Icons.Filled.Favorite,
        screen = Screen.AzkarMain,
        contentDescription = "Azkar"
    ),
    BottomNavItem(
        label = "الصلاة",
        icon = Icons.Filled.AccessTime,
        screen = Screen.PrayerTimes,
        contentDescription = "Prayer"
    ),
    BottomNavItem(
        label = "المزيد",
        icon = Icons.Filled.GridView,
        screen = Screen.MuslimTools,
        contentDescription = "More"
    )
)

@Composable
fun AppNavigation(
    navController: NavHostController,
    initialRoute: String = Screen.Home.route
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(initialRoute) {
            if (initialRoute != Screen.Home.route) {
                navController.navigate(initialRoute) {
                    launchSingleTop = true
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.weight(1f)
        ) {
            // Home screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToQuran = { navController.navigate(Screen.SurahList.route) },
                    onNavigateToPrayer = { navController.navigate(Screen.PrayerTimes.route) },
                    onNavigateToQibla = { navController.navigate(Screen.Qibla.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Quran screens
            composable(Screen.SurahList.route) {
                SurahListScreen(
                    onSurahSelected = { surahId, pageNumber ->
                        navController.navigate(Screen.QuranReader.createRoute(surahId, pageNumber))
                    },
                    onNavigateToReader = { }
                )
            }

            composable(Screen.QuranReader.route) { backStackEntry ->
                val surahId = backStackEntry.arguments?.getString("surahId")?.toIntOrNull() ?: 1
                val pageNumber = backStackEntry.arguments?.getString("page")?.toIntOrNull() ?: 1
                val surahName = com.quranapp.android.models.Surah.getSurahById(surahId)?.nameAr ?: "الفاتحة"
                QuranReaderScreen(
                    surahId = surahId,
                    surahName = surahName,
                    pageNumber = pageNumber,
                    juzNumber = 1,
                    onNavigateToTafsir = { ayahNumber -> navController.navigate(Screen.Tafsir.createRoute(surahId, ayahNumber)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Tafsir.route) { backStackEntry ->
                val surahId = backStackEntry.arguments?.getString("surahId")?.toIntOrNull() ?: 1
                val ayahNumber = backStackEntry.arguments?.getString("ayahNumber")?.toIntOrNull() ?: 1
                val surahName = com.quranapp.android.models.Surah.getSurahById(surahId)?.nameAr ?: "سورة $surahId"
                TafsirScreen(
                    verseNumber = ayahNumber,
                    surahName = surahName,
                    surahId = surahId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Azkar screens
            composable(Screen.AzkarMain.route) {
                AzkarScreen()
            }

            composable(Screen.AzkarList.route) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 0
                AzkarListScreen(
                    categoryId = categoryId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Tasbih.route) {
                TasbihScreen()
            }

            // Prayer screens
            composable(Screen.PrayerTimes.route) {
                PrayerTimesScreen(
                    onNavigateToQibla = { navController.navigate(Screen.Qibla.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Qibla.route) {
                QiblaScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Muslim Tools screens
            composable(Screen.MuslimTools.route) {
                MuslimToolsScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.HadithPortal.route) {
                HadithPortalScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.HadithBooks.route) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 0
                HadithBooksScreen(
                    categoryId = categoryId,
                    onBack = { navController.popBackStack() },
                    onBookSelected = { bookId -> navController.navigate(Screen.HadithChapters.createRoute(bookId)) }
                )
            }

            composable(Screen.HadithChapters.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: 0
                HadithChaptersScreen(
                    bookId = bookId,
                    onBack = { navController.popBackStack() },
                    onHadithSelected = { hadithId -> navController.navigate(Screen.HadithDetail.createRoute(hadithId)) }
                )
            }

            composable(Screen.HadithBabs.route) { backStackEntry ->
                val chapterId = backStackEntry.arguments?.getString("chapterId")?.toIntOrNull() ?: 0
                HadithChaptersScreen(
                    bookId = chapterId,
                    onBack = { navController.popBackStack() },
                    onHadithSelected = { hadithId -> navController.navigate(Screen.HadithDetail.createRoute(hadithId)) }
                )
            }

            composable(Screen.HadithDetail.route) { backStackEntry ->
                val hadithId = backStackEntry.arguments?.getString("hadithId")?.toLongOrNull() ?: 0L
                HadithDetailScreen(
                    hadithId = hadithId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.HadithSearch.route) {
                HadithSearchScreen(
                    onBack = { navController.popBackStack() },
                    onHadithSelected = { hadithId -> navController.navigate(Screen.HadithDetail.createRoute(hadithId)) }
                )
            }

            // Fatwa screens
            composable(Screen.FatwaList.route) {
                FatwaScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.FatwaDetail.route) { backStackEntry ->
                val fatwaId = backStackEntry.arguments?.getString("fatwaId")?.toLongOrNull() ?: 0L
                FatwaDetailScreen(
                    fatwaId = fatwaId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Islamic tools
            composable(Screen.ZakatCalculator.route) {
                ZakatCalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.IslamicCalendar.route) {
                IslamicCalendarScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NearbyMosques.route) {
                NearbyMosquesScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Library screens
            composable(Screen.AudioLibrary.route) {
                AudioLibraryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.IslamicLibrary.route) {
                IslamicLibraryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Nawawi Hadith
            composable(Screen.NawawiHadith.route) {
                NawawiHadithScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NawawiDetail.route) { backStackEntry ->
                val hadithId = backStackEntry.arguments?.getString("hadithId")?.toLongOrNull() ?: 0L
                NawawiDetailScreen(
                    hadithId = hadithId,
                    onBack = { navController.popBackStack() }
                )
            }

            // Settings and preferences
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onCompleted = { navController.navigate(Screen.Home.route) }
                )
            }

            // Bookmarks and stats
            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    onNavigateToReader = { surah, ayah, page -> navController.navigate(Screen.QuranReader.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ReadingStats.route) {
                ReadingStatsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.OfflineDownloads.route) {
                OfflineDownloadsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // Bottom navigation bar
        BottomNavigationBar(navController)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Check if bottom bar should be visible
    val shouldShowBottomBar = shouldShowBottomBar(currentRoute)

    if (shouldShowBottomBar) {
        val colors = AppDesign.colors

        NavigationBar(
            containerColor = colors.card,
            contentColor = colors.textPrimary
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.screen.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.contentDescription,
                            tint = if (isSelected) colors.gold else colors.textTertiary
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = if (isSelected) colors.gold else colors.textTertiary,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.gold,
                        unselectedIconColor = colors.textTertiary,
                        indicatorColor = colors.goldContainer
                    )
                )
            }
        }
    }
}

fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        Screen.QuranReader.route -> false
        Screen.Tafsir.route -> false
        Screen.HadithDetail.route -> false
        Screen.FatwaDetail.route -> false
        Screen.NawawiDetail.route -> false
        Screen.Onboarding.route -> false
        else -> true
    }
}

// Import actual screen composables from their respective packages
// (These would be imported in the actual implementation)
// For now, they're assumed to be available from their packages
