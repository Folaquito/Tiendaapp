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
    val rating: Double,

    @SerializedName("genres")
    val genres: List<Genre>?,

    @SerializedName("parent_platforms")
    val platforms: List<PlatformContainer>?,

    @SerializedName("esrb_rating")
    val esrbRating: EsrbRating?
)

data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class PlatformContainer(
    @SerializedName("platform")
    val platform: Platform
)

data class Platform(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String
)

data class EsrbRating(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)