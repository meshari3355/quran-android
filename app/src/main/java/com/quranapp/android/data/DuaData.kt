package com.quranapp.android.data

import com.quranapp.android.models.Zikr

enum class DuaCategory(val arabicName: String, val englishName: String) {
    DAILY("الأدعية اليومية", "Daily Duas"),
    QURANIC("الأدعية القرآنية", "Quranic Duas"),
    PROPHETIC("الأدعية النبوية", "Prophetic Duas"),
    FORGIVENESS("أدعية المغفرة", "Duas for Forgiveness"),
    PROVISION("أدعية الرزق", "Duas for Provision"),
    HEALTH("أدعية الصحة والشفاء", "Duas for Health"),
    FAMILY("أدعية الأهل والأطفال", "Duas for Family"),
    PROTECTION("أدعية الحماية", "Duas for Protection"),
    KNOWLEDGE("أدعية العلم", "Duas for Knowledge"),
    HAPPINESS("أدعية السعادة والرضا", "Duas for Happiness")
}

object DuaData {

    fun getAllDuaCategories(): Map<DuaCategory, List<Zikr>> = mapOf(
        DuaCategory.DAILY to getDailyDuas(),
        DuaCategory.QURANIC to getQuranicDuas(),
        DuaCategory.PROPHETIC to getPropheticDuas(),
        DuaCategory.FORGIVENESS to getForgivenessLuas(),
        DuaCategory.PROVISION to getProvisionDuas(),
        DuaCategory.HEALTH to getHealthDuas(),
        DuaCategory.FAMILY to getFamilyDuas(),
        DuaCategory.PROTECTION to getProtectionDuas(),
        DuaCategory.KNOWLEDGE to getKnowledgeDuas(),
        DuaCategory.HAPPINESS to getHappinessDuas()
    )

    private fun getDailyDuas(): List<Zikr> = listOf(
        Zikr(
            id = "d_1",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا وَرِزْقًا طَيِّبًا وَعَمَلًا مُتَقَبَّلًا",
            count = 1,
            source = "سنن ابن ماجه",
            category = "daily"
        ),
        Zikr(
            id = "d_2",
            text = "اللَّهُمَّ بِك أَصْبَحْنَا وَبِك أَمْسَيْنَا وَبِك نَحْيَا وَبِك نَمُوتُ وَإِلَيْكَ النُّشُورُ",
            count = 1,
            source = "سنن الترمذي",
            category = "daily"
        ),
        Zikr(
            id = "d_3",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ رِضَاك وَالْجَنَّةَ، وَأَعُوذُ بِك مِنْ سَخَطِك وَالنَّارِ",
            count = 1,
            source = "السنة",
            category = "daily"
        ),
        Zikr(
            id = "d_4",
            text = "اللَّهُمَّ آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            count = 1,
            source = "صحيح البخاري",
            category = "daily"
        ),
        Zikr(
            id = "d_5",
            text = "اللَّهُمَّ اغْفِرْ لِي ذَنْبِي كُلَّهُ دِقَّهُ وَجِلَّهُ أَوَّلَهُ وَآخِرَهُ عَلَانِيَتَهُ وَسِرَّهُ",
            count = 1,
            source = "صحيح مسلم",
            category = "daily"
        ),
        Zikr(
            id = "d_6",
            text = "اللَّهُمَّ إِنِّي أَعُوذُ بِك مِنَ الْهَمِّ وَالْحَزَنِ وَالْعَجْزِ وَالْكَسَلِ وَالْبُخْلِ وَالْجُبْنِ وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            count = 1,
            source = "صحيح البخاري",
            category = "daily"
        ),
        Zikr(
            id = "d_7",
            text = "اللَّهُمَّ عَالِمَ الْغَيْبِ وَالشَّهَادَةِ فَاطِرَ السَّمَاوَاتِ وَالْأَرْضِ رَبَّ كُلِّ شَيْءٍ وَمَلِيكَهُ",
            count = 1,
            source = "صحيح مسلم",
            category = "daily"
        ),
        Zikr(
            id = "d_8",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ أَنْ تَجْعَلَ حَيَاتِي حَيَاةً طَيِّبَةً وَمَمَاتِي مَمَاتًا حَسَنًا",
            count = 1,
            source = "السنة",
            category = "daily"
        ),
        Zikr(
            id = "d_9",
            text = "اللَّهُمَّ يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِك أَسْتَغِيثُ",
            count = 1,
            source = "سنن الترمذي",
            category = "daily"
        ),
        Zikr(
            id = "d_10",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ",
            count = 1,
            source = "سنن ابن ماجه",
            category = "daily"
        )
    )

