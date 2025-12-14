package com.example.tiendaapp.backend.controller

import com.example.tiendaapp.backend.dto.AuthResponse
import com.example.tiendaapp.backend.dto.LoginRequest
import com.example.tiendaapp.backend.dto.RegisterRequest
import com.example.tiendaapp.backend.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:8081", "http://10.0.2.2:8081", "*"])
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterRequest): AuthResponse = authService.register(request)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): AuthResponse = authService.login(request)
}
