package com.example.tiendaapp.backend.dto

import com.example.tiendaapp.backend.model.Favorite

data class FavoriteDto(
    val id: Long? = null,
    val gameId: Int,
    val title: String,
    val imageUrl: String,
    val note: String? = null
)

fun Favorite.toDto() = FavoriteDto(
    id = id,
    gameId = gameId,
    title = title,
    imageUrl = imageUrl,
    note = note
)

fun FavoriteDto.toEntity(existingId: Long? = null) = Favorite(
    id = existingId ?: id ?: 0,
    gameId = gameId,
    title = title,
    imageUrl = imageUrl,
    note = note
)
