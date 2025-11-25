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

class JuegoViewModel(
    private val repository: JuegoRepository
) : ViewModel() {

    val games: StateFlow<List<JuegoEntity>> = repository.games
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshData()
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
}