package com.example.tiendaapp.viewmodel

import com.example.tiendaapp.model.JuegoEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartViewModelTest {

    private lateinit var viewModel: CartViewModel
    private val sampleGame = JuegoEntity(
        id = 1,
        name = "Juego Test",
        imageUrl = "https://example.com/image.jpg",
        rating = 4.5,
        price = 10000,
        stock = 5,
        description = "Demo"
    )

    @Before
    fun setUp() {
        viewModel = CartViewModel()
    }

    @Test
    fun agregarAlCarritoIncrementaCantidadYTotal() {
        viewModel.agregarAlCarrito(sampleGame)
        viewModel.agregarAlCarrito(sampleGame)

        val items = viewModel.items.value
        assertEquals(1, items.size)
        assertEquals(2, items.first().cantidad)
        assertEquals(20000, viewModel.calcularTotal())
    }

    @Test
    fun disminuirCantidadEliminaCuandoLlegaACero() {
        viewModel.agregarAlCarrito(sampleGame)
        viewModel.disminuirCantidad(sampleGame.id)

        assertTrue(viewModel.items.value.isEmpty())
    }
}
