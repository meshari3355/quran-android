package com.quranapp.android.data

import com.quranapp.android.models.Zikr
import com.quranapp.android.models.ZikrCategory
import com.quranapp.android.models.ZikrCategoryType

object AzkarData {

    fun getAllAzkarCategories(): List<ZikrCategory> = listOf(
        getMorningAzkar(),
        getEveningAzkar(),
        getAfterPrayerAzkar(),
        getSleepAzkar(),
        getWakingAzkar(),
        getEnteringHomeAzkar(),
        getLeavingHomeAzkar(),
        getEatingAzkar(),
        getRidingAzkar(),
        getTravelingAzkar(),
        getVisitingSickAzkar(),
        getGeneralAzkar()
    )

    private fun getMorningAzkar(): ZikrCategory = ZikrCategory(
        id = "1",
        name = "أذكار الصباح",
        nameEn = "Morning Azkar",
        icon = "ic_morning",
        color = "#FF9C27B0",
        items = listOf(
            Zikr(
                id = "m_1",
                text = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ رَبِّ الْعَالَمِينَ، اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَذَا الْيَوْمِ، فَتْحَهُ، وَنَصْرَهُ، وَنُورَهُ، وَبَرَكَتَهُ، وَهِدَايَتَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِيهِ وَشَرِّ مَا بَعْدَهُ",
                count = 1,
                source = "السنن الصغرى للنسائي",
                category = "morning"
            ),
            Zikr(
                id = "m_2",
                text = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
                count = 1,
                source = "سنن الترمذي",
                category = "morning"
            ),
            Zikr(
                id = "m_3",
                text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                count = 100,
                source = "صحيح البخاري",
                category = "morning"
            ),
            Zikr(
                id = "m_4",
                text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                count = 10,
                source = "صحيح البخاري",
                category = "morning"
            ),
            Zikr(
                id = "m_5",
                text = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ، بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                count = 1,
                source = "القرآن الكريم",
                category = "morning"
            ),
            Zikr(
                id = "m_6",
                text = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
                count = 7,
                source = "سنن أبي داود",
                category = "morning"
            ),
            Zikr(
                id = "m_7",
                text = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ",
                count = 3,
                source = "سنن الترمذي",
                category = "morning"
            ),
            Zikr(
                id = "m_8",
                text = "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ",
                count = 10,
                source = "صحيح مسلم",
                category = "morning"
            ),
            Zikr(
                id = "m_9",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ",
                count = 3,
                source = "سنن ابن ماجه",
                category = "morning"
            ),
            Zikr(
                id = "m_10",
                text = "اللَّهُمَّ إِنَّ نَفْسِي أَنَاخَتْ بِفِنَاءِ بَابِكَ، وَعَرَاصُ حِمَاكَ، تَطْلُبُ رِضَاكَ وَمَغْفِرَتَكَ",
                count = 1,
                source = "مسند الإمام أحمد",
                category = "morning"
            )
        )
    )

