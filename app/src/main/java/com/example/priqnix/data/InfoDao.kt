package com.example.priqnix.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InfoDao {
    @Query("SELECT * FROM info_items WHERE category = :category ORDER BY title ASC")
    suspend fun getByCategory(category: String): List<InfoItem>

    @Query("SELECT * FROM info_items WHERE (title LIKE :query OR category LIKE :query OR details LIKE :query) AND category = :category")
    suspend fun searchInCategory(category: String, query: String): List<InfoItem>

    @Query("SELECT * FROM info_items WHERE id = :id")
    suspend fun getById(id: Int): InfoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InfoItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InfoItem)

    @Query("SELECT COUNT(*) FROM info_items WHERE category = :category")
    suspend fun getCountByCategory(category: String): Int
}