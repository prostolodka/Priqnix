package com.example.priqnix.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.priqnix.data.DatabaseInitializer
import com.example.priqnix.ui.screens.*
import com.example.priqnix.ui.theme.PriqnixTheme
import com.example.priqnix.viewmodel.EmployeesViewModel
import com.example.priqnix.viewmodel.FavoritesViewModel
import com.example.priqnix.viewmodel.InfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var databaseInitializer: DatabaseInitializer

    private val _isDbReady = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !_isDbReady.value }

        lifecycleScope.launch {
            databaseInitializer.initialize()
            _isDbReady.value = true
        }

        val themeManager = ThemeManager(applicationContext)

        setContent {
            val isDarkTheme by themeManager.isDarkTheme.collectAsStateWithLifecycle()
            PriqnixTheme(darkTheme = isDarkTheme) {
                if (_isDbReady.value) {
                    val infoViewModel: InfoViewModel = hiltViewModel()
                    val employeeViewModel: EmployeesViewModel = hiltViewModel()
                    val favoritesViewModel: FavoritesViewModel = hiltViewModel()
                    AppNavigation(infoViewModel, employeeViewModel, favoritesViewModel, onThemeToggle = { themeManager.toggleTheme() })
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(infoViewModel: InfoViewModel, employeeViewModel: EmployeesViewModel, favoritesViewModel: FavoritesViewModel, onThemeToggle: () -> Unit = {}) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("home", "Главная", Icons.Default.Home),
        BottomNavItem("products", "Продукты", Icons.Default.ShoppingCart),
        BottomNavItem("services", "Услуги", Icons.Default.Work),
        BottomNavItem("education", "Образование", Icons.Default.School),
        BottomNavItem("employees", "Сотрудники", Icons.Default.People),
        BottomNavItem("favorites", "Избранное", Icons.Default.Favorite),
        BottomNavItem("about", "О компании", Icons.Default.Info),
        BottomNavItem("faq", "FAQ", Icons.Default.QuestionAnswer),
        BottomNavItem("contacts", "Контакты", Icons.Default.Contacts)
    )

    val favoriteCount by favoritesViewModel.favoriteItems.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            if (item.route == "favorites") {
                                BadgedBox(badge = {
                                    if (favoriteCount.isNotEmpty()) {
                                        Badge { Text(favoriteCount.size.toString()) }
                                    }
                                }) {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            } else {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        },
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
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable("home") { HomeScreen(navController = navController, onThemeToggle = onThemeToggle) }
            composable("about") { AboutScreen() }
            composable("products") { CategoryScreen(category = "Product", viewModel = infoViewModel, navController = navController, favoritesViewModel = favoritesViewModel) }
            composable("services") { CategoryScreen(category = "Service", viewModel = infoViewModel, navController = navController, favoritesViewModel = favoritesViewModel) }
            composable("education") { CategoryScreen(category = "Education", viewModel = infoViewModel, navController = navController, favoritesViewModel = favoritesViewModel) }
            composable("faq") { FaqScreen() }
            composable("contacts") { ContactsScreen() }
            composable("detail/{itemId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
                InfoDetailScreen(itemId = id, viewModel = infoViewModel, navController = navController)
            }
            composable("favorites") { FavoritesScreen(navController = navController) }
            composable("employees") {
                EmployeesScreen(
                    viewModel = employeeViewModel,
                    onEmployeeClick = { id -> navController.navigate("employee_detail/$id") }
                )
            }
            composable("employee_detail/{employeeId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("employeeId")?.toIntOrNull() ?: 0
                EmployeeDetailScreen(
                    employeeId = id,
                    viewModel = employeeViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun CategoryScreen(category: String, viewModel: InfoViewModel, navController: NavController, favoritesViewModel: FavoritesViewModel? = null) {
    var showDialog by remember { mutableStateOf(false) }
    CategoryListScreen(
        category = category,
        viewModel = viewModel,
        navController = navController,
        onAddClick = { showDialog = true },
        favoritesViewModel = favoritesViewModel
    )
    if (showDialog) {
        AddItemDialog(
            category = category,
            onDismiss = { showDialog = false },
            onConfirm = { title, description, details ->
                viewModel.addItem(
                    com.example.priqnix.data.InfoItem(
                        title = title,
                        description = description,
                        category = category,
                        details = details
                    )
                ) {}
            }
        )
    }
}

data class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)