    private fun getEveningAzkar(): ZikrCategory = ZikrCategory(
        id = "2",
        name = "أذكار المساء",
        nameEn = "Evening Azkar",
        icon = "ic_evening",
        color = "#FF2196F3",
        items = listOf(
            Zikr(
                id = "e_1",
                text = "أَمْسَيْنَا وَأَمْسَىٰ الْمُلْكُ لِلَّهِ رَبِّ الْعَالَمِينَ، اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَٰذِهِ اللَّيْلَةِ، فَتْحَهَا، وَنَصْرَهَا، وَنُورَهَا، وَبَرَكَتَهَا، وَهِدَايَتَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِيهَا وَشَرِّ مَا بَعْدَهَا",
                count = 1,
                source = "السنن الصغرى للنسائي",
                category = "evening"
            ),
            Zikr(
                id = "e_2",
                text = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
                count = 1,
                source = "سنن الترمذي",
                category = "evening"
            ),
            Zikr(
                id = "e_3",
                text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                count = 100,
                source = "صحيح البخاري",
                category = "evening"
            ),
            Zikr(
                id = "e_4",
                text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                count = 10,
                source = "صحيح البخاري",
                category = "evening"
            ),
            Zikr(
                id = "e_5",
                text = "أَعُوذُ بِاللَّهِ السَّمِيعِ الْعَلِيمِ مِنَ الشَّيْطَانِ الرَّجِيمِ",
                count = 3,
                source = "القرآن الكريم",
                category = "evening"
            ),
            Zikr(
                id = "e_6",
                text = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
                count = 7,
                source = "سنن أبي داود",
                category = "evening"
            ),
            Zikr(
                id = "e_7",
                text = "اللَّهُمَّ عَالِمَ الْغَيْبِ وَالشَّهَادَةِ فَاطِرَ السَّمَاوَاتِ وَالْأَرْضِ رَبَّ كُلِّ شَيْءٍ وَمَلِيكَهُ",
                count = 1,
                source = "صحيح مسلم",
                category = "evening"
            ),
            Zikr(
                id = "e_8",
                text = "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ",
                count = 10,
                source = "صحيح مسلم",
                category = "evening"
            ),
            Zikr(
                id = "e_9",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ",
                count = 3,
                source = "سنن ابن ماجه",
                category = "evening"
            ),
            Zikr(
                id = "e_10",
                text = "يَا أَرْحَمَ الرَّاحِمِينَ",
                count = 10,
                source = "القرآن الكريم",
                category = "evening"
            )
        )
    )

    private fun getAfterPrayerAzkar(): ZikrCategory = ZikrCategory(
        id = "3",
        name = "أذكار بعد الصلاة",
        nameEn = "After Prayer Azkar",
        icon = "ic_prayer",
        color = "#FF4CAF50",
        items = listOf(
            Zikr(
                id = "p_1",
                text = "أَسْتَغْفِرُ اللَّهَ",
                count = 3,
                source = "صحيح مسلم",
                category = "prayer"
            ),
            Zikr(
                id = "p_2",
                text = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
                count = 1,
                source = "صحيح مسلم",
                category = "prayer"
            ),
            Zikr(
                id = "p_3",
                text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                count = 10,
                source = "صحيح البخاري",
                category = "prayer"
            ),
            Zikr(
                id = "p_4",
                text = "سُبْحَانَ اللَّهِ",
                count = 33,
                source = "صحيح مسلم",
                category = "prayer"
            ),
            Zikr(
                id = "p_5",
                text = "الْحَمْدُ لِلَّهِ",
                count = 33,
                source = "صحيح مسلم",
                category = "prayer"
            ),
            Zikr(
                id = "p_6",
                text = "اللَّهُ أَكْبَرُ",
                count = 34,
                source = "صحيح مسلم",
                category = "prayer"
            ),
            Zikr(
                id = "p_7",
                text = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
                count = 1,
                source = "صحيح البخاري",
                category = "prayer"
            ),
            Zikr(
                id = "p_8",
                text = "اللَّهُمَّ اجْعَلْ أَفْضَلَ صَلَوَاتِكَ وَأَفْضَلَ سَلَامِكَ عَلَىٰ سَيِّدِنَا مُحَمَّدٍ وَعَلَىٰ آلِهِ وَصَحْبِهِ",
                count = 1,
                source = "السنة",
                category = "prayer"
            ),
            Zikr(
                id = "p_9",
                text = "رَبِّ اغْفِرْ لِي",
                count = 10,
                source = "السنة",
                category = "prayer"
            ),
            Zikr(
                id = "p_10",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ كُلِّ خَيْرٍ وَأَعُوذُ بِكَ مِنْ كُلِّ شَرٍّ",
                count = 1,
                source = "السنة",
                category = "prayer"
            )
        )
    )

