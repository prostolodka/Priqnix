package com.example.priqnix.repository

import com.example.priqnix.data.InfoDao
import com.example.priqnix.data.InfoItem
import kotlinx.coroutines.flow.Flow

class InfoRepository(private val infoDao: InfoDao) {

    fun getByCategory(category: String): Flow<List<InfoItem>> = infoDao.getByCategory(category)

    fun searchInCategory(category: String, query: String): Flow<List<InfoItem>> = infoDao.searchInCategory(category, "%$query%")

    suspend fun getItemById(id: Int): InfoItem? = infoDao.getById(id)

    suspend fun insert(item: InfoItem) {
        infoDao.insert(item)
    }

    suspend fun prefillIfEmpty(category: String, items: List<InfoItem>) {
        if (infoDao.getCountByCategory(category) == 0) {
            infoDao.insertAll(items)
        }
    }
}