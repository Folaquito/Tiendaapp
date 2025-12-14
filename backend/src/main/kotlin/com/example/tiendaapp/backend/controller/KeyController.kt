package com.example.tiendaapp.backend.controller

import com.example.tiendaapp.backend.dto.LoadKeysRequest
import com.example.tiendaapp.backend.dto.LoadKeysResponse
import com.example.tiendaapp.backend.model.Key
import com.example.tiendaapp.backend.model.KeyStatus
import com.example.tiendaapp.backend.repository.KeyRepository
import com.example.tiendaapp.backend.repository.ProductoRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.SecureRandom

@RestController
@RequestMapping("/api/keys")
@CrossOrigin(origins = ["http://localhost:8081", "http://10.0.2.2:8081", "*"])
class KeyController(
    private val keyRepository: KeyRepository,
    private val productoRepository: ProductoRepository
) {
    private val random = SecureRandom()
    private val alphabet = (('A'..'Z') + ('0'..'9')).toTypedArray()

    @PostMapping("/cargar")
    @ResponseStatus(HttpStatus.CREATED)
    fun cargar(@RequestBody request: LoadKeysRequest): LoadKeysResponse {
        require(request.quantity > 0) { "La cantidad debe ser mayor a cero" }
        val producto = productoRepository.findById(request.productId).orElseThrow {
            IllegalArgumentException("Producto no encontrado")
        }
        val generated = (1..request.quantity).map {
            val code = generateCode(producto.nombre)
            Key(code = code, status = KeyStatus.AVAILABLE, producto = producto)
        }
        val saved = keyRepository.saveAll(generated)
        val available = keyRepository.countByProductoAndStatus(producto, KeyStatus.AVAILABLE).toInt()
        val updatedProducto = producto.copy(stock = available)
        productoRepository.save(updatedProducto)
        return LoadKeysResponse(
            generatedCodes = saved.map { it.code },
            availableKeys = available
        )
    }

    private fun generateCode(name: String): String {
        val prefix = name.take(4).uppercase().padEnd(4, 'X')
        val blocks = List(3) { randomBlock() }
        return (listOf(prefix) + blocks).joinToString("-")
    }

    private fun randomBlock(): String = (1..4)
        .map { alphabet[random.nextInt(alphabet.size)] }
        .joinToString("")
}
