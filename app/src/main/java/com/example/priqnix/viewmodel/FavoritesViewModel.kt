package com.example.priqnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priqnix.data.FavoriteItem
import com.example.priqnix.data.InfoItem
import com.example.priqnix.repository.FavoriteRepository
import com.example.priqnix.repository.InfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _favoriteItems = MutableStateFlow<List<InfoItem>>(emptyList())
    val favoriteItems: StateFlow<List<InfoItem>> = _favoriteItems.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favoriteRepository.getAllFavorites().collect { favs ->
                val items = favs.mapNotNull { infoRepository.getItemById(it.infoItemId) }
                _favoriteItems.value = items
            }
        }
    }

    fun isFavorite(infoItemId: Int) = favoriteRepository.isFavorite(infoItemId)

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun toggleFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            val isFav = favoriteRepository.isFavorite(item.infoItemId).first()
            favoriteRepository.toggleFavorite(item, isFav)
            _snackbarMessage.value = if (isFav) "Удалено из избранного" else "Добавлено в избранное"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
