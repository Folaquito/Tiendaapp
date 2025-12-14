package com.example.tiendaapp.backend.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "purchase_items")
data class PurchaseItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    val purchase: Purchase? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    val producto: Producto? = null,
    val rawgGameId: Long? = null,
    val quantity: Int = 0,
    val unitPrice: Int = 0,
    val lineNet: Int = 0,
    val lineVat: Int = 0,
    val lineTotal: Int = 0,
    @OneToMany(mappedBy = "purchaseItem", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val keys: List<Key> = emptyList()
)
