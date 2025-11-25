package com.example.tiendaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.repository.JuegoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JuegoViewModel(
    private val repository: JuegoRepository
) : ViewModel() {

    val games: StateFlow<List<JuegoEntity>> = repository.games
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _backOfficeMessage = MutableStateFlow<String?>(null)
    val backOfficeMessage: StateFlow<String?> = _backOfficeMessage.asStateFlow()

    private val _isOperating = MutableStateFlow(false)
    val isOperating: StateFlow<Boolean> = _isOperating.asStateFlow()

    private val _externalGames = MutableStateFlow<List<JuegoEntity>>(emptyList())
    val externalGames: StateFlow<List<JuegoEntity>> = _externalGames.asStateFlow()

    private val _externalError = MutableStateFlow<String?>(null)
    val externalError: StateFlow<String?> = _externalError.asStateFlow()

    init {
        refreshData()
        loadExternalGames()
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshGames()
        }
    }
    fun getGameFlow(id: Int): Flow<JuegoEntity?> {
        return repository.getGameById(id)
    }

    fun loadDescription(id: Int) {
        viewModelScope.launch {
            repository.fetchGameDescription(id)
        }
    }

    fun loadExternalGames(limit: Int = 6) {
        viewModelScope.launch {
            try {
                _externalError.value = null
                _externalGames.value = repository.fetchExternalGames(limit)
            } catch (e: Exception) {
                _externalError.value = e.message ?: "No se pudieron cargar las recomendaciones"
            }
        }
    }

    fun crearProducto(
        nombre: String,
        descripcion: String,
        imagen: String,
        precio: Int,
        valoracion: Double,
        stock: Int
    ) {
        viewModelScope.launch {
            _isOperating.value = true
            _backOfficeMessage.value = null
            try {
                repository.crearProducto(nombre, descripcion, imagen, precio, valoracion, stock)
                _backOfficeMessage.value = "Producto guardado correctamente"
            } catch (e: Exception) {
                _backOfficeMessage.value = "No se pudo guardar: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            _isOperating.value = true
            try {
                repository.eliminarProducto(id)
                _backOfficeMessage.value = "Producto eliminado"
            } catch (e: Exception) {
                _backOfficeMessage.value = "No se pudo eliminar: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
}
