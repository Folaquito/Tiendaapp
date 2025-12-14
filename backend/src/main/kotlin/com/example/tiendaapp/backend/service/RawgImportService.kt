package com.example.tiendaapp.backend.service

import com.example.tiendaapp.backend.dto.ImportRawgRequest
import com.example.tiendaapp.backend.model.Producto
import com.example.tiendaapp.backend.repository.ProductoRepository
import com.example.tiendaapp.backend.repository.KeyRepository
import com.example.tiendaapp.backend.model.Key
import com.example.tiendaapp.backend.model.KeyStatus
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.SecureRandom

@Service
class RawgImportService(
    private val productoRepository: ProductoRepository,
    private val rawgClient: RawgClient,
    private val keyRepository: KeyRepository
) {

    private val random = SecureRandom()
    private val alphabet = (('A'..'Z') + ('0'..'9')).toTypedArray()
    private val imagePlaceholder = "https://via.placeholder.com/640x360?text=No+Image"

    fun importProduct(rawgId: Long, request: ImportRawgRequest): ImportResult {
        validateRequest(request)
        val rawgGame = rawgClient.getGame(rawgId)

        val nombre = rawgGame.name ?: "Juego sin nombre"
        val descripcion = rawgGame.description_raw ?: rawgGame.description ?: ""
        val imagen = rawgGame.background_image?.takeIf { it.isNotBlank() } ?: imagePlaceholder
        val valoracion = rawgGame.metacritic?.toDouble() ?: rawgGame.rating ?: 0.0

        val existing = productoRepository.findByRawgGameId(rawgId)
        return if (existing != null) {
            val updated = existing.copy(
                nombre = nombre,
                descripcion = descripcion,
                imagen = imagen,
                valoracion = valoracion,
                rawgGameId = rawgId,
                precio = request.precio ?: existing.precio,
                stock = request.stock ?: existing.stock
            )
            ImportResult(productoRepository.save(updated), created = false)
        } else {
            val precio = request.precio ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "precio es obligatorio para crear"
            )
            val stock = request.stock ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "stock es obligatorio para crear"
            )
            val nuevo = Producto(
                rawgGameId = rawgId,
                nombre = nombre,
                descripcion = descripcion,
                imagen = imagen,
                precio = precio,
                valoracion = valoracion,
                stock = stock
            )
            val saved = productoRepository.save(nuevo)
            val withKeys = seedKeys(saved, stock)
            ImportResult(withKeys, created = true)
        }
    }

    fun importGames(page: Int, pageSize: Int, defaultPrice: Int, defaultStock: Int): List<Producto> {
        validateListRequest(page, pageSize, defaultPrice, defaultStock)
        val rawgGames = rawgClient.getGames(page, pageSize).results.orEmpty()
        if (rawgGames.isEmpty()) return emptyList()

        val imported = mutableListOf<Producto>()
        rawgGames.forEach { rawgGame ->
            val gameId = rawgGame.id ?: return@forEach
            val nombre = rawgGame.name ?: return@forEach
            val descripcion = rawgGame.description_raw ?: rawgGame.description ?: ""
            val imagen = rawgGame.background_image?.takeIf { it.isNotBlank() } ?: imagePlaceholder
            val valoracion = rawgGame.metacritic?.toDouble() ?: rawgGame.rating ?: 0.0

            val existing = productoRepository.findByRawgGameId(gameId)
            if (existing != null) {
                val updated = existing.copy(
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    valoracion = valoracion,
                    rawgGameId = gameId,
                    precio = defaultPrice,
                    stock = defaultStock
                )
                val saved = productoRepository.save(updated)
                val withKeys = seedKeys(saved, defaultStock)
                imported.add(withKeys)
            } else {
                val nuevo = Producto(
                    rawgGameId = gameId,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    precio = defaultPrice,
                    valoracion = valoracion,
                    stock = defaultStock
                )
                val saved = productoRepository.save(nuevo)
                val withKeys = seedKeys(saved, defaultStock)
                imported.add(withKeys)
            }
        }
        return imported
    }

    private fun validateRequest(request: ImportRawgRequest) {
        request.precio?.let {
            if (it <= 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "precio debe ser mayor que 0")
        }
        request.stock?.let {
            if (it < 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "stock debe ser 0 o mayor")
        }
    }

    private fun validateListRequest(page: Int, pageSize: Int, price: Int, stock: Int) {
        if (page <= 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "page debe ser mayor que 0")
        if (pageSize <= 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "pageSize debe ser mayor que 0")
        if (price <= 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "precio debe ser mayor que 0")
        if (stock < 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "stock debe ser 0 o mayor")
    }

    private fun seedKeys(producto: Producto, desired: Int): Producto {
        if (desired <= 0) return producto
        val available = keyRepository.countByProductoAndStatus(producto, KeyStatus.AVAILABLE).toInt()
        val missing = desired - available
        if (missing <= 0) return producto.copy(stock = available)

        val generated = (1..missing).map {
            val code = generateCode(producto.nombre)
            Key(code = code, status = KeyStatus.AVAILABLE, producto = producto)
        }
        keyRepository.saveAll(generated)
        val updatedAvailable = keyRepository.countByProductoAndStatus(producto, KeyStatus.AVAILABLE).toInt()
        val updatedProducto = producto.copy(stock = updatedAvailable)
        return productoRepository.save(updatedProducto)
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

data class ImportResult(
    val producto: Producto,
    val created: Boolean
)
