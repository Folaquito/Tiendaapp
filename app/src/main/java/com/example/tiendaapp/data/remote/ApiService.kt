package com.example.tiendaapp.data.remote

import com.example.tiendaapp.model.GameResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): GameResponse
    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int,
        @Query("key") apiKey: String
    ): JuegoDetalleDto
}
