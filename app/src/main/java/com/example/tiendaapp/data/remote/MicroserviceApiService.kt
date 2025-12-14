package com.example.tiendaapp.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MicroserviceApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/productos")
    suspend fun listarProductos(): List<ProductoDto>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoDto

    @POST("api/productos")
    suspend fun crearProducto(@Body producto: ProductoDto): ProductoDto

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: ProductoDto
    ): ProductoDto

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long)
}
