package com.example.tiendaapp.model

data class CartItem(
    val game: JuegoEntity,
    val cantidad: Int = 1
)
