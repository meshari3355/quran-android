package com.quranapp.android.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// ===== DTOs for Metals API =====

data class MetalsApiResponse(
    val code: String? = null,
    val rates: Map<String, Double>? = null,
    val error: String? = null
)

// ===== Zakat Models =====

enum class KaratType(val purity: Double) {
    KARAT_24(1.0),
    KARAT_22(0.916),
    KARAT_21(0.875),
    KARAT_18(0.75),
    KARAT_14(0.585)
}

enum class ZakatItemType {
    GOLD, SILVER, CASH, TRADE_GOODS
}

data class ZakatNisab(
    val goldGramsRequired: Double = 85.0,
    val silverGramsRequired: Double = 595.0,
    val goldPrice: Double = 0.0,
    val silverPrice: Double = 0.0,
    val goldNisabAmount: Double = 0.0,
    val silverNisabAmount: Double = 0.0,
    val timestamp: Long = 0L
)

data class ZakatCalculation(
    val itemType: ZakatItemType,
    val totalAmount: Double,
    val nisabThreshold: Double,
    val zakatDue: Double,
    val isAboveNisab: Boolean
)

// ===== Retrofit API Interface =====

interface MetalsApiInterface {
    @GET("latest")
    suspend fun getMetalPrices(
        @Query("api_key") apiKey: String,
        @Query("base") base: String = "USD",
        @Query("currencies") currencies: String = "XAU,XAG"
    ): Response<MetalsApiResponse>
}

// ===== Retrofit Client =====

private object MetalsRetrofitClient {
    private const val BASE_URL = "https://api.metals.live/v1/"
    private const val TIMEOUT_SECONDS = 30L

    val apiService: MetalsApiInterface by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MetalsApiInterface::class.java)
    }
}

// ===== Zakat Service =====

class ZakatService(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) {
    private val api = MetalsRetrofitClient.apiService

    companion object {
        // Fallback prices (USD per troy ounce)
        private const val FALLBACK_GOLD_PRICE = 2000.0
        private const val FALLBACK_SILVER_PRICE = 25.0
        private const val CACHE_KEY_NISAB = "zakat_nisab_cache"
        private const val CACHE_KEY_TIMESTAMP = "zakat_nisab_timestamp"
        private const val CACHE_DURATION_HOURS = 24
        private const val API_KEY = "e9c78d6d-e1c8-426a-a88c-1e99faa23eb4" // Free tier key
        private const val TROY_OUNCE_TO_GRAMS = 31.1035
        private const val ZAKAT_RATE = 0.025 // 2.5%
    }

    // ===== Nisab Calculation =====

    suspend fun calculateNisab(): Result<ZakatNisab> = withContext(Dispatchers.IO) {
        runCatching {
            // Check cache first
            val cached = getCachedNisab()
            if (cached != null) {
                return@runCatching cached
            }

            // Fetch current prices
            val (goldPrice, silverPrice) = fetchMetalPrices()

            // Convert from per troy ounce to per gram
            val goldPricePerGram = goldPrice / TROY_OUNCE_TO_GRAMS
            val silverPricePerGram = silverPrice / TROY_OUNCE_TO_GRAMS

            val nisab = ZakatNisab(
                goldGramsRequired = 85.0,
                silverGramsRequired = 595.0,
                goldPrice = goldPricePerGram,
                silverPrice = silverPricePerGram,
                goldNisabAmount = 85.0 * goldPricePerGram,
                silverNisabAmount = 595.0 * silverPricePerGram,
                timestamp = System.currentTimeMillis()
            )

            cacheNisab(nisab)
            nisab
        }
    }

    // ===== Zakat Calculations =====

    suspend fun calculateZakat(
        itemType: ZakatItemType,
        totalAmount: Double
    ): Result<ZakatCalculation> = withContext(Dispatchers.IO) {
        runCatching {
            val nisab = calculateNisab().getOrThrow()

            val nisabThreshold = when (itemType) {
                ZakatItemType.GOLD -> nisab.goldNisabAmount
                ZakatItemType.SILVER -> nisab.silverNisabAmount
                ZakatItemType.CASH, ZakatItemType.TRADE_GOODS -> {
                    // For cash and trade goods, use the lower of gold or silver nisab
                    minOf(nisab.goldNisabAmount, nisab.silverNisabAmount)
                }
            }

            val isAboveNisab = totalAmount >= nisabThreshold
            val zakatDue = if (isAboveNisab) {
                (totalAmount - nisabThreshold) * ZAKAT_RATE
            } else {
                0.0
            }

            ZakatCalculation(
                itemType = itemType,
                totalAmount = totalAmount,
                nisabThreshold = nisabThreshold,
                zakatDue = zakatDue,
                isAboveNisab = isAboveNisab
            )
        }
    }