    private fun getQuranicDuas(): List<Zikr> = listOf(
        Zikr(
            id = "q_1",
            text = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            count = 1,
            source = "سورة البقرة 201",
            category = "quranic"
        ),
        Zikr(
            id = "q_2",
            text = "رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَلِمَن دَخَلَ بَيْتِيَ مُؤْمِنًا وَلِلْمُؤْمِنِينَ وَالْمُؤْمِنَاتِ",
            count = 1,
            source = "سورة نوح 28",
            category = "quranic"
        ),
        Zikr(
            id = "q_3",
            text = "رَبَّنَا إِنَّنَا آمَنَّا فَاغْفِرْ لَنَا ذُنُوبَنَا وَقِنَا عَذَابَ النَّارِ",
            count = 1,
            source = "سورة آل عمران 16",
            category = "quranic"
        ),
        Zikr(
            id = "q_4",
            text = "رَبِّ أَنِي مَسَّنِيَ الضُّرُّ وَأَنْتَ أَرْحَمُ الرَّاحِمِينَ",
            count = 1,
            source = "سورة الأنبياء 83",
            category = "quranic"
        ),
        Zikr(
            id = "q_5",
            text = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
            count = 1,
            source = "سورة الأنبياء 87",
            category = "quranic"
        ),
        Zikr(
            id = "q_6",
            text = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي",
            count = 1,
            source = "سورة طه 25-26",
            category = "quranic"
        ),
        Zikr(
            id = "q_7",
            text = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً",
            count = 1,
            source = "سورة آل عمران 8",
            category = "quranic"
        ),
        Zikr(
            id = "q_8",
            text = "رَبَّنَا اغْفِرْ لَنَا وَلِإِخْوَانِنَا الَّذِينَ سَبَقُونَا بِالْإِيمَانِ",
            count = 1,
            source = "سورة الحشر 10",
            category = "quranic"
        ),
        Zikr(
            id = "q_9",
            text = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ أَشْهَدُ أَن لَّا إِلَهَ إِلَّا أَنْتَ أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
            count = 1,
            source = "السنة",
            category = "quranic"
        ),
        Zikr(
            id = "q_10",
            text = "رَبِّ أَوْزِعْنِي أَنْ أَشْكُرَ نِعْمَتَكَ الَّتِي أَنْعَمْتَ عَلَيَّ وَعَلَىٰ وَالِدَيَّ",
            count = 1,
            source = "سورة الأحقاف 15",
            category = "quranic"
        )
    )

