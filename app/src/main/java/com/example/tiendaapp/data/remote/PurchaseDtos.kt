package com.example.tiendaapp.data.remote

data class PurchaseRequest(
    val userId: Long? = null,
    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val items: List<PurchaseItemRequest>
)

data class PurchaseItemRequest(
    val productoId: Long,
    val rawgGameId: Long? = null,
    val quantity: Int
)

data class PurchaseResponse(
    val purchaseId: Long,
    val orderNumber: String,
    val buyer: BuyerResponse,
    val items: List<PurchaseItemResponse>,
    val summary: PurchaseSummaryResponse,
    val createdAt: String
)

data class BuyerResponse(
    val id: Long?,
    val name: String,
    val email: String
)

data class PurchaseItemResponse(
    val productId: Long,
    val rawgGameId: Long?,
    val name: String,
    val unitPrice: Int,
    val quantity: Int,
    val keys: List<String>,
    val lineNet: Int,
    val lineVat: Int,
    val lineTotal: Int
)

data class PurchaseSummaryResponse(
    val net: Int,
    val vatRate: Double,
    val vatAmount: Int,
    val total: Int
)
