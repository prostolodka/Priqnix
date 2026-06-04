package com.example.priqnix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.priqnix.data.Employee
import com.example.priqnix.ui.components.ListShimmer
import com.example.priqnix.viewmodel.EmployeesViewModel
import com.example.priqnix.viewmodel.EmployeeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    viewModel: EmployeesViewModel,
    onEmployeeClick: (Int) -> Unit = {}
) {
    val filteredEmployees by viewModel.filteredEmployees.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackbarEvent by viewModel.event.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var editingEmployee by remember { mutableStateOf<Employee?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarEvent) {
        val event = snackbarEvent
        if (event is EmployeeEvent.ShowSnackbar) {
            snackbarHostState.showSnackbar(event.message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingEmployee = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        topBar = {
            TopAppBar(title = { Text("Сотрудники") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по имени или должности") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            if (isLoading) {
                ListShimmer()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredEmployees, key = { it.id }) { employee ->
                        EmployeeCard(
                            employee = employee,
                            onClick = { onEmployeeClick(employee.id) },
                            onEdit = {
                                editingEmployee = employee
                                showDialog = true
                            },
                            onDelete = { viewModel.deleteEmployee(employee) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        EmployeeDialog(
            employee = editingEmployee,
            onDismiss = { showDialog = false },
            onSave = { name, position, description, photoUrl ->
                if (editingEmployee == null) {
                    viewModel.addEmployee(
                        Employee(
                            name = name,
                            position = position,
                            description = description,
                            photoUrl = photoUrl
                        )
                    )
                } else {
                    viewModel.updateEmployee(
                        editingEmployee!!.copy(
                            name = name,
                            position = position,
                            description = description,
                            photoUrl = photoUrl
                        )
                    )
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun EmployeeCard(
    employee: Employee,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (employee.photoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(employee.photoUrl),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column {
                    Text(employee.name, style = MaterialTheme.typography.titleMedium)
                    Text(employee.position, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
        }
    }
}

@Composable
fun EmployeeDialog(
    employee: Employee?,
    onDismiss: () -> Unit,
    onSave: (name: String, position: String, description: String, photoUrl: String?) -> Unit
) {
    var name by remember { mutableStateOf(employee?.name ?: "") }
    var position by remember { mutableStateOf(employee?.position ?: "") }
    var description by remember { mutableStateOf(employee?.description ?: "") }
    var photoUrl by remember { mutableStateOf(employee?.photoUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (employee == null) "Добавить сотрудника" else "Редактировать") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ФИО") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Должность") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = photoUrl,
                    onValueChange = { photoUrl = it },
                    label = { Text("URL фото (необязательно)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        name,
                        position,
                        description,
                        photoUrl.ifBlank { null }
                    )
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}