    private fun getPropheticDuas(): List<Zikr> = listOf(
        Zikr(
            id = "p_1",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الهدى والتُّقى والعفاف والغنى",
            count = 1,
            source = "صحيح مسلم",
            category = "prophetic"
        ),
        Zikr(
            id = "p_2",
            text = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ",
            count = 1,
            source = "صحيح البخاري",
            category = "prophetic"
        ),
        Zikr(
            id = "p_3",
            text = "اللَّهُمَّ لَا تَكِلْنِي إِلَىٰ نَفْسِي طَرْفَةَ عَيْنٍ",
            count = 1,
            source = "سنن الترمذي",
            category = "prophetic"
        ),
        Zikr(
            id = "p_4",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الثَّبَاتَ فِي الْأَمْرِ وَالْعَزِيمَةَ عَلَىٰ الرُّشْدِ",
            count = 1,
            source = "سنن الترمذي",
            category = "prophetic"
        ),
        Zikr(
            id = "p_5",
            text = "اللَّهُمَّ اجْعَلْ لِي نُورًا فِي قَلْبِي وَنُورًا فِي لِسَانِي وَنُورًا فِي سَمْعِي",
            count = 1,
            source = "صحيح مسلم",
            category = "prophetic"
        ),
        Zikr(
            id = "p_6",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ حُبَّكَ وَحُبَّ مَن يُحِبُّكَ",
            count = 1,
            source = "سنن الترمذي",
            category = "prophetic"
        ),
        Zikr(
            id = "p_7",
            text = "اللَّهُمَّ يَسِّرْ وَلَا تُعَسِّرْ، وَتَمِّمْ بِالْخَيْرِ",
            count = 1,
            source = "السنة",
            category = "prophetic"
        ),
        Zikr(
            id = "p_8",
            text = "اللَّهُمَّ رَبَّ السَّمَاوَاتِ وَالْأَرْضِ وَرَبَّ الْعَرْشِ الْعَظِيمِ رَبَّنَا وَرَبَّ كُلِّ شَيْءٍ",
            count = 1,
            source = "سنن الترمذي",
            category = "prophetic"
        ),
        Zikr(
            id = "p_9",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْقَوْلَ الصَّادِقَ فِي الْغَضَبِ وَالرِّضَا",
            count = 1,
            source = "السنة",
            category = "prophetic"
        ),
        Zikr(
            id = "p_10",
            text = "اللَّهُمَّ لَا تَدَعْ لَنَا ذَنْبًا إِلَّا غَفَرْتَهُ وَلَا هَمًّا إِلَّا فَرَّجْتَهُ",
            count = 1,
            source = "السنة",
            category = "prophetic"
        )
    )

    private fun getForgivenessLuas(): List<Zikr> = listOf(
        Zikr(
            id = "f_1",
            text = "أَسْتَغْفِرُ اللَّهَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
            count = 1,
            source = "سنن الترمذي",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_2",
            text = "اللَّهُمَّ اغْفِرْ لِي ذَنْبِي كُلَّهُ دِقَّهُ وَجِلَّهُ",
            count = 1,
            source = "صحيح مسلم",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_3",
            text = "سَبْحَانَ اللَّهِ وَبِحَمْدِهِ أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            count = 100,
            source = "صحيح البخاري",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_4",
            text = "يَا أَرْحَمَ الرَّاحِمِينَ اغْفِرْ لِي",
            count = 1,
            source = "السنة",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_5",
            text = "اللَّهُمَّ اغْفِرْ لِي وَارْحَمْنِي وَاهْدِنِي وَعَافِنِي وَارْزُقْنِي",
            count = 1,
            source = "صحيح مسلم",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_6",
            text = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            count = 1,
            source = "سنن الترمذي",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_7",
            text = "اللَّهُمَّ اغْفِرْ لِي إِسْرَافِي فِي أَمْرِي كُلِّهِ",
            count = 1,
            source = "السنة",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_8",
            text = "يَا رَبِّ اغْفِرْ وَارْحَمْ وَأَنْتَ خَيْرُ الرَّاحِمِينَ",
            count = 1,
            source = "السنة",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_9",
            text = "اللَّهُمَّ لَا تُؤَاخِذْنِي بِمَا نَسِيتُ",
            count = 1,
            source = "القرآن الكريم",
            category = "forgiveness"
        ),
        Zikr(
            id = "f_10",
            text = "اللَّهُمَّ غَفْرَكَ أَوْسَعُ مِنْ ذُنُوبِي وَرَحْمَتُكَ أَرْجَىٰ عِندِي مِنْ عَمَلِي",
            count = 1,
            source = "السنة",
            category = "forgiveness"
        )
    )

