package com.quranapp.android.data

import com.quranapp.android.models.Reciter

object ReciterData {
    private val reciters = Reciter.getAllReciters()

    fun getAll(): List<Reciter> = reciters

    fun getById(id: Int): Reciter? = reciters.find { it.id == id }

    fun getByName(name: String): Reciter? = reciters.find { it.nameAr == name }

    fun getReciterCount(): Int = reciters.size

    fun getDefaultReciter(): Reciter = reciters.first()
}
