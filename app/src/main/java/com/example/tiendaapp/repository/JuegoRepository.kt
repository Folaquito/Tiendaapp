package com.example.tiendaapp.repository

import android.content.Context
import com.example.tiendaapp.model.Juego
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class JuegoRepository(private val context: Context) {
    suspend fun obtenerJuegos(): List<Juego> {
        return try {
            val jsonString = context.assets.open("juegos.json")
                .bufferedReader()
                .use { it.readText() }
            val listType = object : TypeToken<List<Juego>>() {}.type
            Gson().fromJson(jsonString, listType)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            emptyList()
        }
    }
}