    private fun getProvisionDuas(): List<Zikr> = listOf(
        Zikr(
            id = "r_1",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ رِزْقًا حَلَالًا طَيِّبًا وَفِيرًا",
            count = 1,
            source = "السنة",
            category = "provision"
        ),
        Zikr(
            id = "r_2",
            text = "اللَّهُمَّ ارْزُقْنِي حَلَالًا وَاحْرِمْنِي الْحَرَامَ",
            count = 1,
            source = "سنن ابن ماجه",
            category = "provision"
        ),
        Zikr(
            id = "r_3",
            text = "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا",
            count = 1,
            source = "صحيح البخاري",
            category = "provision"
        ),
        Zikr(
            id = "r_4",
            text = "اللَّهُمَّ اجْعَلْ رِزْقِي فِي مَدِينَتِي",
            count = 1,
            source = "السنة",
            category = "provision"
        ),
        Zikr(
            id = "r_5",
            text = "اللَّهُمَّ أَغْنِنِي بِحِلِّك عَن حَرَامِك وَبِطَاعَتِك عَن مَعْصِيَتِك",
            count = 1,
            source = "سنن الترمذي",
            category = "provision"
        ),
        Zikr(
            id = "r_6",
            text = "اللَّهُمَّ كَفِنِي بِحَلَالِك عَن حَرَامِك وَأَغْنِنِي بِفَضْلِك عَن سِوَاك",
            count = 1,
            source = "سنن الترمذي",
            category = "provision"
        ),
        Zikr(
            id = "r_7",
            text = "اللَّهُمَّ يَسِّرْ لِي الرِّزْقَ الْحَلَالَ",
            count = 1,
            source = "السنة",
            category = "provision"
        ),
        Zikr(
            id = "r_8",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ رِزْقًا يَأْتِيَنِي مِن حَيْثُ لَا أَحْتَسِبُ",
            count = 1,
            source = "السنة",
            category = "provision"
        ),
        Zikr(
            id = "r_9",
            text = "رَبِّ اجْعَل لِي عِندَكَ عَهْدًا وَارْزُقْنِي رِزْقًا حَسَنًا",
            count = 1,
            source = "القرآن الكريم",
            category = "provision"
        ),
        Zikr(
            id = "r_10",
            text = "اللَّهُمَّ اجْعَلْ رِزْقِي رِزْقًا يَأْتِيَنِي بِغَيْرِ كَدٍّ",
            count = 1,
            source = "السنة",
            category = "provision"
        )
    )

    private fun getHealthDuas(): List<Zikr> = listOf(
        Zikr(
            id = "h_1",
            text = "اللَّهُمَّ اشْفِنِي شِفَاءً لَيْسَ بَعْدَهُ سَقَمٌ",
            count = 1,
            source = "السنة",
            category = "health"
        ),
        Zikr(
            id = "h_2",
            text = "اللَّهُمَّ إِنِّي أَعُوذُ بِك مِنَ مُرِّ الْقَضَاءِ وَمِنْ دَرَكِ الشَّقَاءِ",
            count = 1,
            source = "السنة",
            category = "health"
        ),
        Zikr(
            id = "h_3",
            text = "لَا بَأْسَ طَهُورٌ إِنْ شَاءَ اللَّهُ",
            count = 1,
            source = "صحيح البخاري",
            category = "health"
        ),
        Zikr(
            id = "h_4",
            text = "اللَّهُمَّ عَافِنِي وَعَافِ الْمُسْلِمِينَ",
            count = 1,
            source = "السنة",
            category = "health"
        ),
        Zikr(
            id = "h_5",
            text = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ",
            count = 1,
            source = "سنن الترمذي",
            category = "health"
        ),
        Zikr(
            id = "h_6",
            text = "أَعُوذُ بِعِزَّةِ اللَّهِ وَقُدْرَتِهِ مِن شَرِّ مَا أَجِدُ وَأُحَاذِرُ",
            count = 7,
            source = "صحيح مسلم",
            category = "health"
        ),
        Zikr(
            id = "h_7",
            text = "اللَّهُمَّ الشِّفَاءَ شِفَاؤُك لَا شِفَاءَ إِلَّا شِفَاؤُك",
            count = 1,
            source = "السنة",
            category = "health"
        ),
        Zikr(
            id = "h_8",
            text = "اللَّهُمَّ رُدَّنَا إِلَىٰ دِينِك رَدًّا جَمِيلًا",
            count = 1,
            source = "السنة",
            category = "health"
        ),
        Zikr(
            id = "h_9",
            text = "اللَّهُمَّ عَافِنَا فِي أَبْدَانِنَا",
            count = 1,
            source = "السنة",
            category = "health"
        ),
        Zikr(
            id = "h_10",
            text = "اللَّهُمَّ إِنِّي أَعُوذُ بِك مِنَ الْبَرَصِ وَالْجُنُونِ وَالْجُذَامِ",
            count = 1,
            source = "سنن أبي داود",
            category = "health"
        )
    )

