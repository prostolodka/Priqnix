package com.example.priqnix.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info_items")
data class InfoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val details: String
)