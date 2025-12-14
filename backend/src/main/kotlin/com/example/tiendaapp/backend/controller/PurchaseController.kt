package com.example.tiendaapp.backend.controller

import com.example.tiendaapp.backend.dto.PurchaseRequest
import com.example.tiendaapp.backend.dto.PurchaseResponse
import com.example.tiendaapp.backend.service.PurchaseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = ["http://localhost:8081", "http://10.0.2.2:8081", "*"])
class PurchaseController(
    private val purchaseService: PurchaseService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@RequestBody request: PurchaseRequest): PurchaseResponse = purchaseService.createPurchase(request)

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long): PurchaseResponse? = purchaseService.findPurchase(id)
}
