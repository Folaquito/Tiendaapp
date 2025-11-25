package com.example.tiendaapp.backend.repository

import com.example.tiendaapp.backend.model.Producto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductoRepository : CrudRepository<Producto, Long>