    private fun getSleepAzkar(): ZikrCategory = ZikrCategory(
        id = "4",
        name = "أذكار قبل النوم",
        nameEn = "Before Sleep Azkar",
        icon = "ic_sleep",
        color = "#FF673AB7",
        items = listOf(
            Zikr(
                id = "s_1",
                text = "بِسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                count = 1,
                source = "صحيح البخاري",
                category = "sleep"
            ),
            Zikr(
                id = "s_2",
                text = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
                count = 3,
                source = "سنن الترمذي",
                category = "sleep"
            ),
            Zikr(
                id = "s_3",
                text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                count = 100,
                source = "صحيح البخاري",
                category = "sleep"
            ),
            Zikr(
                id = "s_4",
                text = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي وَسَقَانِي، وَكَفَانِي، وَآوَاهُ",
                count = 1,
                source = "سنن الترمذي",
                category = "sleep"
            ),
            Zikr(
                id = "s_5",
                text = "اللَّهُمَّ بِاسْمِكَ أَحْيَا وَأَمُوتُ",
                count = 1,
                source = "صحيح البخاري",
                category = "sleep"
            ),
            Zikr(
                id = "s_6",
                text = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                count = 3,
                source = "صحيح مسلم",
                category = "sleep"
            ),
            Zikr(
                id = "s_7",
                text = "اللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ",
                count = 1,
                source = "صحيح مسلم",
                category = "sleep"
            ),
            Zikr(
                id = "s_8",
                text = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا",
                count = 1,
                source = "صحيح البخاري",
                category = "sleep"
            ),
            Zikr(
                id = "s_9",
                text = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                count = 1,
                source = "القرآن الكريم",
                category = "sleep"
            ),
            Zikr(
                id = "s_10",
                text = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ",
                count = 7,
                source = "السنة",
                category = "sleep"
            )
        )
    )

    private fun getWakingAzkar(): ZikrCategory = ZikrCategory(
        id = "5",
        name = "أذكار عند الاستيقاظ",
        nameEn = "Upon Waking Azkar",
        icon = "ic_waking",
        color = "#FFFF9800",
        items = listOf(
            Zikr(
                id = "w_1",
                text = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                count = 1,
                source = "صحيح البخاري",
                category = "waking"
            ),
            Zikr(
                id = "w_2",
                text = "الْحَمْدُ لِلَّهِ الَّذِي عَافَانِي فِي جَسَدِي وَرَدَّ عَلَيَّ رُوحِي وَأَذِنَ لِي بِذِكْرِهِ",
                count = 1,
                source = "سنن الترمذي",
                category = "waking"
            ),
            Zikr(
                id = "w_3",
                text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                count = 1,
                source = "صحيح البخاري",
                category = "waking"
            ),
            Zikr(
                id = "w_4",
                text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                count = 100,
                source = "صحيح البخاري",
                category = "waking"
            ),
            Zikr(
                id = "w_5",
                text = "أَصْبَحْنَا عَلَىٰ فِطْرَةِ اللَّهِ",
                count = 1,
                source = "صحيح مسلم",
                category = "waking"
            ),
            Zikr(
                id = "w_6",
                text = "سُبْحَانَ اللَّهِ",
                count = 33,
                source = "صحيح مسلم",
                category = "waking"
            ),
            Zikr(
                id = "w_7",
                text = "الْحَمْدُ لِلَّهِ",
                count = 33,
                source = "صحيح مسلم",
                category = "waking"
            ),
            Zikr(
                id = "w_8",
                text = "اللَّهُ أَكْبَرُ",
                count = 34,
                source = "صحيح مسلم",
                category = "waking"
            ),
            Zikr(
                id = "w_9",
                text = "اللَّهُمَّ بِكَ أَصْبَحْنَا وَبِكَ أَمْسَيْنَا",
                count = 1,
                source = "السنة",
                category = "waking"
            ),
            Zikr(
                id = "w_10",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا وَرِزْقًا طَيِّبًا وَعَمَلًا مُتَقَبَّلًا",
                count = 1,
                source = "السنة",
                category = "waking"
            )
        )
    )

