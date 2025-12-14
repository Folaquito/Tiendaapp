package com.example.tiendaapp.backend.repository

import com.example.tiendaapp.backend.model.Key
import com.example.tiendaapp.backend.model.KeyStatus
import com.example.tiendaapp.backend.model.Producto
import com.example.tiendaapp.backend.model.PurchaseItem
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KeyRepository : JpaRepository<Key, Long> {
    fun countByProductoAndStatus(producto: Producto, status: KeyStatus): Long
    fun findByProductoAndStatus(producto: Producto, status: KeyStatus, pageable: Pageable): List<Key>
    fun findByPurchaseItem(purchaseItem: PurchaseItem): List<Key>
}
