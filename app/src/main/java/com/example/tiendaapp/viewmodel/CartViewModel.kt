package com.example.tiendaapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendaapp.data.remote.MicroserviceClient
import com.example.tiendaapp.data.remote.AuthResponse
import com.example.tiendaapp.data.remote.PurchaseItemRequest
import com.example.tiendaapp.data.remote.PurchaseResponse
import com.example.tiendaapp.data.remote.PurchaseRequest
import com.example.tiendaapp.model.CartItem
import com.example.tiendaapp.model.JuegoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    // Estado del carrito
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _lastPurchase = MutableStateFlow<PurchaseSnapshot?>(null)
    val lastPurchase: StateFlow<PurchaseSnapshot?> = _lastPurchase.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun agregarAlCarrito(game: JuegoEntity) {
        val actual = _items.value.toMutableList()
        val indice = actual.indexOfFirst { it.game.id == game.id }

        if (indice >= 0) {
            val existente = actual[indice]
            actual[indice] = existente.copy(cantidad = existente.cantidad + 1)
        } else {
            actual.add(CartItem(game))
        }
        _items.value = actual
    }

    fun disminuirCantidad(gameId: Int) {
        val actual = _items.value.toMutableList()
        val indice = actual.indexOfFirst { it.game.id == gameId }

        if (indice >= 0) {
            val existente = actual[indice]
            if (existente.cantidad > 1) {
                actual[indice] = existente.copy(cantidad = existente.cantidad - 1)
            } else {
                actual.removeAt(indice)
            }
            _items.value = actual
        }
    }

    fun eliminarDelCarrito(gameId: Int) {
        val actual = _items.value.filterNot { it.game.id == gameId }
        _items.value = actual
    }

    fun vaciarCarrito() {
        _items.value = emptyList()
    }

    fun registrarCompraSnapshot() {
        val currentItems = _items.value
        if (currentItems.isEmpty()) {
            _lastPurchase.value = null
            return
        }
        val total = calcularTotal(currentItems)
        _lastPurchase.value = PurchaseSnapshot(
            orderId = null,
            items = currentItems,
            total = total
        )
    }

    suspend fun realizarCompra(currentUser: AuthResponse?, buyerName: String?, buyerEmail: String?): Result<PurchaseResponse> {
        val currentItems = _items.value
        if (currentItems.isEmpty()) {
            return Result.failure(IllegalStateException("El carrito está vacío"))
        }

        _isProcessing.value = true
        _errorMessage.value = null
        return try {
            val request = PurchaseRequest(
                userId = currentUser?.id,
                buyerName = currentUser?.name ?: buyerName,
                buyerEmail = currentUser?.email ?: buyerEmail,
                items = currentItems.map { item ->
                    PurchaseItemRequest(
                        productoId = item.game.id.toLong(),
                        rawgGameId = item.game.rawgGameId?.toLong(),
                        quantity = item.cantidad
                    )
                }
            )
            val response = MicroserviceClient.api.crearCompra(request)
            registrarCompraDesdeRespuesta(response, currentItems)
            vaciarCarrito()
            Result.success(response)
        } catch (e: Exception) {
            _errorMessage.value = e.message
            Result.failure(e)
        } finally {
            _isProcessing.value = false
        }
    }

    fun registrarCompraDesdeRespuesta(response: PurchaseResponse, items: List<CartItem>) {
        // Preferir rawgGameId (id del juego en la app) para mapear correctamente las keys en UI
        val keysByGameId = response.items.mapNotNull { item ->
            val keyId = (item.rawgGameId ?: item.productId).toInt()
            keyId to item.keys
        }.toMap()
        _lastPurchase.value = PurchaseSnapshot(
            orderId = response.orderNumber,
            items = items,
            net = response.summary.net,
            vat = response.summary.vatAmount,
            total = response.summary.total,
            keysByGameId = keysByGameId
        )
    }

    fun calcularTotal(): Int = calcularTotal(_items.value)

    private fun calcularTotal(items: List<CartItem>): Int =
        items.sumOf { it.game.price * it.cantidad }
}

data class PurchaseSnapshot(
    val orderId: String? = null,
    val items: List<CartItem> = emptyList(),
    val net: Int? = null,
    val vat: Int? = null,
    val total: Int? = null,
    val keysByGameId: Map<Int, List<String>>? = null
)