package com.kimlic.db.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromCountries(countries: List<String>): String {
        return gson.toJson(countries)
    }

    @TypeConverter
    fun toCountries(data: String?): List<String> {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }
}