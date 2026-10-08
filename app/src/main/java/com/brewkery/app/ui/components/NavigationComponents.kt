package com.brewkery.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.brewkery.app.ui.theme.CoffeeBrown
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.MutedText
import com.brewkery.app.ui.util.formatPrice

enum class BrewTab(val route: String, val label: String, val icon: ImageVector) {
    HOME("home", "Home", Icons.Outlined.Home),
    MENU("menu", "Menu", Icons.Outlined.LocalCafe),
    CART("cart", "Cart", Icons.Outlined.ShoppingCart),
}

@Composable
fun BrewkeryBottomBar(
    selected: BrewTab,
    onSelect: (BrewTab) -> Unit,
    cartCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrewTab.entries.forEach { tab ->
                val selectedTab = tab == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelect(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            tab.icon,
                            contentDescription = tab.label,
                            tint = if (selectedTab) CoffeeBrown else MutedText,
                            modifier = Modifier.size(24.dp),
                        )
                        if (tab == BrewTab.CART && cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(CoffeeBrown, CircleShape),
                            )
                        }
                    }
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selectedTab) CoffeeBrown else MutedText,
                    )
                    val indicator by animateDpAsState(if (selectedTab) 4.dp else 0.dp, label = "navIndicator")
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(width = 24.dp, height = indicator)
                            .background(CoffeeBrown, CircleShape),
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingCartBar(
    itemCount: Int,
    total: Double,
    currencySymbol: String,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable(onClick = onViewCart),
        shape = RoundedCornerShape(20.dp),
        color = Espresso,
        shadowElevation = 10.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🛒 $itemCount items", color = Cream, style = MaterialTheme.typography.titleMedium)
                Text(formatPrice(currencySymbol, total), color = Cream, style = MaterialTheme.typography.titleLarge)
            }
            Text(
                "View Cart →",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Cream.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun BrewkeryErrorState(
    title: String = "Oops! The coffee machine took a break.",
    subtitle: String = "We couldn't load the menu right now.",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("☕💨", style = MaterialTheme.typography.displaySmall)
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MutedText)
        Text(
            "Try Again",
            modifier = Modifier
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CoffeeBrown)
                .clickable(onClick = onRetry)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            color = Cream,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
