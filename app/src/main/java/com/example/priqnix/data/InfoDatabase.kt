package com.example.priqnix.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [InfoItem::class, Employee::class, FavoriteItem::class],
    version = 3,
    exportSchema = false
)
abstract class InfoDatabase : RoomDatabase() {
    abstract fun infoDao(): InfoDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: InfoDatabase? = null

        fun getInstance(context: Context): InfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InfoDatabase::class.java,
                    "info_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}