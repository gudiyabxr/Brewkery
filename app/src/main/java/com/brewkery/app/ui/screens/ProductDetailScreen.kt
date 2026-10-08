package com.brewkery.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brewkery.app.data.model.SizeOption
import com.brewkery.app.ui.components.BrewkeryErrorState
import com.brewkery.app.ui.components.HomeLoadingShimmer
import com.brewkery.app.ui.theme.CoffeeBrown
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.MutedText
import com.brewkery.app.ui.util.formatPrice
import com.brewkery.app.ui.viewmodel.CartViewModel
import com.brewkery.app.ui.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onAdded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val currency by cartViewModel.currencySymbol.collectAsState()
    var favorite by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> HomeLoadingShimmer(Modifier.fillMaxSize())
            uiState.error != null -> BrewkeryErrorState(subtitle = uiState.error ?: "", onRetry = viewModel::load)
            uiState.item != null -> {
                val item = uiState.item!!
                Column(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Column {
                            Box {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(320.dp)
                                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    IconButton(
                                        onClick = onBack,
                                        modifier = Modifier.background(Espresso.copy(alpha = 0.5f), CircleShape),
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Cream)
                                    }
                                    IconButton(
                                        onClick = { favorite = !favorite },
                                        modifier = Modifier.background(Espresso.copy(alpha = 0.5f), CircleShape),
                                    ) {
                                        Icon(
                                            if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = Cream,
                                        )
                                    }
                                }
                            }
                            Column(Modifier.padding(20.dp)) {
                                Text(item.name, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "⭐ ${"%.1f".format(item.rating)} · ${item.reviewCount} reviews",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedText,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(item.description, style = MaterialTheme.typography.bodyLarge, color = MutedText)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    formatPrice(currency, item.basePrice),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = CoffeeBrown,
                                )

                                SectionLabel("Choose your size")
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    item.customizations.sizes.forEach { size ->
                                        SizeCard(
                                            size = size,
                                            currency = currency,
                                            selected = uiState.selectedSize?.id == size.id,
                                            basePrice = item.basePrice,
                                            onSelect = viewModel::selectSize,
                                        )
                                    }
                                }

                                SectionLabel("Choose your milk")
                                item.customizations.milkOptions.forEach { milk ->
                                    SelectRow(
                                        label = milk.name,
                                        extra = milk.extraPrice,
                                        currency = currency,
                                        selected = uiState.selectedMilk?.id == milk.id,
                                        onClick = { viewModel.selectMilk(milk) },
                                    )
                                }

                                SectionLabel("Sweetness")
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    item.customizations.sugarLevels.forEach { sugar ->
                                        val selected = uiState.selectedSugar == sugar
                                        Text(
                                            text = sugar,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (selected) CoffeeBrown else Latte)
                                                .clickable { viewModel.selectSugar(sugar) }
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            color = if (selected) Cream else CoffeeBrown,
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    }
                                }

                                SectionLabel("Quantity")
                                QuantityControl(
                                    quantity = uiState.quantity,
                                    onMinus = viewModel::decrementQty,
                                    onPlus = viewModel::incrementQty,
                                )
                                Spacer(Modifier.height(100.dp))
                            }
                        }
                    }
                    AddToCartBar(
                        total = viewModel.lineTotal(),
                        currency = currency,
                        onAdd = {
                            val size = uiState.selectedSize ?: return@AddToCartBar
                            val milk = uiState.selectedMilk ?: return@AddToCartBar
                            val sugar = uiState.selectedSugar ?: return@AddToCartBar
                            cartViewModel.addFromDetail(item, size, milk, sugar, uiState.quantity)
                            onAdded()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun SizeCard(
    size: SizeOption,
    currency: String,
    basePrice: Double,
    selected: Boolean,
    onSelect: (SizeOption) -> Unit,
) {
    val price = basePrice + size.extraPrice
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) CoffeeBrown else Latte,
                shape = RoundedCornerShape(16.dp),
            )
            .background(if (selected) Latte.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface)
            .clickable { onSelect(size) }
            .padding(12.dp),
    ) {
        Text(size.label.split(" ").first(), style = MaterialTheme.typography.labelLarge)
        Text(formatPrice(currency, price), style = MaterialTheme.typography.titleMedium, color = CoffeeBrown)
    }
}

@Composable
private fun SelectRow(
    label: String,
    extra: Double,
    currency: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, if (selected) CoffeeBrown else Latte, RoundedCornerShape(14.dp))
            .background(if (selected) Latte.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        if (extra > 0) Text("+${formatPrice(currency, extra)}", color = MutedText)
    }
}

@Composable
private fun QuantityControl(quantity: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CoffeeBrown.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("−", modifier = Modifier.clickable(onClick = onMinus).padding(12.dp), style = MaterialTheme.typography.titleLarge)
        Text("$quantity", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.titleLarge)
        Text("+", modifier = Modifier.clickable(onClick = onPlus).padding(12.dp), style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun AddToCartBar(total: Double, currency: String, onAdd: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "addScale")

    Surface(shadowElevation = 12.dp, color = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
                .scale(scale)
                .clip(RoundedCornerShape(18.dp))
                .background(Espresso)
                .clickable(interactionSource = interaction, indication = null, onClick = onAdd)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Add to Cart", color = Cream, style = MaterialTheme.typography.titleLarge)
            Text(formatPrice(currency, total), color = Cream, style = MaterialTheme.typography.titleMedium)
        }
    }
}
