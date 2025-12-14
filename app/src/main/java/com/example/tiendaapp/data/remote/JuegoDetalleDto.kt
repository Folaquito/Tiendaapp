package com.example.tiendaapp.data.remote

import com.example.tiendaapp.model.EsrbRating
import com.example.tiendaapp.model.Genre
import com.example.tiendaapp.model.PlatformContainer
import com.google.gson.annotations.SerializedName

data class JuegoDetalleDto(
    @SerializedName("id") val id: Int,
    @SerializedName("description_raw") val description: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("genres") val genres: List<Genre>?,
    @SerializedName("parent_platforms") val platforms: List<PlatformContainer>?,
    @SerializedName("esrb_rating") val esrbRating: EsrbRating?
)