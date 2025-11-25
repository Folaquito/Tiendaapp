package com.example.tiendaapp.repository

import android.util.Log
import com.example.tiendaapp.BuildConfig
import com.example.tiendaapp.data.local.JuegoDao
import com.example.tiendaapp.data.remote.ApiService
import com.example.tiendaapp.data.remote.MicroserviceApiService
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.model.toEntity
import kotlinx.coroutines.flow.Flow

class JuegoRepository(
    private val rawgApi: ApiService,
    private val backendApi: MicroserviceApiService,
    private val dao: JuegoDao
) {

    val games: Flow<List<JuegoEntity>> = dao.getAllGames()

    suspend fun refreshGames() {
        try {
            val backendGames = backendApi.listarProductos()
            dao.deleteAllGames()
            dao.insertGames(backendGames.map { it.toEntity() })
        } catch (e: Exception) {
            Log.e("GamesRepository", "Error fetching data: ${e.message}")
            e.printStackTrace()
        }
    }
    fun getGameById(id: Int): Flow<JuegoEntity?> = dao.getGameById(id)

    suspend fun fetchGameDescription(id: Int) {
        if (BuildConfig.RAWG_API_KEY.isBlank()) return

        try {
            val response = rawgApi.getGameDetail(id, BuildConfig.RAWG_API_KEY)

            if (response.description.isNotEmpty()) {
                dao.updateGameDescription(id, response.description)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchExternalGames(limit: Int = 5): List<JuegoEntity> {
        if (BuildConfig.RAWG_API_KEY.isBlank()) {
            throw IllegalStateException("RAWG_API_KEY no configurada en local.properties")
        }

        return try {
            val response = rawgApi.getGames(
                apiKey = BuildConfig.RAWG_API_KEY,
                pageSize = limit
            )
            response.results.map { it.toEntity() }
        } catch (e: Exception) {
            Log.e("GamesRepository", "Error fetching RAWG data: ${e.message}")
            throw e
        }
    }

    suspend fun crearProducto(
        nombre: String,
        descripcion: String,
        imagen: String,
        precio: Int,
        valoracion: Double,
        stock: Int
    ) {
        try {
            backendApi.crearProducto(
                com.example.tiendaapp.data.remote.ProductoDto(
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    precio = precio,
                    valoracion = valoracion,
                    stock = stock
                )
            )
            refreshGames()
        } catch (e: Exception) {
            Log.e("GamesRepository", "Error creating product: ${e.message}")
            throw e
        }
    }

    suspend fun eliminarProducto(id: Int) {
        try {
            backendApi.eliminarProducto(id.toLong())
            refreshGames()
        } catch (e: Exception) {
            Log.e("GamesRepository", "Error deleting product: ${e.message}")
            throw e
        }
    }
}
