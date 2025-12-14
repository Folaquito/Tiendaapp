package com.example.tiendaapp.backend.config

import com.example.tiendaapp.backend.model.User
import com.example.tiendaapp.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminSeeder(
    @Value("\${app.admin.email:admin@tienda.test}") private val adminEmail: String,
    @Value("\${app.admin.password:admin123}") private val adminPassword: String,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val email = adminEmail.trim().lowercase()
        if (userRepository.existsByEmailIgnoreCase(email)) return

        val admin = User(
            name = "Admin",
            email = email,
            passwordHash = passwordEncoder.encode(adminPassword),
            role = "ADMIN"
        )
        userRepository.save(admin)
    }
}
