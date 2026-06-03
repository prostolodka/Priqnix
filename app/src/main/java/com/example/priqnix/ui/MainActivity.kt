package com.example.priqnix.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work          // 👈 добавить
import androidx.compose.material.icons.filled.School        // 👈 добавить
import androidx.compose.material.icons.filled.Contacts      // 👈 добавить
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.priqnix.data.InfoDatabase
import com.example.priqnix.repository.InfoRepository
import com.example.priqnix.ui.screens.*
import com.example.priqnix.ui.theme.PriqnixTheme
import com.example.priqnix.viewmodel.InfoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = InfoDatabase.getInstance(this)
        val repository = InfoRepository(database.infoDao())
        setContent {
            PriqnixTheme {
                val viewModel: InfoViewModel = viewModel(factory = InfoViewModelFactory(repository))
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: InfoViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("home", "Главная", Icons.Default.Home),
        BottomNavItem("about", "О компании", Icons.Default.Info),
        BottomNavItem("products", "Продукты", Icons.Default.ShoppingCart),
        BottomNavItem("services", "Услуги", Icons.Default.Work),
        BottomNavItem("education", "Образование", Icons.Default.School),
        BottomNavItem("contacts", "Контакты", Icons.Default.Contacts)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("about") { AboutScreen() }
            composable("products") {
                var showDialog by remember { mutableStateOf(false) }
                CategoryListScreen(
                    category = "Product",
                    viewModel = viewModel,
                    navController = navController,
                    onAddClick = { showDialog = true }
                )
                if (showDialog) {
                    AddItemDialog(
                        category = "Product",
                        onDismiss = { showDialog = false },
                        onConfirm = { title, description, details ->
                            viewModel.addItem(
                                com.example.priqnix.data.InfoItem(
                                    title = title,
                                    description = description,
                                    category = "Product",
                                    details = details
                                )
                            ) {}
                        }
                    )
                }
            }
            composable("services") {
                var showDialog by remember { mutableStateOf(false) }
                CategoryListScreen(
                    category = "Service",
                    viewModel = viewModel,
                    navController = navController,
                    onAddClick = { showDialog = true }
                )
                if (showDialog) {
                    AddItemDialog(
                        category = "Service",
                        onDismiss = { showDialog = false },
                        onConfirm = { title, description, details ->
                            viewModel.addItem(
                                com.example.priqnix.data.InfoItem(
                                    title = title,
                                    description = description,
                                    category = "Service",
                                    details = details
                                )
                            ) {}
                        }
                    )
                }
            }
            composable("education") {
                var showDialog by remember { mutableStateOf(false) }
                CategoryListScreen(
                    category = "Education",
                    viewModel = viewModel,
                    navController = navController,
                    onAddClick = { showDialog = true }
                )
                if (showDialog) {
                    AddItemDialog(
                        category = "Education",
                        onDismiss = { showDialog = false },
                        onConfirm = { title, description, details ->
                            viewModel.addItem(
                                com.example.priqnix.data.InfoItem(
                                    title = title,
                                    description = description,
                                    category = "Education",
                                    details = details
                                )
                            ) {}
                        }
                    )
                }
            }
            composable("contacts") { ContactsScreen() }
            composable("detail/{itemId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
                InfoDetailScreen(itemId = id, viewModel = viewModel, navController = navController)
            }
        }
    }
}

data class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

class InfoViewModelFactory(private val repository: InfoRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}