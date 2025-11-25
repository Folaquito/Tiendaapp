package com.example.tiendaapp.model


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
        description = null

    )
}