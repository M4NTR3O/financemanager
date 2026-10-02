package com.bignerdranch.android.financemanager.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.bignerdranch.android.financemanager.ui.analytics.AnalyticsScreen
import com.bignerdranch.android.financemanager.ui.categories.CategoriesScreen
import com.bignerdranch.android.financemanager.ui.home.HomeScreen
import com.bignerdranch.android.financemanager.ui.setup.SetupScreen
import com.bignerdranch.android.financemanager.ui.setup.SetupViewModel
import com.bignerdranch.android.financemanager.ui.transactions.*

private object Routes {
    const val SETUP = "setup"
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
    const val EDIT = "edit"
    const val ANALYTICS = "analytics"
    const val CATEGORIES = "categories"
    fun edit(txId: Long? = null) = "$EDIT?txId=${txId ?: -1L}"
}

@Composable
fun AppNav() {
    val setupVm: SetupViewModel = hiltViewModel()
    val ready by setupVm.ready.collectAsState()
    val userExists by setupVm.userExists.collectAsState()

    when {
        !ready -> LoadingScreen()
        userExists == false -> SetupScreen(vm = setupVm)
        else -> MainScaffold()
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize())
}


@Composable
private fun MainScaffold() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val showBottomBar = route in setOf(Routes.HOME, Routes.TRANSACTIONS, Routes.ANALYTICS, Routes.CATEGORIES)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple(Routes.HOME, "Главная", Icons.Filled.Home),
                        Triple(Routes.TRANSACTIONS, "Операции", Icons.Filled.List),
                        Triple(Routes.ANALYTICS, "Аналитика", Icons.Filled.BarChart),
                        Triple(Routes.CATEGORIES, "Категории", Icons.Filled.Category)
                    )
                    items.forEach { (r, label, icon) ->
                        NavigationBarItem(
                            selected = route == r,
                            onClick = {
                                nav.navigate(r) {
                                    popUpTo(nav.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenTransactions = { nav.navigate(Routes.TRANSACTIONS) },
                    onAddTransaction = { nav.navigate(Routes.edit()) }
                )
            }
            composable(Routes.TRANSACTIONS) {
                TransactionListScreen(
                    onEdit = { id -> nav.navigate(Routes.edit(id)) },
                    onAdd = { nav.navigate(Routes.edit()) }
                )
            }
            composable(
                route = "${Routes.EDIT}?txId={txId}",
                arguments = listOf(navArgument("txId") { type = NavType.LongType; defaultValue = -1L })
            ) { entry ->
                val txId = entry.arguments?.getLong("txId")?.takeIf { it >= 0 }
                TransactionEditScreen(
                    txId = txId,
                    onDone = { nav.popBackStack() }
                )
            }
            composable(Routes.ANALYTICS) { AnalyticsScreen() }
            composable(Routes.CATEGORIES) { CategoriesScreen() }
        }
    }
}