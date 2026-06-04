package com.example.priqnix.di

import android.content.Context
import com.example.priqnix.data.DatabaseInitializer
import com.example.priqnix.data.EmployeeDao
import com.example.priqnix.data.FavoriteDao
import com.example.priqnix.data.InfoDao
import com.example.priqnix.data.InfoDatabase
import com.example.priqnix.repository.EmployeeRepository
import com.example.priqnix.repository.FavoriteRepository
import com.example.priqnix.repository.InfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InfoDatabase {
        return InfoDatabase.getInstance(context)
    }

    @Provides
    fun provideInfoDao(database: InfoDatabase): InfoDao {
        return database.infoDao()
    }

    @Provides
    fun provideEmployeeDao(database: InfoDatabase): EmployeeDao {
        return database.employeeDao()
    }

    @Provides
    fun provideFavoriteDao(database: InfoDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideInfoRepository(infoDao: InfoDao): InfoRepository {
        return InfoRepository(infoDao)
    }

    @Provides
    @Singleton
    fun provideEmployeeRepository(employeeDao: EmployeeDao): EmployeeRepository {
        return EmployeeRepository(employeeDao)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(favoriteDao: FavoriteDao): FavoriteRepository {
        return FavoriteRepository(favoriteDao)
    }

    @Provides
    @Singleton
    fun provideDatabaseInitializer(database: InfoDatabase): DatabaseInitializer {
        return DatabaseInitializer(database)
    }
}
