package com.brewkery.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.brewkery.app.ui.theme.CoffeeBrown
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.MutedText
import com.brewkery.app.ui.util.formatPrice
import com.brewkery.app.ui.viewmodel.CartViewModel

private data class OrderStep(val label: String, val done: Boolean, val current: Boolean)

@Composable
fun OrderSuccessScreen(
    orderId: String,
    cartViewModel: CartViewModel,
    onOrderMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items by cartViewModel.items.collectAsState()
    val currency by cartViewModel.currencySymbol.collectAsState()
    val estimated by cartViewModel.estimatedTime.collectAsState()
    val total = cartViewModel.summary().total

    val steps = listOf(
        OrderStep("Order Placed", done = true, current = false),
        OrderStep("Order Accepted", done = true, current = false),
        OrderStep("Brewing", done = false, current = true),
        OrderStep("Ready for Pickup", done = false, current = false),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("☕", style = MaterialTheme.typography.displaySmall)
        Text("Your coffee is brewing!", style = MaterialTheme.typography.titleLarge)
        Text("Great choice. Your order is on its way.", style = MaterialTheme.typography.bodyMedium, color = MutedText)

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Latte.copy(alpha = 0.4f)),
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ORDER TICKET", style = MaterialTheme.typography.labelMedium, color = MutedText)
                Text("\"$orderId\"", style = MaterialTheme.typography.headlineMedium, color = CoffeeBrown)
            }
        }

        Column(Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
            steps.forEachIndexed { index, step ->
                OrderTimelineRow(step)
                if (index < steps.lastIndex) {
                    Text("↓", modifier = Modifier.padding(start = 14.dp), color = MutedText)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(estimated.replace("minutes", "min"), style = MaterialTheme.typography.headlineMedium, color = CoffeeBrown)
                Text("Estimated preparation time", color = MutedText)
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Order Summary", style = MaterialTheme.typography.titleLarge)
                items.forEach { line ->
                    Text("${line.quantity} × ${line.name}", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(8.dp))
                Text("Total: ${formatPrice(currency, total)}", style = MaterialTheme.typography.headlineMedium, color = CoffeeBrown)
            }
        }

        Text(
            "Order More Coffee",
            modifier = Modifier
                .padding(top = 24.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Espresso)
                .clickable(onClick = onOrderMore)
                .padding(horizontal = 28.dp, vertical = 14.dp),
            color = Cream,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun OrderTimelineRow(step: OrderStep) {
    val transition = rememberInfiniteTransition(label = "steam")
    val steamAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "steamAlpha",
    )

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = when {
                    step.done -> "✓"
                    step.current -> "☕"
                    else -> "○"
                },
                modifier = if (step.current) Modifier.alpha(steamAlpha) else Modifier,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Text(
            step.label,
            style = MaterialTheme.typography.titleMedium,
            color = if (step.current) CoffeeBrown else MaterialTheme.colorScheme.onBackground,
        )
    }
}
