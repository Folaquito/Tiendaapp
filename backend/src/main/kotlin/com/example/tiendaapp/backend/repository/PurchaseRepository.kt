package com.example.tiendaapp.backend.repository

import com.example.tiendaapp.backend.model.Purchase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseRepository : JpaRepository<Purchase, Long>
