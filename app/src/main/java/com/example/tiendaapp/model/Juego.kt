package com.example.tiendaapp.model

data class Juego(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val genero: String,
    val imagen: String,
    val imagend1: String,
    val imagend2: String,
    val valoracion: Int,
    val descripcionlarga: String,
    val comentario1: String,
    val comentario2: String
)