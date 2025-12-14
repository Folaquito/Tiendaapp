package com.example.tiendaapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games_table")
data class JuegoEntity(
    @PrimaryKey
    val id: Int,
    val rawgGameId: Int? = null,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val price: Int,
    val stock: Int = 0,
    val description: String? = null,
    val genres: List<Genre>?,
    val platforms: List<PlatformContainer>?,
    val esrbRating: String?
)