    private fun getEnteringHomeAzkar(): ZikrCategory = ZikrCategory(
        id = "6",
        name = "عند دخول المنزل",
        nameEn = "Entering Home Azkar",
        icon = "ic_home",
        color = "#FFE91E63",
        items = listOf(
            Zikr(
                id = "h_1",
                text = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَىٰ رَبِّنَا تَوَكَّلْنَا",
                count = 1,
                source = "سنن الترمذي",
                category = "home"
            ),
            Zikr(
                id = "h_2",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ الْمَوْلَجِ وَخَيْرَ الْمَخْرَجِ، بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا",
                count = 1,
                source = "سنن أبي داود",
                category = "home"
            ),
            Zikr(
                id = "h_3",
                text = "الْحَمْدُ لِلَّهِ الَّذِي رَدَّنَا إِلَىٰ دَارِنَا سَالِمِينَ",
                count = 1,
                source = "صحيح مسلم",
                category = "home"
            ),
            Zikr(
                id = "h_4",
                text = "اللَّهُمَّ حَبِّبْ إِلَيْنَا الْمَدِينَةَ كَمَا حَبَّبْتَ إِلَيْنَا مَكَّةَ أَوْ أَشَدَّ",
                count = 1,
                source = "صحيح البخاري",
                category = "home"
            ),
            Zikr(
                id = "h_5",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا وَرِزْقًا وَاسِعًا وَشِفَاءً مِنْ كُلِّ سُقْمٍ",
                count = 1,
                source = "السنة",
                category = "home"
            ),
            Zikr(
                id = "h_6",
                text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                count = 10,
                source = "السنة",
                category = "home"
            ),
            Zikr(
                id = "h_7",
                text = "الْحَمْدُ لِلَّهِ عَلَىٰ كُلِّ حَالٍ",
                count = 1,
                source = "السنة",
                category = "home"
            ),
            Zikr(
                id = "h_8",
                text = "اللَّهُمَّ بَارِكْ لَنَا فِيهَا",
                count = 1,
                source = "السنة",
                category = "home"
            ),
            Zikr(
                id = "h_9",
                text = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                count = 1,
                source = "السنة",
                category = "home"
            ),
            Zikr(
                id = "h_10",
                text = "اللَّهُمَّ احْفَظْ لَنَا أَهْلَنَا وَأَمْوَالَنَا",
                count = 1,
                source = "السنة",
                category = "home"
            )
        )
    )

    private fun getLeavingHomeAzkar(): ZikrCategory = ZikrCategory(
        id = "7",
        name = "عند الخروج من المنزل",
        nameEn = "Leaving Home Azkar",
        icon = "ic_leaving",
        color = "#FF00BCD4",
        items = listOf(
            Zikr(
                id = "l_1",
                text = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَىٰ اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                count = 1,
                source = "سنن الترمذي",
                category = "leaving"
            ),
            Zikr(
                id = "l_2",
                text = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ أَنْ أَضِلَّ أَوْ أُضَلَّ، أَوْ أَزِلَّ أَوْ أُزَلَّ، أَوْ أَظْلِمَ أَوْ أُظْلَمَ، أَوْ أَجْهَلَ أَوْ يُجْهَلَ عَلَيَّ",
                count = 1,
                source = "سنن الترمذي",
                category = "leaving"
            ),
            Zikr(
                id = "l_3",
                text = "بِسْمِ اللَّهِ وَعَلَىٰ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَذَا الْمَخْرَجِ",
                count = 1,
                source = "سنن أبي داود",
                category = "leaving"
            ),
            Zikr(
                id = "l_4",
                text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
                count = 1,
                source = "السنة",
                category = "leaving"
            ),
            Zikr(
                id = "l_5",
                text = "اللَّهُمَّ احْفَظْنِي مِنْ أَمَامِي وَمِنْ خَلْفِي وَعَنْ يَمِينِي وَعَنْ شِمَالِي وَمِنْ فَوْقِي وَأَعُوذُ بِعَظَمَتِكَ أَنْ أُغْتَالَ مِنْ تَحْتِي",
                count = 1,
                source = "سنن الترمذي",
                category = "leaving"
            ),
            Zikr(
                id = "l_6",
                text = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ",
                count = 7,
                source = "السنة",
                category = "leaving"
            ),
            Zikr(
                id = "l_7",
                text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                count = 10,
                source = "السنة",
                category = "leaving"
            ),
            Zikr(
                id = "l_8",
                text = "اللَّهُمَّ بِكَ أَصْبَحْنَا وَبِكَ نَسِيرُ",
                count = 1,
                source = "السنة",
                category = "leaving"
            ),
            Zikr(
                id = "l_9",
                text = "اللَّهُمَّ يَسِّرْ لِي كُلَّ عُسْرٍ",
                count = 1,
                source = "السنة",
                category = "leaving"
            ),
            Zikr(
                id = "l_10",
                text = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ",
                count = 1,
                source = "السنة",
                category = "leaving"
            )
        )
    )

