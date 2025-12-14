package com.example.tiendaapp.backend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "product_keys")
data class Key(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val code: String = "",
    @Enumerated(EnumType.STRING)
    val status: KeyStatus = KeyStatus.AVAILABLE,
    @ManyToOne
    @JoinColumn(name = "producto_id")
    val producto: Producto? = null,
    @ManyToOne
    @JoinColumn(name = "purchase_item_id")
    val purchaseItem: PurchaseItem? = null
)
