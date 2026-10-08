package com.brewkery.app.ui.util

fun formatPrice(symbol: String, amount: Double): String =
    "$symbol${"%.2f".format(amount)}"

fun isDessertItem(name: String, badge: String?): Boolean =
    name.contains("Danish", ignoreCase = true) ||
        badge?.contains("SEASONAL", ignoreCase = true) == true
