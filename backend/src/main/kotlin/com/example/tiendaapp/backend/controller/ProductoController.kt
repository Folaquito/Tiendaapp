package com.example.tiendaapp.backend.controller

import com.example.tiendaapp.backend.model.Producto
import com.example.tiendaapp.backend.repository.ProductoRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = ["http://localhost:8081", "http://10.0.2.2:8081", "*"])
class ProductoController(
    private val repository: ProductoRepository
) {

    @GetMapping
    fun listar(): Iterable<Producto> = repository.findAll()

    @GetMapping("/{id}")
    fun detalle(@PathVariable id: Long): Producto? = repository.findById(id).orElse(null)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody producto: Producto): Producto = repository.save(producto.copy(id = 0))

    @PutMapping("/{id}")
    fun actualizar(
        @PathVariable id: Long,
        @RequestBody producto: Producto
    ): Producto = repository.save(producto.copy(id = id))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        }
    }
}
