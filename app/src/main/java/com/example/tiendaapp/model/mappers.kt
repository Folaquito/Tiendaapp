package com.example.tiendaapp.model

import com.example.tiendaapp.data.remote.ProductoDto

fun Juego.toEntity(): JuegoEntity {
    return JuegoEntity(
        id = this.id,
        rawgGameId = this.id,
        name = this.name,
        imageUrl = this.backgroundImage ?: "",
        rating = this.rating,
        price = when {
            this.rating > 4.5 -> 24990
            this.rating > 4.0 -> 19990
            this.rating > 3.0 -> 14990
            else -> 9990
        },
        stock = 0,
        description = null,
        genres = this.genres,
        platforms = this.platforms,
        esrbRating = this.esrbRating?.name
    )
}

fun ProductoDto.toEntity(): JuegoEntity = JuegoEntity(
    id = (id ?: 0).toInt(),
    rawgGameId = rawgGameId?.toInt(),
    name = nombre,
    imageUrl = imagen,
    rating = valoracion,
    price = precio,
    stock = stock,
    description = descripcion,
    genres = null,
    platforms = null,
    esrbRating = null
)

fun JuegoEntity.toProductoDto(): ProductoDto = ProductoDto(
    id = id.toLong(),
    rawgGameId = rawgGameId?.toLong(),
    nombre = name,
    descripcion = description ?: "Sin descripción",
    imagen = imageUrl ?: "",
    precio = price,
    valoracion = rating,
    stock = stock
)