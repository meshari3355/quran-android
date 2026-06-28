package com.quranapp.android.models

import com.google.gson.annotations.SerializedName

data class Zikr(
    @SerializedName("id")
    val id: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("source")
    val source: String,
    @SerializedName("category")
    val category: String
)

data class ZikrCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nameEn")
    val nameEn: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("items")
    val items: List<Zikr>
)

enum class ZikrCategoryType(val arabicName: String, val englishName: String, val iconName: String) {
    MORNING("أذكار الصباح", "Morning Azkar", "ic_morning"),
    EVENING("أذكار المساء", "Evening Azkar", "ic_evening"),
    AFTER_PRAYER("أذكار بعد الصلاة", "After Prayer", "ic_prayer"),
    SLEEP("أذكار قبل النوم", "Before Sleep", "ic_sleep"),
    WAKING("أذكار عند الاستيقاظ", "Upon Waking", "ic_waking"),
    ENTERING_HOME("عند دخول المنزل", "Entering Home", "ic_home"),
    LEAVING_HOME("عند الخروج من المنزل", "Leaving Home", "ic_leaving"),
    EATING("عند الطعام", "Before Eating", "ic_eating"),
    RIDING("عند الركوب", "When Riding", "ic_riding"),
    TRAVELING("أذكار السفر", "Travel Azkar", "ic_travel"),
    VISITING_SICK("عند عيادة المريض", "Visiting the Sick", "ic_sick"),
    GENERAL("أذكار عامة", "General Azkar", "ic_general")
}
