package com.example.tiendaapp.model

import com.google.gson.annotations.SerializedName

data class GameResponse(
    @SerializedName("results")
    val results: List<Juego>
)

data class Juego(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    @SerializedName("rating")
    val rating: Double
)