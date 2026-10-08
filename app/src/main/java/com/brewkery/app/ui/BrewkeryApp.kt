package com.brewkery.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brewkery.app.data.repository.BrewkeryRepository
import com.brewkery.app.ui.components.BrewTab
import com.brewkery.app.ui.components.BrewkeryBottomBar
import com.brewkery.app.ui.components.FloatingCartBar
import com.brewkery.app.ui.screens.CartScreen
import com.brewkery.app.ui.screens.HomeScreen
import com.brewkery.app.ui.screens.MenuScreen
import com.brewkery.app.ui.screens.BrewkerySplashScreen
import com.brewkery.app.ui.screens.OrderSuccessScreen
import com.brewkery.app.ui.screens.ProductDetailScreen
import com.brewkery.app.ui.viewmodel.CartViewModel
import com.brewkery.app.ui.viewmodel.MenuViewModel
import com.brewkery.app.ui.viewmodel.ProductDetailViewModel

object Routes {
    const val HOME = "home"
    const val MENU = "menu"
    const val CART = "cart"
    const val PRODUCT = "product/{id}"
    const val ORDER_SUCCESS = "order_success/{orderId}"

    fun product(id: Int) = "product/$id"
    fun orderSuccess(orderId: String) = "order_success/$orderId"
}

private fun NavHostController.navigateToMainTab(route: String) {
    if (currentDestination?.route == route) return

    if (route == Routes.HOME) {
        val returnedToHome = popBackStack(Routes.HOME, inclusive = false)
        if (!returnedToHome) {
            navigate(Routes.HOME) {
                popUpTo(graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
        return
    }

    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun BrewkeryApp(
    repository: BrewkeryRepository = BrewkeryRepository(),
    onComposeSplashVisible: () -> Unit = {},
) {
    var showSplash by remember { mutableStateOf(true) }
    val navController = rememberNavController()
    val menuViewModel: MenuViewModel = viewModel(factory = MenuViewModel.factory(repository))
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModel.Factory)
    val menuUiState by menuViewModel.uiState.collectAsState()

    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route
    val showBottomBar = currentRoute in setOf(Routes.HOME, Routes.MENU, Routes.CART)
    val selectedTab = when (currentRoute) {
        Routes.MENU -> BrewTab.MENU
        Routes.CART -> BrewTab.CART
        else -> BrewTab.HOME
    }

    val cartItems by cartViewModel.items.collectAsState()
    val currency by cartViewModel.currencySymbol.collectAsState()
    val cartSummary = cartViewModel.summary()
    val showFloatingCart =
        showBottomBar && cartItems.isNotEmpty() && currentRoute != Routes.CART

    Box(Modifier.fillMaxSize()) {
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BrewkeryBottomBar(
                    selected = selectedTab,
                    cartCount = cartSummary.itemCount,
                    onSelect = { tab -> navController.navigateToMainTab(tab.route) },
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        menuViewModel = menuViewModel,
                        cartViewModel = cartViewModel,
                        onProductClick = { navController.navigate(Routes.product(it)) },
                        onExploreMenu = { navController.navigateToMainTab(Routes.MENU) },
                    )
                }
                composable(Routes.MENU) {
                    MenuScreen(
                        menuViewModel = menuViewModel,
                        cartViewModel = cartViewModel,
                        onProductClick = { navController.navigate(Routes.product(it)) },
                    )
                }
                composable(Routes.CART) {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        onExplore = { navController.navigateToMainTab(Routes.HOME) },
                        onPlaceOrder = { ticket ->
                            navController.navigate(Routes.orderSuccess(ticket))
                        },
                    )
                }
                composable(
                    route = Routes.PRODUCT,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val id = entry.arguments?.getInt("id") ?: return@composable
                    val detailViewModel: ProductDetailViewModel = viewModel(
                        key = "detail_$id",
                        factory = ProductDetailViewModel.factory(repository, id),
                    )
                    ProductDetailScreen(
                        viewModel = detailViewModel,
                        cartViewModel = cartViewModel,
                        onBack = { navController.popBackStack() },
                        onAdded = { navController.popBackStack() },
                    )
                }
                composable(
                    route = Routes.ORDER_SUCCESS,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
                ) { entry ->
                    val orderId = entry.arguments?.getString("orderId").orEmpty()
                    OrderSuccessScreen(
                        orderId = orderId,
                        cartViewModel = cartViewModel,
                        onOrderMore = {
                            cartViewModel.clear()
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                    )
                }
            }

            AnimatedVisibility(
                visible = showFloatingCart,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                FloatingCartBar(
                    itemCount = cartSummary.itemCount,
                    total = cartSummary.subtotal,
                    currencySymbol = currency,
                    onViewCart = { navController.navigateToMainTab(Routes.CART) },
                )
            }
        }
    }

        AnimatedVisibility(
            visible = showSplash,
            exit = fadeOut(animationSpec = tween(500)),
        ) {
            BrewkerySplashScreen(
                isLoading = menuUiState.isLoading,
                onFinished = { showSplash = false },
                onVisible = onComposeSplashVisible,
            )
        }
    }
}
