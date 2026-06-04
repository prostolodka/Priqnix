package com.example.priqnix.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY itemCategory, itemTitle")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Query("SELECT * FROM favorites WHERE infoItemId = :id")
    suspend fun getFavoriteById(id: Int): FavoriteItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteItem)

    @Query("DELETE FROM favorites WHERE infoItemId = :id")
    suspend fun removeFavorite(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE infoItemId = :id)")
    fun isFavorite(id: Int): Flow<Boolean>
}
