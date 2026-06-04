package com.example.priqnix.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey
    val infoItemId: Int,
    val itemTitle: String,
    val itemCategory: String
)
