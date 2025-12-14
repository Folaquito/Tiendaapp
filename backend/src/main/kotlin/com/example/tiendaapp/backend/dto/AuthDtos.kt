package com.example.tiendaapp.backend.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)
