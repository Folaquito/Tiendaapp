package com.example.tiendaapp.data.local

import androidx.room.TypeConverter
import com.example.tiendaapp.model.Genre
import com.example.tiendaapp.model.PlatformContainer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGenreList(value: List<Genre>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGenreList(value: String?): List<Genre>? {
        val listType = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromPlatformList(value: List<PlatformContainer>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPlatformList(value: String?): List<PlatformContainer>? {
        val listType = object : TypeToken<List<PlatformContainer>>() {}.type
        return gson.fromJson(value, listType)
    }
}