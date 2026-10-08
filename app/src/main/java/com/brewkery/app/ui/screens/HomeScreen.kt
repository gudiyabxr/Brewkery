package com.brewkery.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brewkery.app.ui.components.BrewkeryErrorState
import com.brewkery.app.ui.components.HomeLoadingShimmer
import com.brewkery.app.ui.components.PopularProductCard
import com.brewkery.app.ui.components.PromoOfferCard
import com.brewkery.app.ui.theme.CoffeeBrown
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.DarkCoffee
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.MutedText
import com.brewkery.app.ui.viewmodel.CartViewModel
import com.brewkery.app.ui.viewmodel.ExploreCategory
import com.brewkery.app.ui.viewmodel.MenuViewModel

@Composable
fun HomeScreen(
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    onProductClick: (Int) -> Unit,
    onExploreMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by menuViewModel.uiState.collectAsState()
    val currency by cartViewModel.currencySymbol.collectAsState()

    LaunchedEffect(uiState.meta) {
        uiState.meta?.let { meta ->
            cartViewModel.applyMeta(
                meta.deliveryFee,
                meta.taxRatePercent,
                meta.estimatedDeliveryTime,
                meta.currencySymbol,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            uiState.isLoading -> HomeLoadingShimmer(Modifier.fillMaxSize())
            uiState.error != null -> BrewkeryErrorState(onRetry = menuViewModel::loadMenu, modifier = Modifier.fillMaxSize())
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 100.dp),
                ) {
                    HomeHeader()
                    HeroSection(onExplore = onExploreMenu)
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = menuViewModel::onSearchChange,
                    )
                    CategorySection(
                        selected = uiState.selectedCategory,
                        onSelect = menuViewModel::onCategorySelected,
                    )
                    SectionTitle("Customer Favorites", "Most loved brews & bites")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(menuViewModel.popularItems(), key = { it.id }) { item ->
                            PopularProductCard(
                                item = item,
                                currencySymbol = currency,
                                onClick = { onProductClick(item.id) },
                                onAdd = {
                                    cartViewModel.addFromSummary(item)
                                    menuViewModel.showAddConfirmation(item.name)
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    PromoOfferCard(
                        onOrder = onExploreMenu,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    if (uiState.searchQuery.isNotBlank()) {
                        SectionTitle("Search Results", null)
                        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            menuViewModel.filteredItems().forEach { item ->
                                SearchResultRow(item.name, item.tagline) { onProductClick(item.id) }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.addConfirmation != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Espresso,
                shadowElevation = 8.dp,
            ) {
                Text(
                    text = "Added ${uiState.addConfirmation}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Cream,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("☕ Brewkery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Good coffee starts here.", style = MaterialTheme.typography.bodyMedium, color = MutedText)
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Latte),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = CoffeeBrown)
        }
    }
}

@Composable
private fun HeroSection(onExplore: () -> Unit) {
    CardHero(onExplore)
}

@Composable
private fun CardHero(onExplore: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onExplore),
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=800&auto=format&fit=crop&q=80",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, DarkCoffee.copy(alpha = 0.85f)),
                    ),
                )
                .padding(20.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Column {
                Text(
                    "Your perfect cup\nis waiting.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Cream,
                )
                Text(
                    "Freshly roasted beans. Handcrafted with care.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cream.copy(alpha = 0.85f),
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Explore Menu →",
                    style = MaterialTheme.typography.labelLarge,
                    color = Cream,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CoffeeBrown.copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = CoffeeBrown)
        Spacer(Modifier.size(12.dp))
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Search your favorite coffee...", color = MutedText, style = MaterialTheme.typography.bodyMedium)
                }
                inner()
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        )
    }
}

@Composable
private fun CategorySection(selected: ExploreCategory, onSelect: (ExploreCategory) -> Unit) {
    SectionTitle("Explore Our Menu", "Made fresh, brewed with passion.")
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ExploreCategory.entries.filter { it != ExploreCategory.ALL }.forEach { category ->
            val isSelected = category == selected
            Text(
                text = "${category.emoji} ${category.label}",
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) DarkCoffee else Latte)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (isSelected) Cream else CoffeeBrown,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String?) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        subtitle?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MutedText)
        }
    }
}

@Composable
private fun SearchResultRow(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MutedText, maxLines = 1)
    }
}
