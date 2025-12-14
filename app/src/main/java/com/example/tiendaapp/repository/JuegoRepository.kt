package com.example.tiendaapp.repository

import android.util.Log
import com.example.tiendaapp.data.local.JuegoDao
import com.example.tiendaapp.data.remote.ApiService
import com.example.tiendaapp.data.remote.BackendService
import com.example.tiendaapp.data.remote.MicroserviceApiService
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class JuegoRepository(
    private val api: ApiService,
    private val dao: JuegoDao,
    private val backend: BackendService,
    private val microservice: MicroserviceApiService
) {

    val games: Flow<List<JuegoEntity>> = dao.getAllGames()

    suspend fun refreshGames() {
        try {
            val productos = microservice.listarProductos()
            val gamesEntities = productos.map { it.toEntity() }
            dao.deleteAllGames()
            dao.insertGames(gamesEntities)

            // Enriquecer con datos RAWG para filtros (género/plataforma) cuando haya rawgGameId
            gamesEntities.forEach { entity ->
                val rawgId = entity.rawgGameId ?: return@forEach
                try {
                    val apiKey = com.example.tiendaapp.BuildConfig.RAWG_API_KEY
                    val detail = api.getGameDetail(rawgId, apiKey)
                    val enriched = entity.copy(
                        description = detail.description ?: entity.description,
                        imageUrl = if (entity.imageUrl.isBlank()) detail.backgroundImage ?: entity.imageUrl else entity.imageUrl,
                        genres = detail.genres ?: entity.genres,
                        platforms = detail.platforms ?: entity.platforms,
                        esrbRating = detail.esrbRating?.name ?: entity.esrbRating
                    )
                    dao.insertGames(listOf(enriched))
                } catch (e: Exception) {
                    Log.w("GamesRepository", "RAWG enrich failed for $rawgId: ${e.message}")
                }
            }

        } catch (e: Exception) {
            Log.e("GamesRepository", "Error fetching data: ${e.message}")
            e.printStackTrace()
        }
    }
    fun getGameById(id: Int): Flow<JuegoEntity?> = dao.getGameById(id)

    suspend fun fetchGameDescription(id: Int) {
        try {
            val current = dao.getGameById(id).firstOrNull() ?: return
            val rawgId = current.rawgGameId ?: return
            val apiKey = com.example.tiendaapp.BuildConfig.RAWG_API_KEY
            val response = api.getGameDetail(rawgId, apiKey)

            val updated = current.copy(
                description = response.description ?: current.description,
                imageUrl = if (current.imageUrl.isBlank()) response.backgroundImage ?: current.imageUrl else current.imageUrl,
                genres = response.genres ?: current.genres,
                platforms = response.platforms ?: current.platforms,
                esrbRating = response.esrbRating?.name ?: current.esrbRating
            )
            dao.insertGames(listOf(updated))
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
