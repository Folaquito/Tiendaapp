package com.example.tiendaapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tiendaapp.model.JuegoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JuegoDao {

    @Query("SELECT * FROM games_table ORDER BY rating DESC")
    fun getAllGames(): Flow<List<JuegoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<JuegoEntity>)

    @Query("DELETE FROM games_table")
    suspend fun deleteAllGames()

    @Query("UPDATE games_table SET description = :description WHERE id = :id")
    suspend fun updateGameDescription(id: Int, description: String)

    @Query("SELECT * FROM games_table WHERE id = :id")
    fun getGameById(id: Int): Flow<JuegoEntity?>
}