    private fun getEatingAzkar(): ZikrCategory = ZikrCategory(
        id = "8",
        name = "عند الطعام",
        nameEn = "Before Eating Azkar",
        icon = "ic_eating",
        color = "#FFC2185D",
        items = listOf(
            Zikr(
                id = "f_1",
                text = "بِسْمِ اللَّهِ",
                count = 1,
                source = "صحيح البخاري",
                category = "eating"
            ),
            Zikr(
                id = "f_2",
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                count = 1,
                source = "السنة",
                category = "eating"
            ),
            Zikr(
                id = "f_3",
                text = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                count = 1,
                source = "سنن الترمذي",
                category = "eating"
            ),
            Zikr(
                id = "f_4",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ بِرَحْمَتِكَ أَنْ تُطْعِمَ الْجَائِعَ وَتَسْقِيَ الظَّمْآنَ",
                count = 1,
                source = "السنة",
                category = "eating"
            ),
            Zikr(
                id = "f_5",
                text = "الْحَمْدُ لِلَّهِ الَّذِي أَغْنَاهُ وَأَطْعَمَهُ",
                count = 1,
                source = "السنة",
                category = "eating"
            ),
            Zikr(
                id = "f_6",
                text = "شَكَرْتُ الرَّازِقَ عَلَىٰ نِعَمِهِ",
                count = 1,
                source = "السنة",
                category = "eating"
            ),
            Zikr(
                id = "f_7",
                text = "الْحَمْدُ لِلَّهِ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
                count = 1,
                source = "صحيح البخاري",
                category = "eating"
            ),
            Zikr(
                id = "f_8",
                text = "اللَّهُمَّ بَارِكْ لَنَا فِيهِ",
                count = 1,
                source = "السنة",
                category = "eating"
            ),
            Zikr(
                id = "f_9",
                text = "اللَّهُمَّ احْمِ رَزْقَنَا مِنَ الْآفَاتِ",
                count = 1,
                source = "السنة",
                category = "eating"
            ),
            Zikr(
                id = "f_10",
                text = "بِحَمْدِ اللَّهِ رَبِّ الْعَالَمِينَ",
                count = 1,
                source = "السنة",
                category = "eating"
            )
        )
    )

