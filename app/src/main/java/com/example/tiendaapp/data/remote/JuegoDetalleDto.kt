package com.example.tiendaapp.data.remote

import com.google.gson.annotations.SerializedName

data class JuegoDetalleDto(
    @SerializedName("id") val id: Int,
    @SerializedName("description_raw") val description: String
)