package com.brewkery.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.brewkery.app.data.cart.CartLineItem
import com.brewkery.app.data.cart.CartSummary
import com.brewkery.app.data.model.MenuItemDetail
import com.brewkery.app.data.model.MenuItemSummary
import com.brewkery.app.data.model.MilkOption
import com.brewkery.app.data.model.SizeOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.round

class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartLineItem>>(emptyList())
    val items: StateFlow<List<CartLineItem>> = _items.asStateFlow()

    private val _deliveryFee = MutableStateFlow(2.50)
    val deliveryFee: StateFlow<Double> = _deliveryFee.asStateFlow()

    private val _taxRatePercent = MutableStateFlow(8.0)
    val taxRatePercent: StateFlow<Double> = _taxRatePercent.asStateFlow()

    private val _estimatedTime = MutableStateFlow("20–30 minutes")
    val estimatedTime: StateFlow<String> = _estimatedTime.asStateFlow()

    private val _currencySymbol = MutableStateFlow("$")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    fun applyMeta(deliveryFee: Double, taxRatePercent: Double, estimatedTime: String, currencySymbol: String) {
        _deliveryFee.value = deliveryFee
        _taxRatePercent.value = taxRatePercent
        _estimatedTime.value = estimatedTime.replace("mins", "minutes")
        _currencySymbol.value = currencySymbol
    }

    fun addFromSummary(item: MenuItemSummary) {
        val line = CartLineItem(
            productId = item.id,
            name = item.name,
            imageUrl = item.imageUrl,
            sizeLabel = "Standard",
            milkLabel = "House Default",
            sugarLabel = "Regular",
            quantity = 1,
            unitPrice = item.basePrice,
        )
        _items.update { current ->
            val existing = current.find {
                it.productId == line.productId &&
                    it.sizeLabel == line.sizeLabel &&
                    it.milkLabel == line.milkLabel &&
                    it.sugarLabel == line.sugarLabel
            }
            if (existing != null) {
                current.map {
                    if (it.cartLineId == existing.cartLineId) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                current + line
            }
        }
    }

    fun addFromDetail(
        detail: MenuItemDetail,
        size: SizeOption,
        milk: MilkOption,
        sugar: String,
        quantity: Int,
    ) {
        val unitPrice = detail.basePrice + size.extraPrice + milk.extraPrice
        val line = CartLineItem(
            productId = detail.id,
            name = detail.name,
            imageUrl = detail.imageUrl,
            sizeLabel = size.label,
            milkLabel = milk.name,
            sugarLabel = sugar,
            quantity = quantity,
            unitPrice = unitPrice,
        )
        _items.update { current ->
            val existing = current.find {
                it.productId == line.productId &&
                    it.sizeLabel == line.sizeLabel &&
                    it.milkLabel == line.milkLabel &&
                    it.sugarLabel == line.sugarLabel
            }
            if (existing != null) {
                current.map {
                    if (it.cartLineId == existing.cartLineId) {
                        it.copy(quantity = it.quantity + quantity)
                    } else it
                }
            } else {
                current + line
            }
        }
    }

    fun increment(lineId: String) {
        _items.update { list ->
            list.map { if (it.cartLineId == lineId) it.copy(quantity = it.quantity + 1) else it }
        }
    }

    fun decrement(lineId: String) {
        _items.update { list ->
            list.mapNotNull { item ->
                when {
                    item.cartLineId != lineId -> item
                    item.quantity > 1 -> item.copy(quantity = item.quantity - 1)
                    else -> null
                }
            }
        }
    }

    fun remove(lineId: String) {
        _items.update { it.filterNot { line -> line.cartLineId == lineId } }
    }

    fun clear() {
        _items.value = emptyList()
    }

    fun summary(): CartSummary {
        val subtotal = _items.value.sumOf { it.lineTotal }
        val delivery = if (_items.value.isEmpty()) 0.0 else _deliveryFee.value
        val tax = round(subtotal * _taxRatePercent.value / 100.0 * 100) / 100.0
        val total = round((subtotal + delivery + tax) * 100) / 100.0
        val count = _items.value.sumOf { it.quantity }
        return CartSummary(count, subtotal, delivery, tax, total)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = CartViewModel() as T
        }
    }
}
