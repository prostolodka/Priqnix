package com.example.priqnix.repository

import com.example.priqnix.data.FavoriteDao
import com.example.priqnix.data.FavoriteItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    fun getAllFavorites(): Flow<List<FavoriteItem>> = favoriteDao.getAllFavorites()

    fun isFavorite(infoItemId: Int): Flow<Boolean> = favoriteDao.isFavorite(infoItemId)

    suspend fun toggleFavorite(item: FavoriteItem, isFav: Boolean) {
        if (isFav) {
            favoriteDao.removeFavorite(item.infoItemId)
        } else {
            favoriteDao.addFavorite(item)
        }
    }
}
