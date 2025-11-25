package com.example.tiendaapp.viewmodel

import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.repository.JuegoRepository
import com.example.tiendaapp.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class JuegoViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: JuegoRepository = mockk(relaxed = true)
    private val gamesFlow = MutableStateFlow<List<JuegoEntity>>(emptyList())

    private val sampleGame = JuegoEntity(
        id = 99,
        name = "RAWG Game",
        imageUrl = "https://example.com/rawg.jpg",
        rating = 4.8,
        price = 45000,
        stock = 10,
        description = ""
    )

    private fun buildViewModel(): JuegoViewModel {
        every { repository.games } returns gamesFlow
        coEvery { repository.refreshGames() } returns Unit
        return JuegoViewModel(repository)
    }

    @Test
    fun loadExternalGamesEmiteResultados() = runTest {
        coEvery { repository.fetchExternalGames(any()) } returns listOf(sampleGame)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.externalGames.value.size)
        coVerify { repository.fetchExternalGames(any()) }
    }

    @Test
    fun loadExternalGamesConErrorEntregaMensaje() = runTest {
        coEvery { repository.fetchExternalGames(any()) } throws IllegalStateException("Sin API key")

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals("Sin API key", viewModel.externalError.value)
    }
}