    private fun getRidingAzkar(): ZikrCategory = ZikrCategory(
        id = "9",
        name = "عند الركوب",
        nameEn = "When Riding Azkar",
        icon = "ic_riding",
        color = "#FF3F51B5",
        items = listOf(
            Zikr(
                id = "r_1",
                text = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ",
                count = 1,
                source = "صحيح مسلم",
                category = "riding"
            ),
            Zikr(
                id = "r_2",
                text = "الْحَمْدُ لِلَّهِ ثُمَّ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ",
                count = 1,
                source = "صحيح مسلم",
                category = "riding"
            ),
            Zikr(
                id = "r_3",
                text = "بِسْمِ اللَّهِ، الْحَمْدُ لِلَّهِ، سُبْحَانَ اللَّهِ",
                count = 1,
                source = "السنة",
                category = "riding"
            ),
            Zikr(
                id = "r_4",
                text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ",
                count = 1,
                source = "السنة",
                category = "riding"
            ),
            Zikr(
                id = "r_5",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ هَٰذَا الْمَسِيرِ",
                count = 1,
                source = "السنة",
                category = "riding"
            ),
            Zikr(
                id = "r_6",
                text = "اللَّهُمَّ احْفَظْنِي وَاحْفَظْ مَا مَعِي",
                count = 1,
                source = "السنة",
                category = "riding"
            ),
            Zikr(
                id = "r_7",
                text = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ",
                count = 7,
                source = "السنة",
                category = "riding"
            ),
            Zikr(
                id = "r_8",
                text = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                count = 1,
                source = "السنة",
                category = "riding"
            ),
            Zikr(
                id = "r_9",
                text = "اللَّهُمَّ بِكَ أَرُوحُ وَبِكَ أَسِيرُ",
                count = 1,
                source = "السنة",
                category = "riding"
            ),
            Zikr(
                id = "r_10",
                text = "اللَّهُمَّ يَسِّرْ لِي سَفَرِي هَذَا",
                count = 1,
                source = "السنة",
                category = "riding"
            )
        )
    )

    private fun getTravelingAzkar(): ZikrCategory = ZikrCategory(
        id = "10",
        name = "أذكار السفر",
        nameEn = "Travel Azkar",
        icon = "ic_travel",
        color = "#FFFF5722",
        items = listOf(
            Zikr(
                id = "t_1",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ فِي سَفَرِي هَٰذَا الْبِرَّ وَالتَّقْوَىٰ، وَمِنَ الْعَمَلِ مَا تَرْضَىٰ",
                count = 1,
                source = "صحيح مسلم",
                category = "travel"
            ),
            Zikr(
                id = "t_2",
                text = "سُبْحَانَ اللَّهِ سُبْحَانَ اللَّهِ سُبْحَانَ اللَّهِ",
                count = 1,
                source = "السنة",
                category = "travel"
            ),
            Zikr(
                id = "t_3",
                text = "الْحَمْدُ لِلَّهِ الَّذِي سَهَّلَ لَنَا السَّفَرَ",
                count = 1,
                source = "السنة",
                category = "travel"
            ),
            Zikr(
                id = "t_4",
                text = "اللَّهُمَّ اجْعَلْ سَفَرَنَا هَٰذَا سَفَرًا عَبَادَةً",
                count = 1,
                source = "السنة",
                category = "travel"
            ),
            Zikr(
                id = "t_5",
                text = "اللَّهُمَّ احْفَظْنَا فِي سَفَرِنَا هَٰذَا",
                count = 1,
                source = "السنة",
                category = "travel"
            ),
            Zikr(
                id = "t_6",
                text = "إِنَّ اللَّهَ مَعَ السَّائِرِينَ",
                count = 1,
                source = "القرآن الكريم",
                category = "travel"
            ),
            Zikr(
                id = "t_7",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ السَّفَرَ الصَّالِحَ",
                count = 1,
                source = "السنة",
                category = "travel"
            ),
            Zikr(
                id = "t_8",
                text = "اللَّهُمَّ وَفِّرْ أَحْظَاظَنَا وَنَوَايَانَا",
                count = 1,
                source = "السنة",
                category = "travel"
            ),
            Zikr(
                id = "t_9",
                text = "تَوَكَّلْنَا عَلَى اللَّهِ وَعَلَيْهِ فَلْيَتَوَكَّلِ الْمُتَوَكِّلُونَ",
                count = 1,
                source = "القرآن الكريم",
                category = "travel"
            ),
            Zikr(
                id = "t_10",
                text = "اللَّهُمَّ أَعِدْنَا إِلَىٰ دِيَارِنَا سَالِمِينَ",
                count = 1,
                source = "السنة",
                category = "travel"
            )
        )
    )

