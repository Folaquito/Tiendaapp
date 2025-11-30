package com.example.tiendaapp.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendService {
    @GET("favorites")
    suspend fun getFavorites(): List<FavoriteGameDto>

    @POST("favorites")
    suspend fun addFavorite(@Body game: FavoriteGameDto): FavoriteGameDto

    @DELETE("favorites/{gameId}")
    suspend fun removeFavorite(@Path("gameId") gameId: Int)

    @PUT("favorites/{gameId}")
    suspend fun updateFavorite(@Path("gameId") gameId: Int, @Body game: FavoriteGameDto): FavoriteGameDto
}

data class FavoriteGameDto(
    val id: Long? = null,
    val gameId: Int,
    val title: String,
    val imageUrl: String,
    val note: String? = null
)
