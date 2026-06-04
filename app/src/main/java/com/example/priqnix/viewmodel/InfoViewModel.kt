package com.example.priqnix.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priqnix.data.InfoItem
import com.example.priqnix.repository.InfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class InfoUiState {
    object Loading : InfoUiState()
    data class Success(val items: List<InfoItem>) : InfoUiState()
    data class Error(val message: String) : InfoUiState()
}

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val repository: InfoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<InfoUiState>(InfoUiState.Loading)
    val uiState: StateFlow<InfoUiState> = _uiState.asStateFlow()

    var searchQuery: String
        get() = savedStateHandle["searchQuery"] ?: ""
        set(value) { savedStateHandle["searchQuery"] = value }

    private var currentCategory = ""
    private var currentQuery = ""

    fun loadItems(category: String, query: String = "") {
        currentCategory = category
        currentQuery = query
        viewModelScope.launch {
            _uiState.value = InfoUiState.Loading
            val items = if (query.isBlank()) {
                repository.getByCategory(category)
            } else {
                repository.searchInCategory(category, query)
            }
            items.collect { list ->
                _uiState.value = InfoUiState.Success(list)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun getItem(id: Int, onResult: (InfoItem?) -> Unit) {
        viewModelScope.launch {
            val item = repository.getItemById(id)
            onResult(item)
        }
    }

    fun addItem(item: InfoItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insert(item)
            loadItems(currentCategory, currentQuery)
            onComplete()
        }
    }

    fun prefillData(category: String, defaultItems: List<InfoItem>) {
        viewModelScope.launch {
            repository.prefillIfEmpty(category, defaultItems)
            loadItems(category, currentQuery)
        }
    }
}