    private fun getVisitingSickAzkar(): ZikrCategory = ZikrCategory(
        id = "11",
        name = "عند عيادة المريض",
        nameEn = "Visiting the Sick Azkar",
        icon = "ic_sick",
        color = "#FFB71C1C",
        items = listOf(
            Zikr(
                id = "v_1",
                text = "الْحَمْدُ لِلَّهِ الَّذِي أَصَحَّكَ",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_2",
                text = "اللَّهُمَّ اشْفِهِ شِفَاءً لَيْسَ بَعْدَهُ سَقَمٌ",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_3",
                text = "اللَّهُمَّ إِنَّ لَكَ أَحْمَدِ الضُّرِّ، وَأَنْتَ أَرْحَمُ الرَّاحِمِينَ",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_4",
                text = "لَا بَأْسَ طَهُورٌ إِنْ شَاءَ اللَّهُ",
                count = 1,
                source = "صحيح البخاري",
                category = "sick"
            ),
            Zikr(
                id = "v_5",
                text = "اللَّهُمَّ رُدَّهُ إِلَىٰ أَهْلِهِ سَالِمًا",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_6",
                text = "اللَّهُمَّ عَافِهِ مِنْ كُلِّ بَلَاءٍ",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_7",
                text = "سَقَاهُ اللَّهُ شَرَابًا طَاهِرًا",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_8",
                text = "اللَّهُمَّ اجْعَلْ مَرَضَهُ كَفَّارَةً لِذُنُوبِهِ",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_9",
                text = "اللَّهُمَّ رُدَّهُ الشِّفَاءَ",
                count = 1,
                source = "السنة",
                category = "sick"
            ),
            Zikr(
                id = "v_10",
                text = "بِسْمِ اللَّهِ ثَلَاثًا وَقُلْ سَبْعَ مَرَّاتٍ: أَعُوذُ بِعِزَّةِ اللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ",
                count = 1,
                source = "صحيح مسلم",
                category = "sick"
            )
        )
    )

    private fun getGeneralAzkar(): ZikrCategory = ZikrCategory(
        id = "12",
        name = "أذكار عامة",
        nameEn = "General Azkar",
        icon = "ic_general",
        color = "#FF6D4C41",
        items = listOf(
            Zikr(
                id = "g_1",
                text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                count = 1,
                source = "صحيح البخاري",
                category = "general"
            ),
            Zikr(
                id = "g_2",
                text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ عَدَدَ خَلْقِهِ وَرِضَا نَفْسِهِ وَزِنَةَ عَرْشِهِ وَمِدَادَ كَلِمَاتِهِ",
                count = 3,
                source = "صحيح مسلم",
                category = "general"
            ),
            Zikr(
                id = "g_3",
                text = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ عَذَابِ جَهَنَّمَ",
                count = 3,
                source = "السنة",
                category = "general"
            ),
            Zikr(
                id = "g_4",
                text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْجَنَّةَ وَأَعُوذُ بِكَ مِنَ النَّارِ",
                count = 1,
                source = "السنة",
                category = "general"
            ),
            Zikr(
                id = "g_5",
                text = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                count = 1,
                source = "القرآن الكريم",
                category = "general"
            ),
            Zikr(
                id = "g_6",
                text = "سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ وَلَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ",
                count = 10,
                source = "صحيح مسلم",
                category = "general"
            ),
            Zikr(
                id = "g_7",
                text = "اسْتَغْفِرُ اللَّهَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
                count = 1,
                source = "سنن الترمذي",
                category = "general"
            ),
            Zikr(
                id = "g_8",
                text = "الْحَمْدُ لِلَّهِ عَدَدَ أنْفَاسِهِ وَرِضَا نَفْسِهِ وَزِنَةَ عَرْشِهِ وَمِدَادَ كَلِمَاتِهِ",
                count = 1,
                source = "السنة",
                category = "general"
            ),
            Zikr(
                id = "g_9",
                text = "يَا كَافِيُّ يَا غَنِيُّ يَا أَعَزُّ يَا أَحْكَمُ",
                count = 1,
                source = "السنة",
                category = "general"
            ),
            Zikr(
                id = "g_10",
                text = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
                count = 10,
                source = "القرآن الكريم",
                category = "general"
            )
        )
    )
}