    suspend fun calculateGoldZakat(
        grams: Double,
        karat: KaratType = KaratType.KARAT_24
    ): Result<ZakatCalculation> = withContext(Dispatchers.IO) {
        runCatching {
            val nisab = calculateNisab().getOrThrow()

            // Adjust for karat purity
            val pureGold = grams * karat.purity

            // Convert pure grams to monetary value
            val goldValue = pureGold * nisab.goldPrice

            calculateZakat(ZakatItemType.GOLD, goldValue).getOrThrow()
        }
    }

    suspend fun calculateSilverZakat(grams: Double): Result<ZakatCalculation> = withContext(Dispatchers.IO) {
        runCatching {
            val nisab = calculateNisab().getOrThrow()

            val silverValue = grams * nisab.silverPrice
            calculateZakat(ZakatItemType.SILVER, silverValue).getOrThrow()
        }
    }

    suspend fun calculateCashZakat(amount: Double): Result<ZakatCalculation> = withContext(Dispatchers.IO) {
        runCatching {
            calculateZakat(ZakatItemType.CASH, amount).getOrThrow()
        }
    }

    suspend fun calculateTradeGoodsZakat(value: Double): Result<ZakatCalculation> = withContext(Dispatchers.IO) {
        runCatching {
            calculateZakat(ZakatItemType.TRADE_GOODS, value).getOrThrow()
        }
    }

    suspend fun calculateMultipleZakat(
        items: List<Pair<ZakatItemType, Double>>
    ): Result<ZakatCalculation> = withContext(Dispatchers.IO) {
        runCatching {
            val nisab = calculateNisab().getOrThrow()

            val totalValue = items.sumOf { (itemType, amount) ->
                when (itemType) {
                    ZakatItemType.GOLD -> amount
                    ZakatItemType.SILVER -> amount
                    ZakatItemType.CASH -> amount
                    ZakatItemType.TRADE_GOODS -> amount
                }
            }

            val nisabThreshold = minOf(nisab.goldNisabAmount, nisab.silverNisabAmount)
            val isAboveNisab = totalValue >= nisabThreshold
            val zakatDue = if (isAboveNisab) {
                (totalValue - nisabThreshold) * ZAKAT_RATE
            } else {
                0.0
            }

            ZakatCalculation(
                itemType = ZakatItemType.CASH, // Represent as cash for combined calculation
                totalAmount = totalValue,
                nisabThreshold = nisabThreshold,
                zakatDue = zakatDue,
                isAboveNisab = isAboveNisab
            )
        }
    }

    // ===== Metal Price Fetching =====

    private suspend fun fetchMetalPrices(): Pair<Double, Double> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getMetalPrices(API_KEY)

            if (response.isSuccessful && response.body() != null) {
                val rates = response.body()!!.rates ?: emptyMap()
                val goldPrice = rates["XAU"]?.toDouble() ?: FALLBACK_GOLD_PRICE
                val silverPrice = rates["XAG"]?.toDouble() ?: FALLBACK_SILVER_PRICE
                Pair(goldPrice, silverPrice)
            } else {
                Pair(FALLBACK_GOLD_PRICE, FALLBACK_SILVER_PRICE)
            }
        } catch (e: Exception) {
            Pair(FALLBACK_GOLD_PRICE, FALLBACK_SILVER_PRICE)
        }
    }

    suspend fun updatePrices(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val nisab = calculateNisab().getOrThrow()
            // Cache is automatically updated in calculateNisab
        }
    }

    // ===== Cache Management =====

    private fun getCachedNisab(): ZakatNisab? {
        return try {
            val json = sharedPreferences.getString(CACHE_KEY_NISAB, null) ?: return null
            val timestamp = sharedPreferences.getLong(CACHE_KEY_TIMESTAMP, 0)

            val cacheAgeHours = (System.currentTimeMillis() - timestamp) / (1000 * 60 * 60)
            if (cacheAgeHours > CACHE_DURATION_HOURS) {
                clearCache()
                return null
            }

            gson.fromJson(json, ZakatNisab::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun cacheNisab(nisab: ZakatNisab) {
        sharedPreferences.edit()
            .putString(CACHE_KEY_NISAB, gson.toJson(nisab))
            .putLong(CACHE_KEY_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun clearCache() {
        sharedPreferences.edit()
            .remove(CACHE_KEY_NISAB)
            .remove(CACHE_KEY_TIMESTAMP)
            .apply()
    }

    // ===== Utility Methods =====

    fun getKaratPurityInfo(): List<Pair<KaratType, String>> {
        return listOf(
            KaratType.KARAT_24 to "24K (100% Pure)",
            KaratType.KARAT_22 to "22K (91.6% Pure)",
            KaratType.KARAT_21 to "21K (87.5% Pure)",
            KaratType.KARAT_18 to "18K (75% Pure)",
            KaratType.KARAT_14 to "14K (58.5% Pure)"
        )
    }

    fun formatCurrency(amount: Double, currencySymbol: String = "$"): String {
        return String.format("%s%.2f", currencySymbol, amount)
    }
}
