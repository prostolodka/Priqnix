package com.example.priqnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priqnix.data.Employee
import com.example.priqnix.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _filteredEmployees = MutableStateFlow<List<Employee>>(emptyList())
    val filteredEmployees: StateFlow<List<Employee>> = _filteredEmployees.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _event = MutableStateFlow<EmployeeEvent?>(null)
    val event: StateFlow<EmployeeEvent?> = _event.asStateFlow()

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllEmployees().collect { list ->
                _employees.value = list
                applyFilter()
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        _filteredEmployees.value = if (query.isEmpty()) {
            _employees.value
        } else {
            _employees.value.filter { emp ->
                emp.name.lowercase().contains(query) ||
                emp.position.lowercase().contains(query)
            }
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.addEmployee(employee)
            _event.value = EmployeeEvent.ShowSnackbar("Сотрудник добавлен")
            clearEvent()
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
            _event.value = EmployeeEvent.ShowSnackbar("Сотрудник обновлён")
            clearEvent()
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
            _event.value = EmployeeEvent.ShowSnackbar("Сотрудник удалён")
            clearEvent()
        }
    }

    suspend fun getEmployeeById(id: Int): Employee? = repository.getEmployeeById(id)

    private fun clearEvent() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _event.value = null
        }
    }
}

sealed class EmployeeEvent {
    data class ShowSnackbar(val message: String) : EmployeeEvent()
}