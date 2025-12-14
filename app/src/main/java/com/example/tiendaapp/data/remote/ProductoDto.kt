package com.example.tiendaapp.data.remote

data class ProductoDto(
    val id: Long? = null,
    val rawgGameId: Long? = null,
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val precio: Int,
    val valoracion: Double,
    val stock: Int
)
