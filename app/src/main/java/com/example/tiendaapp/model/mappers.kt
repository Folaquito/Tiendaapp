package com.example.tiendaapp.model

import com.example.tiendaapp.data.remote.ProductoDto

fun Juego.toEntity(): JuegoEntity {
    return JuegoEntity(
        id = this.id,
        name = this.name,
        imageUrl = this.backgroundImage ?: "",
        rating = this.rating,
        price = when {
            this.rating > 4.5 -> 64990
            this.rating > 4.0 -> 49990
            this.rating > 3.0 -> 29990
            else -> 14990
        },
        stock = 0,
        description = null
    )
}

fun ProductoDto.toEntity(): JuegoEntity = JuegoEntity(
    id = (id ?: 0).toInt(),
    name = nombre,
    imageUrl = imagen,
    rating = valoracion,
    price = precio,
    stock = stock,
    description = descripcion
)

fun JuegoEntity.toProductoDto(): ProductoDto = ProductoDto(
    id = id.toLong(),
    nombre = name,
    descripcion = description ?: "Sin descripción",
    imagen = imageUrl,
    precio = price,
    valoracion = rating,
    stock = stock
)
