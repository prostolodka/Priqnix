package com.example.priqnix.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS employees (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        position TEXT NOT NULL,
                        photoUrl TEXT NOT NULL DEFAULT '',
                        description TEXT NOT NULL DEFAULT ''
                    )
                """)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS favorites (
                        infoItemId INTEGER PRIMARY KEY NOT NULL,
                        itemTitle TEXT NOT NULL,
                        itemCategory TEXT NOT NULL
                    )
                """)
            }
        }
        @Volatile
        private var INSTANCE: InfoDatabase? = null

        fun getInstance(context: Context): InfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InfoDatabase::class.java,
                    "info_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}