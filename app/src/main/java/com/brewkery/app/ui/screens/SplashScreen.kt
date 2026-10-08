package com.brewkery.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brewkery.app.R
import com.brewkery.app.ui.theme.Cappuccino
import com.brewkery.app.ui.theme.Caramel
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.DarkCoffee
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import kotlinx.coroutines.delay

@Composable
fun BrewkerySplashScreen(
    isLoading: Boolean,
    onFinished: () -> Unit,
    onVisible: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var enterComplete by remember { mutableStateOf(false) }
    var minTimeComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onVisible()
        delay(80)
        enterComplete = true
        delay(2400)
        minTimeComplete = true
    }

    LaunchedEffect(isLoading, minTimeComplete) {
        if (minTimeComplete && !isLoading) {
            delay(350)
            onFinished()
        }
    }

    LaunchedEffect(minTimeComplete) {
        if (minTimeComplete && isLoading) {
            delay(3200)
            onFinished()
        }
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (enterComplete) 1f else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "splashFade",
    )
    val contentScale by animateFloatAsState(
        targetValue = if (enterComplete) 1f else 0.92f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "splashScale",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Espresso, DarkCoffee, Cappuccino, Latte, Cream),
                ),
            ),
    ) {
        DecorativeRings(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .scale(contentScale)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SteamAnimation(modifier = Modifier.padding(bottom = 8.dp))
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Cream,
                shadowElevation = 18.dp,
            ) {
                Image(
                    painter = painterResource(R.drawable.brewkery_logo),
                    contentDescription = "Brewkery",
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 22.dp)
                        .size(width = 260.dp, height = 120.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Brewkery",
                style = MaterialTheme.typography.headlineLarge,
                color = Cream,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Crafted Coffee. Freshly Baked.",
                style = MaterialTheme.typography.bodyMedium,
                color = Cream.copy(alpha = 0.88f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(36.dp))
            LoadingDots()
        }

        Text(
            text = "Warming up your brew…",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(contentAlpha * 0.75f),
            style = MaterialTheme.typography.labelMedium,
            color = Espresso.copy(alpha = 0.65f),
        )
    }
}

@Composable
private fun SteamAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "steam")
    val steam1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "s1",
    )
    val steam2 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Reverse),
        label = "s2",
    )

    Box(modifier = modifier.height(36.dp), contentAlignment = Alignment.BottomCenter) {
        Text("☕", style = MaterialTheme.typography.titleLarge, modifier = Modifier.alpha(0.9f))
        Text(
            "~",
            modifier = Modifier
                .offset(x = (-10).dp, y = (-18 - steam1 * 8).dp)
                .alpha(0.35f + steam1 * 0.35f),
            color = Cream,
        )
        Text(
            "~",
            modifier = Modifier
                .offset(x = 10.dp, y = (-16 - steam2 * 10).dp)
                .alpha(0.25f + steam2 * 0.4f),
            color = Cream,
        )
    }
}

@Composable
private fun LoadingDots() {
    val transition = rememberInfiniteTransition(label = "dots")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "dotPhase",
    )

    RowDots(phase)
}

@Composable
private fun RowDots(phase: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val active = phase.toInt() % 3 == index
            Box(
                modifier = Modifier
                    .size(if (active) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (active) Caramel else Cream.copy(alpha = 0.45f)),
            )
        }
    }
}

@Composable
private fun DecorativeRings(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(Cream.copy(alpha = 0.06f)),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .size(280.dp)
                .clip(CircleShape)
                .background(Caramel.copy(alpha = 0.08f)),
        )
    }
}
