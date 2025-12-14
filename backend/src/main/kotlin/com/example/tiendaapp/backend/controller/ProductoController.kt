package com.example.tiendaapp.backend.controller

import com.example.tiendaapp.backend.dto.ImportRawgRequest
import com.example.tiendaapp.backend.model.Producto
import com.example.tiendaapp.backend.repository.ProductoRepository
import com.example.tiendaapp.backend.service.RawgImportService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = ["http://localhost:8081", "http://10.0.2.2:8081", "*"])
class ProductoController(
    private val repository: ProductoRepository,
    private val rawgImportService: RawgImportService
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

    @PostMapping("/import/rawg/{rawgId}")
    fun importarDesdeRawg(
        @PathVariable rawgId: Long,
        @RequestBody body: ImportRawgRequest
    ): ResponseEntity<Producto> {
        val result = rawgImportService.importProduct(rawgId, body)
        return if (result.created) {
            ResponseEntity.status(HttpStatus.CREATED).body(result.producto)
        } else {
            ResponseEntity.ok(result.producto)
        }
    }

    @PostMapping("/import/rawg")
    fun importarListaDesdeRawg(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "29990") precio: Int,
        @RequestParam(defaultValue = "5") stock: Int
    ): List<Producto> {
        return rawgImportService.importGames(page, pageSize, precio, stock)
    }
}
