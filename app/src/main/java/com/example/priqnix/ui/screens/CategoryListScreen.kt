package com.example.priqnix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.priqnix.data.FavoriteItem
import com.example.priqnix.data.InfoItem
import com.example.priqnix.ui.components.ListShimmer
import com.example.priqnix.viewmodel.FavoritesViewModel
import com.example.priqnix.viewmodel.InfoUiState
import com.example.priqnix.viewmodel.InfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    category: String,
    viewModel: InfoViewModel,
    navController: NavController,
    onAddClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf(viewModel.searchQuery) }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by favoritesViewModel?.snackbarMessage?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            favoritesViewModel?.clearSnackbar()
        }
    }

    LaunchedEffect(category) {
        viewModel.loadItems(category)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(when(category) {
                    "Product" -> "Наши продукты"
                    "Service" -> "Услуги"
                    "Education" -> "Образование"
                    else -> category
                }) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    viewModel.searchQuery = query
                    viewModel.loadItems(category, query)
                },
                label = { Text("Поиск") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (val state = uiState) {
                is InfoUiState.Loading -> {
                    ListShimmer()
                }
                is InfoUiState.Success -> {
                    if (state.items.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Нет записей. Нажмите + для добавления.")
                        }
                    } else {
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(state.items) { item ->
                                    InfoCard(
                                        item = item,
                                        navController = navController,
                                        favoritesViewModel = favoritesViewModel
                                    )
                                }
                            }
                    }
                }
                is InfoUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ошибка: ${state.message}")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    item: InfoItem,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel? = null
) {
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(item.id) {
        favoritesViewModel?.let { vm ->
            vm.isFavorite(item.id).collect { isFavorite = it }
        }
    }

    Card(
        onClick = { navController.navigate("detail/${item.id}") },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                Text(text = item.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.description, maxLines = 2)
            }
            if (favoritesViewModel != null) {
                IconButton(onClick = {
                    favoritesViewModel.toggleFavorite(
                        FavoriteItem(item.id, item.title, item.category)
                    )
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}