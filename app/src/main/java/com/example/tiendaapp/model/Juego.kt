package com.example.tiendaapp.model

data class Juego(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val genero: String,
    val imagen: String
)