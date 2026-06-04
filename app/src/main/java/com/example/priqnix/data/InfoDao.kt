package com.example.priqnix.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InfoDao {
    @Query("SELECT * FROM info_items WHERE category = :category ORDER BY title ASC")
    fun getByCategory(category: String): Flow<List<InfoItem>>

    @Query("SELECT * FROM info_items WHERE (title LIKE :query OR category LIKE :query OR details LIKE :query) AND category = :category")
    fun searchInCategory(category: String, query: String): Flow<List<InfoItem>>

    @Query("SELECT * FROM info_items WHERE id = :id")
    suspend fun getById(id: Int): InfoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InfoItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InfoItem)

    @Query("SELECT COUNT(*) FROM info_items WHERE category = :category")
    suspend fun getCountByCategory(category: String): Int
}