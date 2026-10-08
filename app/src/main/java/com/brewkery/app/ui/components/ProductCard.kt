package com.brewkery.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brewkery.app.data.model.MenuItemSummary
import com.brewkery.app.ui.theme.Caramel
import com.brewkery.app.ui.theme.CoffeeBrown
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.DarkCoffee
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.MutedText
import com.brewkery.app.ui.util.formatPrice

@Composable
fun PopularProductCard(
    item: MenuItemSummary,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAdd: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "cardScale")

    Card(
        modifier = modifier
            .width(210.dp)
            .scale(scale)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop,
            )
            item.badge?.let { badge ->
                Text(
                    text = badge,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Espresso.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Cream,
                )
            }
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.tagline,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Caramel, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(item.rating),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatPrice(currencySymbol, item.basePrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = CoffeeBrown,
                )
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier
                        .size(40.dp)
                        .background(CoffeeBrown, CircleShape),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Cream)
                }
            }
        }
    }
}

@Composable
fun MenuGridCard(
    item: MenuItemSummary,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    formatPrice(currencySymbol, item.basePrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = CoffeeBrown,
                )
            }
        }
    }
}

@Composable
fun PromoOfferCard(onOrder: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(listOf(DarkCoffee, Caramel)),
                    RoundedCornerShape(24.dp),
                )
                .padding(20.dp),
        ) {
            Column {
                Text("Morning Brew", style = MaterialTheme.typography.headlineMedium, color = Cream)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Get your favorite coffee + fresh pastry.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cream.copy(alpha = 0.9f),
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Order Now →",
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Cream.copy(alpha = 0.15f))
                        .clickable(onClick = onOrder)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Cream,
                )
            }
        }
    }
}
