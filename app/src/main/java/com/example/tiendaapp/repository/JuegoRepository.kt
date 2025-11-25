package com.example.tiendaapp.repository

import android.util.Log
import com.example.tiendaapp.data.local.JuegoDao
import com.example.tiendaapp.data.remote.ApiService
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.model.toEntity
import kotlinx.coroutines.flow.Flow

class JuegoRepository(
    private val api: ApiService,
    private val dao: JuegoDao
) {

    val games: Flow<List<JuegoEntity>> = dao.getAllGames()

    suspend fun refreshGames() {
        try {

            val response = api.getGames(apiKey = "")

            val gamesEntities = response.results.map { dto ->
                dto.toEntity()
            }

            dao.insertGames(gamesEntities)

        } catch (e: Exception) {
            Log.e("GamesRepository", "Error fetching data: ${e.message}")
            e.printStackTrace()
        }
    }
    fun getGameById(id: Int): Flow<JuegoEntity?> = dao.getGameById(id)

    suspend fun fetchGameDescription(id: Int) {
        try {
            val response = api.getGameDetail(id, "")

            if (response.description.isNotEmpty()) {
                dao.updateGameDescription(id, response.description)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}