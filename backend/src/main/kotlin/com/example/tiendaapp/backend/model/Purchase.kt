package com.example.tiendaapp.backend.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "purchases")
data class Purchase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderNumber: String = "",
    val buyerUserId: Long? = null,
    val buyerName: String = "",
    val buyerEmail: String = "",
    val createdAt: Instant = Instant.now(),
    val net: Int = 0,
    val vatAmount: Int = 0,
    val total: Int = 0,
    @OneToMany(mappedBy = "purchase", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val items: List<PurchaseItem> = emptyList()
)