    private fun getFamilyDuas(): List<Zikr> = listOf(
        Zikr(
            id = "f_1",
            text = "رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَلِمَن دَخَلَ بَيْتِيَ مُؤْمِنًا",
            count = 1,
            source = "سورة نوح 28",
            category = "family"
        ),
        Zikr(
            id = "f_2",
            text = "اللَّهُمَّ أَصْلِحْ لِي أَهْلِي وَمَالِي",
            count = 1,
            source = "السنة",
            category = "family"
        ),
        Zikr(
            id = "f_3",
            text = "اللَّهُمَّ اجْعَلْنَا مِمَّنْ يَخْشَاكَ وَيَحُبُّك وَيَتَّقِيك",
            count = 1,
            source = "السنة",
            category = "family"
        ),
        Zikr(
            id = "f_4",
            text = "اللَّهُمَّ بَارِكْ لَنَا فِي أَزْوَاجِنَا وَذُرِّيَّاتِنَا",
            count = 1,
            source = "السنة",
            category = "family"
        ),
        Zikr(
            id = "f_5",
            text = "اللَّهُمَّ آتِ نَفْسِي تَقْوَاهَا وَزَكِّهَا أَنْتَ خَيْرُ مَن زَكَّاهَا",
            count = 1,
            source = "صحيح مسلم",
            category = "family"
        ),
        Zikr(
            id = "f_6",
            text = "اللَّهُمَّ ارْزُقْنِي ذُرِّيَّةً طَيِّبَةً",
            count = 1,
            source = "السنة",
            category = "family"
        ),
        Zikr(
            id = "f_7",
            text = "اللَّهُمَّ احْفَظْ أَهْلِي بِحِفْظِك",
            count = 1,
            source = "السنة",
            category = "family"
        ),
        Zikr(
            id = "f_8",
            text = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ",
            count = 1,
            source = "سورة الفرقان 74",
            category = "family"
        ),
        Zikr(
            id = "f_9",
            text = "اللَّهُمَّ أَصْلِحِ ذُرِّيَّتِي وَزَوْجِي",
            count = 1,
            source = "السنة",
            category = "family"
        ),
        Zikr(
            id = "f_10",
            text = "اللَّهُمَّ اجْمَعْ بَيْنَنَا عَلَىٰ خَيْرٍ",
            count = 1,
            source = "السنة",
            category = "family"
        )
    )

    private fun getProtectionDuas(): List<Zikr> = listOf(
        Zikr(
            id = "p_1",
            text = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِن شَرِّ مَا خَلَقَ",
            count = 3,
            source = "صحيح مسلم",
            category = "protection"
        ),
        Zikr(
            id = "p_2",
            text = "أَعُوذُ بِاللَّهِ السَّمِيعِ الْعَلِيمِ مِنَ الشَّيْطَانِ الرَّجِيمِ",
            count = 3,
            source = "القرآن الكريم",
            category = "protection"
        ),
        Zikr(
            id = "p_3",
            text = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            count = 7,
            source = "القرآن الكريم",
            category = "protection"
        ),
        Zikr(
            id = "p_4",
            text = "اللَّهُمَّ احْفَظْنِي بِالْإِسْلَامِ قَائِمًا",
            count = 1,
            source = "السنة",
            category = "protection"
        ),
        Zikr(
            id = "p_5",
            text = "اللَّهُمَّ احْفَظْنِي مِنْ أَمَامِي وَمِنْ خَلْفِي وَعَن يَمِينِي وَعَن شِمَالِي",
            count = 1,
            source = "سنن الترمذي",
            category = "protection"
        ),
        Zikr(
            id = "p_6",
            text = "بِسْمِ اللَّهِ أَدْخُلُ وَبِسْمِ اللَّهِ أَخْرُجُ",
            count = 1,
            source = "السنة",
            category = "protection"
        ),
        Zikr(
            id = "p_7",
            text = "اللَّهُمَّ إِنِّي أَعُوذُ بِك مِن شَرِّ الشَّيْطَانِ وَشِرْكِهِ",
            count = 1,
            source = "السنة",
            category = "protection"
        ),
        Zikr(
            id = "p_8",
            text = "اللَّهُمَّ احْفَظْنِي حِفْظَكَ",
            count = 1,
            source = "السنة",
            category = "protection"
        ),
        Zikr(
            id = "p_9",
            text = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            count = 1,
            source = "صحيح البخاري",
            category = "protection"
        ),
        Zikr(
            id = "p_10",
            text = "اللَّهُمَّ احْفَظْنِي مِن كُلِّ سُوءٍ",
            count = 1,
            source = "السنة",
            category = "protection"
        )
    )

