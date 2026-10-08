package com.brewkery.app.data.cart

import java.util.UUID

data class CartLineItem(
    val cartLineId: String = UUID.randomUUID().toString(),
    val productId: Int,
    val name: String,
    val imageUrl: String,
    val sizeLabel: String,
    val milkLabel: String,
    val sugarLabel: String,
    val quantity: Int,
    val unitPrice: Double,
) {
    val lineTotal: Double get() = unitPrice * quantity
}

data class CartSummary(
    val itemCount: Int,
    val subtotal: Double,
    val deliveryFee: Double,
    val tax: Double,
    val total: Double,
)
