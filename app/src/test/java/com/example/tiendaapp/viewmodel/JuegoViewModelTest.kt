package com.example.tiendaapp.viewmodel

import com.example.tiendaapp.data.remote.FavoriteGameDto
import com.example.tiendaapp.model.JuegoEntity
import com.example.tiendaapp.repository.JuegoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class JuegoViewModelTest {

    private lateinit var repository: JuegoRepository
    private lateinit var viewModel: JuegoViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        // Mock games flow to avoid initialization errors
        io.mockk.every { repository.games } returns flowOf(emptyList())
        viewModel = JuegoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshData calls repository refreshGames`() = runTest {
        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.refreshGames() }
    }

    @Test
    fun `loadFavorites updates favorites state`() = runTest {
        val favorites = listOf(FavoriteGameDto(1, 101, "Game 1", "url"))
        coEvery { repository.getFavorites() } returns favorites

        viewModel.loadFavorites()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(favorites, viewModel.favorites.value)
    }

    @Test
    fun `loadFavorites handles empty list`() = runTest {
        coEvery { repository.getFavorites() } returns emptyList()

        viewModel.loadFavorites()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.favorites.value.isEmpty())
    }

    @Test
    fun `toggleFavorite adds favorite if not exists`() = runTest {
        val game = JuegoEntity(101, "Game 1", "url", 4.5, 1000, 15,"desc", null, null, null)
        coEvery { repository.getFavorites() } returns emptyList()

        viewModel.toggleFavorite(game)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.addFavorite(any()) }
    }

    @Test
    fun `toggleFavorite removes favorite if exists`() = runTest {
        val game = JuegoEntity(101, "Game 1", "url", 4.5, 1000, 15,"desc", null, null, null)
        val favorites = listOf(FavoriteGameDto(1, 101, "Game 1", "url"))
        coEvery { repository.getFavorites() } returns favorites

        // Initialize with favorites
        viewModel.loadFavorites()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite(game)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.removeFavorite(101) }
    }

    @Test
    fun `toggleFavorite reloads favorites after action`() = runTest {
        val game = JuegoEntity(101, "Game 1", "url", 4.5, 1000, 15,"desc", null, null, null)
        coEvery { repository.getFavorites() } returns emptyList()

        viewModel.toggleFavorite(game)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify loadFavorites is called twice (init + after toggle)
        coVerify(atLeast = 2) { repository.getFavorites() }
    }

    @Test
    fun `getGameFlow calls repository getGameById`() {
        io.mockk.every { repository.getGameById(101) } returns flowOf(null)
        viewModel.getGameFlow(101)
        io.mockk.verify { repository.getGameById(101) }
    }

    @Test
    fun `loadDescription calls repository fetchGameDescription`() = runTest {
        viewModel.loadDescription(101)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.fetchGameDescription(101) }
    }

    @Test
    fun `initial state of favorites is empty`() {
        assertTrue(viewModel.favorites.value.isEmpty())
    }

    @Test
    fun `refreshData handles exception gracefully`() = runTest {
        coEvery { repository.refreshGames() } throws Exception("Network error")

        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Should not crash
        coVerify { repository.refreshGames() }
    }

    @Test
    fun `updateNote calls repository updateFavoriteNote`() = runTest {
        val gameId = 101
        val note = "My Note"

        viewModel.updateNote(gameId, note)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.updateFavoriteNote(gameId, note) }
        // Also verifies that it reloads favorites
        coVerify { repository.getFavorites() }
    }
}
