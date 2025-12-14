package com.example.tiendaapp.backend.repository

import com.example.tiendaapp.backend.model.Favorite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun findByGameId(gameId: Int): Favorite?
    fun deleteByGameId(gameId: Int)
}
