package com.brewkery.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brewkery.app.data.cart.CartLineItem
import com.brewkery.app.ui.theme.CoffeeBrown
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.MutedText
import com.brewkery.app.ui.util.formatPrice
import com.brewkery.app.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onExplore: () -> Unit,
    onPlaceOrder: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items by cartViewModel.items.collectAsState()
    val currency by cartViewModel.currencySymbol.collectAsState()
    val estimated by cartViewModel.estimatedTime.collectAsState()
    val summary = cartViewModel.summary()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Your Cart", style = MaterialTheme.typography.titleLarge)
            Text("Almost ready for your perfect coffee.", style = MaterialTheme.typography.bodyMedium, color = MutedText)
        }

        if (items.isEmpty()) {
            EmptyCart(onExplore = onExplore, modifier = Modifier.weight(1f))
        } else {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 96.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items, key = { it.cartLineId }) { line ->
                        CartLineCard(
                            line = line,
                            currency = currency,
                            onIncrement = { cartViewModel.increment(line.cartLineId) },
                            onDecrement = { cartViewModel.decrement(line.cartLineId) },
                            onRemove = { cartViewModel.remove(line.cartLineId) },
                        )
                    }
                    item {
                        SummaryCard(summary, currency)
                        DeliveryCard(estimated)
                    }
                }
                PlaceOrderBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    total = summary.total,
                    currency = currency,
                    onPlaceOrder = {
                        val ticket = "BK-${(10000..99999).random()}"
                        onPlaceOrder(ticket)
                    },
                )
            }
        }
    }
}

@Composable
private fun EmptyCart(onExplore: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("☕", style = MaterialTheme.typography.displaySmall)
        Text("Your cart is empty", style = MaterialTheme.typography.titleLarge)
        Text(
            "Looks like you haven't found your perfect brew yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText,
        )
        Text(
            "Explore Coffee",
            modifier = Modifier
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CoffeeBrown)
                .clickable(onClick = onExplore)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            color = Cream,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun CartLineCard(
    line: CartLineItem,
    currency: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = line.imageUrl,
                contentDescription = line.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(line.name, style = MaterialTheme.typography.titleMedium)
                Text("Size: ${line.sizeLabel}", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                Text("Milk: ${line.milkLabel}", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                Text("Sweetness: ${line.sugarLabel}", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                Text(formatPrice(currency, line.lineTotal), style = MaterialTheme.typography.titleLarge, color = CoffeeBrown)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("−", Modifier.clickable(onClick = onDecrement).padding(8.dp))
                    Text("${line.quantity}")
                    Text("+", Modifier.clickable(onClick = onIncrement).padding(8.dp))
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Outlined.Delete, contentDescription = "Remove")
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: com.brewkery.app.data.cart.CartSummary, currency: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryRow("Subtotal", formatPrice(currency, summary.subtotal))
            SummaryRow("Delivery", formatPrice(currency, summary.deliveryFee))
            SummaryRow("Tax", formatPrice(currency, summary.tax))
            HorizontalDivider(color = Latte)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.headlineMedium)
                Text(formatPrice(currency, summary.total), style = MaterialTheme.typography.titleLarge, color = CoffeeBrown)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MutedText)
        Text(value)
    }
}

@Composable
private fun DeliveryCard(estimated: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Latte.copy(alpha = 0.35f)),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("☕ Estimated preparation", style = MaterialTheme.typography.titleMedium)
            Text(estimated, style = MaterialTheme.typography.headlineMedium, color = CoffeeBrown)
            Text(
                "Your order will be freshly prepared by our barista.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
            )
        }
    }
}

@Composable
private fun PlaceOrderBar(
    total: Double,
    currency: String,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "orderScale")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(Espresso)
            .clickable(interactionSource = interaction, indication = null, onClick = onPlaceOrder)
            .padding(18.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            "Place Order · ${formatPrice(currency, total)}",
            color = Cream,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
