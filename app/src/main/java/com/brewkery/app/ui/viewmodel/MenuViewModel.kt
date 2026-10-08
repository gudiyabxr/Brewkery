package com.brewkery.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brewkery.app.data.model.MenuItemSummary
import com.brewkery.app.data.model.MenuMeta
import com.brewkery.app.data.repository.BrewkeryRepository
import com.brewkery.app.ui.util.isDessertItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ExploreCategory(val apiIds: Set<String>?, val label: String, val emoji: String) {
    ALL(null, "All", "☕"),
    COFFEE(setOf("cat_hot_coffee"), "Coffee", "☕"),
    COLD_BREW(setOf("cat_cold_brews"), "Cold Brew", "🧊"),
    BAKERY(setOf("cat_bakery"), "Bakery", "🥐"),
    DESSERTS(setOf("cat_bakery"), "Desserts", "🍰"),
}

data class MenuUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val meta: MenuMeta? = null,
    val items: List<MenuItemSummary> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ExploreCategory = ExploreCategory.COFFEE,
    val addConfirmation: String? = null,
)

class MenuViewModel(
    private val repository: BrewkeryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.loadMenu() }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            meta = response.meta,
                            items = response.items,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Unable to load menu")
                    }
                }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: ExploreCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun menuItems(): List<MenuItemSummary> {
        val query = _uiState.value.searchQuery
        return _uiState.value.items.filter { item ->
            query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.tagline.contains(query, ignoreCase = true)
        }
    }

    fun filteredItems(): List<MenuItemSummary> {
        val state = _uiState.value
        return state.items.filter { item ->
            val matchesSearch = state.searchQuery.isBlank() ||
                item.name.contains(state.searchQuery, ignoreCase = true) ||
                item.tagline.contains(state.searchQuery, ignoreCase = true)
            val matchesCategory = when (state.selectedCategory) {
                ExploreCategory.ALL -> true
                ExploreCategory.DESSERTS ->
                    item.categoryId == "cat_bakery" && isDessertItem(item.name, item.badge)
                ExploreCategory.BAKERY ->
                    item.categoryId == "cat_bakery" && !isDessertItem(item.name, item.badge)
                else -> state.selectedCategory.apiIds?.contains(item.categoryId) == true
            }
            matchesSearch && matchesCategory
        }
    }

    fun popularItems(): List<MenuItemSummary> =
        _uiState.value.items.sortedByDescending { it.rating }.take(6)

    fun showAddConfirmation(name: String) {
        _uiState.update { it.copy(addConfirmation = name) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            _uiState.update { if (it.addConfirmation == name) it.copy(addConfirmation = null) else it }
        }
    }

    fun clearAddConfirmation() {
        _uiState.update { it.copy(addConfirmation = null) }
    }

    companion object {
        fun factory(repository: BrewkeryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    MenuViewModel(repository) as T
            }
    }
}
