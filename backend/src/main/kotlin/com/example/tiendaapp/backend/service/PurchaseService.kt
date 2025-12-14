package com.example.tiendaapp.backend.service

import com.example.tiendaapp.backend.dto.BuyerResponse
import com.example.tiendaapp.backend.dto.PurchaseItemRequest
import com.example.tiendaapp.backend.dto.PurchaseItemResponse
import com.example.tiendaapp.backend.dto.PurchaseRequest
import com.example.tiendaapp.backend.dto.PurchaseResponse
import com.example.tiendaapp.backend.dto.PurchaseSummaryResponse
import com.example.tiendaapp.backend.model.Key
import com.example.tiendaapp.backend.model.KeyStatus
import com.example.tiendaapp.backend.model.Producto
import com.example.tiendaapp.backend.model.Purchase
import com.example.tiendaapp.backend.model.PurchaseItem
import com.example.tiendaapp.backend.repository.KeyRepository
import com.example.tiendaapp.backend.repository.ProductoRepository
import com.example.tiendaapp.backend.repository.PurchaseItemRepository
import com.example.tiendaapp.backend.repository.PurchaseRepository
import com.example.tiendaapp.backend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.roundToInt

@Service
class PurchaseService(
    private val productoRepository: ProductoRepository,
    private val purchaseRepository: PurchaseRepository,
    private val purchaseItemRepository: PurchaseItemRepository,
    private val keyRepository: KeyRepository,
    private val userRepository: UserRepository,
    @Value("\${app.iva-percent:0.19}")
    private val vatRate: Double
) {

    @Transactional
    fun createPurchase(request: PurchaseRequest): PurchaseResponse {
        val buyerUser = request.userId?.let { id ->
            userRepository.findById(id).orElse(null)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado")
        }

        val buyerName = buyerUser?.name ?: request.buyerName?.takeIf { it.isNotBlank() }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "buyerName es requerido")
        val buyerEmail = buyerUser?.email ?: request.buyerEmail?.takeIf { it.isNotBlank() }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "buyerEmail es requerido")

        val validatedItems = request.items.map { validateItem(it) }

        var netTotal = 0
        var vatTotal = 0

        validatedItems.forEach { validated ->
            val lineNet = validated.unitPrice * validated.quantity
            val lineVat = (lineNet * vatRate).roundToInt()
            netTotal += lineNet
            vatTotal += lineVat
        }

        val savedPurchase = purchaseRepository.save(
            Purchase(
                orderNumber = generateOrderNumber(),
                buyerUserId = buyerUser?.id,
                buyerName = buyerName,
                buyerEmail = buyerEmail,
                createdAt = Instant.now(),
                net = netTotal,
                vatAmount = vatTotal,
                total = netTotal + vatTotal
            )
        )

        val responseItems = validatedItems.map { validated ->
            val lineNet = validated.unitPrice * validated.quantity
            val lineVat = (lineNet * vatRate).roundToInt()
            val lineTotal = lineNet + lineVat

            val savedItem = purchaseItemRepository.save(
                PurchaseItem(
                    purchase = savedPurchase,
                    producto = validated.producto,
                    rawgGameId = validated.rawgGameId,
                    quantity = validated.quantity,
                    unitPrice = validated.unitPrice,
                    lineNet = lineNet,
                    lineVat = lineVat,
                    lineTotal = lineTotal
                )
            )

            val keys = assignKeysForItem(savedItem)

            PurchaseItemResponse(
                productId = validated.producto.id,
                rawgGameId = validated.rawgGameId,
                name = validated.producto.nombre,
                unitPrice = validated.unitPrice,
                quantity = validated.quantity,
                keys = keys.map { it.code },
                lineNet = lineNet,
                lineVat = lineVat,
                lineTotal = lineTotal
            )
        }

        val summary = PurchaseSummaryResponse(
            net = netTotal,
            vatRate = vatRate,
            vatAmount = vatTotal,
            total = netTotal + vatTotal
        )

        return PurchaseResponse(
            purchaseId = savedPurchase.id,
            orderNumber = savedPurchase.orderNumber,
            buyer = BuyerResponse(buyerUser?.id, buyerName, buyerEmail),
            items = responseItems,
            summary = summary,
            createdAt = DateTimeFormatter.ISO_INSTANT.format(savedPurchase.createdAt)
        )
    }

    fun findPurchase(id: Long): PurchaseResponse? {
        val purchase = purchaseRepository.findById(id).orElse(null) ?: return null
        val items = purchaseItemRepository.findAll().filter { it.purchase?.id == id }
        val responseItems = items.map { item ->
            val keys = keyRepository.findByPurchaseItem(item)
            PurchaseItemResponse(
                productId = item.producto?.id ?: 0,
                rawgGameId = item.rawgGameId,
                name = item.producto?.nombre ?: "",
                unitPrice = item.unitPrice,
                quantity = item.quantity,
                keys = keys.map { it.code },
                lineNet = item.lineNet,
                lineVat = item.lineVat,
                lineTotal = item.lineTotal
            )
        }
        val summary = PurchaseSummaryResponse(
            net = purchase.net,
            vatRate = vatRate,
            vatAmount = purchase.vatAmount,
            total = purchase.total
        )
        return PurchaseResponse(
            purchaseId = purchase.id,
            orderNumber = purchase.orderNumber,
            buyer = BuyerResponse(purchase.buyerUserId, purchase.buyerName, purchase.buyerEmail),
            items = responseItems,
            summary = summary,
            createdAt = DateTimeFormatter.ISO_INSTANT.format(purchase.createdAt)
        )
    }

    private data class ValidatedItem(
        val producto: Producto,
        val rawgGameId: Long,
        val quantity: Int,
        val unitPrice: Int
    )

    private fun validateItem(item: PurchaseItemRequest): ValidatedItem {
        if (item.quantity <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero")
        }
        val producto = when {
            item.productoId != null -> productoRepository.findById(item.productoId).orElse(null)
            item.rawgGameId != null -> productoRepository.findByRawgGameId(item.rawgGameId)
            else -> null
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado para la compra")

        val rawgId = item.rawgGameId ?: producto.rawgGameId
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "rawgGameId es requerido")
        val available = keyRepository.countByProductoAndStatus(producto, KeyStatus.AVAILABLE)
        if (available < item.quantity) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para el producto ${producto.nombre}")
        }

        return ValidatedItem(
            producto = producto,
            rawgGameId = rawgId,
            quantity = item.quantity,
            unitPrice = producto.precio
        )
    }

    private fun assignKeysForItem(purchaseItem: PurchaseItem): List<Key> {
        val producto = purchaseItem.producto ?: throw IllegalStateException("Producto requerido")
        val keysToAssign = keyRepository.findByProductoAndStatus(
            producto,
            KeyStatus.AVAILABLE,
            PageRequest.of(0, purchaseItem.quantity)
        )
        if (keysToAssign.size < purchaseItem.quantity) {
            throw IllegalStateException("No hay suficientes keys disponibles")
        }
        val updatedKeys = keysToAssign.map {
            it.copy(status = KeyStatus.ASSIGNED, purchaseItem = purchaseItem)
        }
        val savedKeys = keyRepository.saveAll(updatedKeys)
        val remaining = keyRepository.countByProductoAndStatus(producto, KeyStatus.AVAILABLE).toInt()
        val updatedProducto = producto.copy(stock = remaining)
        productoRepository.save(updatedProducto)
        return savedKeys
    }

    private fun generateOrderNumber(): String {
        val token = UUID.randomUUID().toString().take(8).uppercase()
        return "ORD-$token"
    }
}