    private fun getKnowledgeDuas(): List<Zikr> = listOf(
        Zikr(
            id = "k_1",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا",
            count = 1,
            source = "سنن ابن ماجه",
            category = "knowledge"
        ),
        Zikr(
            id = "k_2",
            text = "رَبِّ زِدْنِي عِلْمًا وَفَهْمًا",
            count = 1,
            source = "القرآن الكريم",
            category = "knowledge"
        ),
        Zikr(
            id = "k_3",
            text = "اللَّهُمَّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِن لِّسَانِي",
            count = 1,
            source = "صحيح مسلم",
            category = "knowledge"
        ),
        Zikr(
            id = "k_4",
            text = "اللَّهُمَّ يَسِّرْ عَلَيَّ مَا أَتَعَلَّمُهُ",
            count = 1,
            source = "السنة",
            category = "knowledge"
        ),
        Zikr(
            id = "k_5",
            text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ فِقْهًا فِي الدِّينِ",
            count = 1,
            source = "صحيح البخاري",
            category = "knowledge"
        ),
        Zikr(
            id = "k_6",
            text = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ الْعِلْمِ",
            count = 1,
            source = "السنة",
            category = "knowledge"
        ),
        Zikr(
            id = "k_7",
            text = "اللَّهُمَّ أَنِرْ قَلْبِي بِنُورِ الْعِلْمِ",
            count = 1,
            source = "السنة",
            category = "knowledge"
        ),
        Zikr(
            id = "k_8",
            text = "اللَّهُمَّ اجْعَلْنِي عَالِمًا عَامِلًا",
            count = 1,
            source = "السنة",
            category = "knowledge"
        ),
        Zikr(
            id = "k_9",
            text = "اللَّهُمَّ عَلِّمْنِي مَا يَنْفَعُنِي وَنَفِّعْنِي بِمَا عَلَّمْتَنِي",
            count = 1,
            source = "سنن الترمذي",
            category = "knowledge"
        ),
        Zikr(
            id = "k_10",
            text = "اللَّهُمَّ اجْعَلْنِي مِن عُلَمَاءِ الدِّينِ",
            count = 1,
            source = "السنة",
            category = "knowledge"
        )
    )

    private fun getHappinessDuas(): List<Zikr> = listOf(
        Zikr(
            id = "ha_1",
            text = "اللَّهُمَّ اجْعَلْ حَيَاتِي حَيَاةً طَيِّبَةً وَمَمَاتِي مَمَاتًا حَسَنًا",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_2",
            text = "اللَّهُمَّ أَدِمْ عَلَيْنَا نِعَمَك وَأَتْمِمْهَا عَلَيْنَا",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_3",
            text = "اللَّهُمَّ اجْعَلْنَا مِمَّنْ رَضِيَ بِقَضَائِك",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_4",
            text = "اللَّهُمَّ طَيِّبْ عَيْشَنَا وَسَهِّلْ مَعِيشَتَنَا",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_5",
            text = "اللَّهُمَّ اجْعَلْنَا سُعَدَاءَ بِطَاعَتِك",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_6",
            text = "اللَّهُمَّ ارْزُقْنَا شُكْرَ نِعَمِك",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_7",
            text = "اللَّهُمَّ أَسْعِدْ جَنَّتَنَا",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_8",
            text = "اللَّهُمَّ اجْعَلْنَا أَهْلَ السَّعَادَةِ",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_9",
            text = "اللَّهُمَّ اجْعَلْ سَرُورَنَا فِي طَاعَتِك",
            count = 1,
            source = "السنة",
            category = "happiness"
        ),
        Zikr(
            id = "ha_10",
            text = "اللَّهُمَّ ارْزُقْنَا السِّعَادَةَ وَالرِّضَا",
            count = 1,
            source = "السنة",
            category = "happiness"
        )
    )
}
