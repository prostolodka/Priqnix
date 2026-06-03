package com.example.priqnix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.priqnix.data.InfoItem
import com.example.priqnix.viewmodel.InfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDetailScreen(
    itemId: Int,
    viewModel: InfoViewModel,
    navController: NavController
) {
    var item by remember { mutableStateOf<InfoItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(itemId) {
        viewModel.getItem(itemId) { result ->
            item = result
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детальная информация") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else if (item == null) {
                Text("Запись не найдена", modifier = Modifier.fillMaxSize().padding(16.dp))
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item!!.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Категория: ${item!!.category}", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(item!!.description, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Подробнее:", style = MaterialTheme.typography.titleMedium)
                    Text(item!!.details)
                }
            }
        }
    }
}