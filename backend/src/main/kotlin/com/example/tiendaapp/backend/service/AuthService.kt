package com.example.tiendaapp.backend.service

import com.example.tiendaapp.backend.dto.AuthResponse
import com.example.tiendaapp.backend.dto.LoginRequest
import com.example.tiendaapp.backend.dto.RegisterRequest
import com.example.tiendaapp.backend.model.User
import com.example.tiendaapp.backend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmailIgnoreCase(request.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already registered")
        }
        if (request.password.length < 6) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters")
        }

        val user = User(
            name = request.name.trim(),
            email = request.email.trim().lowercase(),
            passwordHash = passwordEncoder.encode(request.password),
            role = "USER"
        )

        val saved = userRepository.save(user)
        return AuthResponse(saved.id, saved.name, saved.email, saved.role)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmailIgnoreCase(request.email.trim())
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }

        return AuthResponse(user.id, user.name, user.email, user.role)
    }
}
