package com.example.tiendaapp.repository

import android.util.Log
import com.example.tiendaapp.data.local.JuegoDao
import com.example.tiendaapp.data.remote.ApiService
import com.example.tiendaapp.data.remote.BackendService
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.model.toEntity
import kotlinx.coroutines.flow.Flow

class JuegoRepository(
    private val api: ApiService,
    private val dao: JuegoDao,
    private val backend: com.example.tiendaapp.data.remote.BackendService,
    private val microservice: com.example.tiendaapp.data.remote.MicroserviceApiService
) {

    val games: Flow<List<JuegoEntity>> = dao.getAllGames()

    suspend fun refreshGames() {
        try {
            val apiKey = com.example.tiendaapp.BuildConfig.RAWG_API_KEY
            val response = api.getGames(apiKey = apiKey)

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
            val response = api.getGameDetail(id, "llave")

            if (response.description.isNotEmpty()) {
                dao.updateGameDescription(id, response.description)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Backend Integration
    suspend fun getFavorites(): List<com.example.tiendaapp.data.remote.FavoriteGameDto> {
        return try {
            backend.getFavorites()
        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error fetching favorites: ${e.message}")
            emptyList()
        }
    }

    suspend fun addFavorite(game: com.example.tiendaapp.data.remote.FavoriteGameDto) {
        try {
            backend.addFavorite(game)
        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error adding favorite: ${e.message}")
        }
    }

    suspend fun removeFavorite(gameId: Int) {
        try {
            backend.removeFavorite(gameId)
        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error removing favorite: ${e.message}")
        }
    }

    suspend fun updateFavoriteNote(gameId: Int, note: String) {
        try {
            // Backend only uses the note field for the PUT request
            val dummyDto = com.example.tiendaapp.data.remote.FavoriteGameDto(
                gameId = gameId,
                title = "",
                imageUrl = "",
                note = note
            )
            backend.updateFavorite(gameId, dummyDto)
        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error updating note: ${e.message}")
        }
    }

    suspend fun crearProducto(producto: com.example.tiendaapp.data.remote.ProductoDto) {
        try {
            microservice.crearProducto(producto)
        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error creating product: ${e.message}")
            throw e
        }
    }
}
