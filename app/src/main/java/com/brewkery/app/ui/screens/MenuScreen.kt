package com.brewkery.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brewkery.app.ui.components.BrewkeryErrorState
import com.brewkery.app.ui.components.HomeLoadingShimmer
import com.brewkery.app.ui.components.MenuGridCard
import com.brewkery.app.ui.theme.MutedText
import com.brewkery.app.ui.viewmodel.CartViewModel
import com.brewkery.app.ui.viewmodel.MenuViewModel

@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by menuViewModel.uiState.collectAsState()
    val currency by cartViewModel.currencySymbol.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Full Menu", style = MaterialTheme.typography.titleLarge)
            Text("Crafted Coffee. Freshly Baked.", style = MaterialTheme.typography.bodyMedium, color = MutedText)
        }
        when {
            uiState.isLoading -> HomeLoadingShimmer()
            uiState.error != null -> BrewkeryErrorState(onRetry = menuViewModel::loadMenu)
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(menuViewModel.menuItems(), key = { it.id }) { item ->
                        MenuGridCard(
                            item = item,
                            currencySymbol = currency,
                            onClick = { onProductClick(item.id) },
                        )
                    }
                }
            }
        }
    }
}
