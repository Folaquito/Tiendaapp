package com.example.tiendaapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaapp.model.Juego
import com.example.tiendaapp.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JuegoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = JuegoRepository(application.applicationContext)
    private val _juegos = MutableStateFlow<List<Juego>>(emptyList())
    val juegos: StateFlow<List<Juego>> = _juegos.asStateFlow()
    init {
        cargarJuegos()
    }
    private fun cargarJuegos() {
        viewModelScope.launch {
            _juegos.value = repository.obtenerJuegos()
        }
    }
    fun buscarProductoPorId(id: Int): Juego? =
        _juegos.value.find { it.id == id }
}