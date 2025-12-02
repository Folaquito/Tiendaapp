package com.example.tiendaapp.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.backend", "com.example.tiendaapp.backend"])
class TiendaBackendApplication

fun main(args: Array<String>) {
    runApplication<TiendaBackendApplication>(*args)
}
