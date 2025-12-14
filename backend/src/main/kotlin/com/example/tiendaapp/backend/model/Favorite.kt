package com.example.tiendaapp.backend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = [UniqueConstraint(columnNames = ["game_id"])]
)
data class Favorite(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "game_id", nullable = false)
    var gameId: Int = 0,
    @Column(nullable = false)
    var title: String = "",
    @Column(name = "image_url", nullable = false)
    var imageUrl: String = "",
    var note: String? = null
)
