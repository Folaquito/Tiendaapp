package com.example.tiendaapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendaapp.model.CartItem
import com.example.tiendaapp.model.Juego
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun agregarAlCarrito(juego: Juego) {
        val actual = _items.value.toMutableList()
        val indice = actual.indexOfFirst { it.juego.id == juego.id }
        if (indice >= 0) {
            val existente = actual[indice]
            actual[indice] = existente.copy(cantidad = existente.cantidad + 1)
        } else {
            actual.add(CartItem(juego))
        }
        _items.value = actual
    }

    fun disminuirCantidad(juegoId: Int) {
        val actual = _items.value.toMutableList()
        val indice = actual.indexOfFirst { it.juego.id == juegoId }
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

    fun eliminarDelCarrito(juegoId: Int) {
        val actual = _items.value.filterNot { it.juego.id == juegoId }
        _items.value = actual
    }

    fun vaciarCarrito() {
        _items.value = emptyList()
    }

    fun calcularTotal(): Int {
        return _items.value.sumOf { it.juego.precio * it.cantidad }
    }
}
