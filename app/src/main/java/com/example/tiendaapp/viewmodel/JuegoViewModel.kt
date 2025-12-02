package com.example.tiendaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.repository.JuegoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

class JuegoViewModel(
    private val repository: JuegoRepository
) : ViewModel() {

    val games: StateFlow<List<JuegoEntity>> = repository.games
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _favorites = MutableStateFlow<List<com.example.tiendaapp.data.remote.FavoriteGameDto>>(emptyList())
    val favorites: StateFlow<List<com.example.tiendaapp.data.remote.FavoriteGameDto>> = _favorites

    private val _backOfficeMessage = MutableStateFlow<String?>(null)
    val backOfficeMessage: StateFlow<String?> = _backOfficeMessage

    private val _isOperating = MutableStateFlow(false)
    val isOperating: StateFlow<Boolean> = _isOperating

    init {
        refreshData()
        loadFavorites()
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshGames()
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = repository.getFavorites()
        }
    }

    fun toggleFavorite(game: JuegoEntity) {
        viewModelScope.launch {
            val currentFavs = _favorites.value
            val existing = currentFavs.find { it.gameId == game.id }
            if (existing != null) {
                repository.removeFavorite(game.id)
            } else {
                val newFav = com.example.tiendaapp.data.remote.FavoriteGameDto(
                    gameId = game.id,
                    title = game.name,
                    imageUrl = game.imageUrl
                )
                repository.addFavorite(newFav)
            }
            loadFavorites()
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

    fun updateNote(gameId: Int, note: String) {
        viewModelScope.launch {
            repository.updateFavoriteNote(gameId, note)
            loadFavorites()
        }
    }

    fun crearProducto(nombre: String, descripcion: String, imagen: String, precio: Int, valoracion: Double, stock: Int) {
        viewModelScope.launch {
            _isOperating.value = true
            _backOfficeMessage.value = null
            try {
                val producto = com.example.tiendaapp.data.remote.ProductoDto(
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = imagen,
                    precio = precio,
                    valoracion = valoracion,
                    stock = stock
                )
                repository.crearProducto(producto)
                _backOfficeMessage.value = "Producto creado exitosamente"
            } catch (e: Exception) {
                _backOfficeMessage.value = "Error al crear producto: ${e.message}"
            } finally {
                _isOperating.value = false
            }
        }
    }
}
