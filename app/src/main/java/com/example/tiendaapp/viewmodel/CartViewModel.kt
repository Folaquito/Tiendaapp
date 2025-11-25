package com.example.tiendaapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendaapp.model.CartItem
import com.example.tiendaapp.model.JuegoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    // Estado del carrito
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

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

    fun calcularTotal(): Int {
        return _items.value.sumOf { it.game.price * it.cantidad }
    }
}