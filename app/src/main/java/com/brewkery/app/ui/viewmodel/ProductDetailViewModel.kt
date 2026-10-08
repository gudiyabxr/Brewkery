package com.brewkery.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brewkery.app.data.model.MenuItemDetail
import com.brewkery.app.data.model.MilkOption
import com.brewkery.app.data.model.SizeOption
import com.brewkery.app.data.repository.BrewkeryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val item: MenuItemDetail? = null,
    val selectedSize: SizeOption? = null,
    val selectedMilk: MilkOption? = null,
    val selectedSugar: String? = null,
    val quantity: Int = 1,
)

class ProductDetailViewModel(
    private val repository: BrewkeryRepository,
    private val itemId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.loadItem(itemId) }
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            item = detail,
                            selectedSize = detail.customizations.sizes.firstOrNull(),
                            selectedMilk = detail.customizations.milkOptions.firstOrNull(),
                            selectedSugar = detail.customizations.sugarLevels.firstOrNull(),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Unable to load item")
                    }
                }
        }
    }

    fun selectSize(size: SizeOption) {
        _uiState.update { it.copy(selectedSize = size) }
    }

    fun selectMilk(milk: MilkOption) {
        _uiState.update { it.copy(selectedMilk = milk) }
    }

    fun selectSugar(sugar: String) {
        _uiState.update { it.copy(selectedSugar = sugar) }
    }

    fun incrementQty() {
        _uiState.update { it.copy(quantity = (it.quantity + 1).coerceAtMost(99)) }
    }

    fun decrementQty() {
        _uiState.update { it.copy(quantity = (it.quantity - 1).coerceAtLeast(1)) }
    }

    fun unitPrice(): Double {
        val state = _uiState.value
        val item = state.item ?: return 0.0
        val sizeExtra = state.selectedSize?.extraPrice ?: 0.0
        val milkExtra = state.selectedMilk?.extraPrice ?: 0.0
        return item.basePrice + sizeExtra + milkExtra
    }

    fun lineTotal(): Double = unitPrice() * _uiState.value.quantity

    companion object {
        fun factory(repository: BrewkeryRepository, itemId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProductDetailViewModel(repository, itemId) as T
            }
    }
}
