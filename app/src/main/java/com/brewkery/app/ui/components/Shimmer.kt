package com.brewkery.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.brewkery.app.ui.theme.Cappuccino
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.Cream

@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    return Brush.linearGradient(
        colors = listOf(Latte, Cream, Cappuccino, Latte),
        start = Offset(translate - 300f, translate - 300f),
        end = Offset(translate, translate),
    )
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier, cornerRadius: Int = 20) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(shimmerBrush()),
    )
}

@Composable
fun HomeLoadingShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(20.dp)) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(56.dp), cornerRadius = 16)
        Spacer(Modifier.height(16.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(200.dp), cornerRadius = 28)
        Spacer(Modifier.height(16.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(52.dp), cornerRadius = 20)
        Spacer(Modifier.height(24.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f).height(24.dp), cornerRadius = 8)
        Spacer(Modifier.height(12.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp), cornerRadius = 24)
    }
}
