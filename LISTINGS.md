# Листинги кода для приложения диплома

Скопируйте каждый листинг в приложение диплома как отдельный номер (А.1, А.2, ...).

---

## А.1 — InfoItem.kt (сущность продукта/услуги/курса)

```kotlin
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
```

---

## А.2 — InfoDao.kt (DAO для продуктов/услуг/курсов)

```kotlin
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
```

---

## А.3 — InfoDatabase.kt (Room БД с миграциями)

```kotlin
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
```

---

## А.4 — InfoRepository.kt

```kotlin
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
```

---

## А.5 — InfoViewModel.kt

```kotlin
package com.example.priqnix.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priqnix.data.InfoItem
import com.example.priqnix.repository.InfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class InfoUiState {
    object Loading : InfoUiState()
    data class Success(val items: List<InfoItem>) : InfoUiState()
    data class Error(val message: String) : InfoUiState()
}

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val repository: InfoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<InfoUiState>(InfoUiState.Loading)
    val uiState: StateFlow<InfoUiState> = _uiState.asStateFlow()

    var searchQuery: String
        get() = savedStateHandle["searchQuery"] ?: ""
        set(value) { savedStateHandle["searchQuery"] = value }

    private var currentCategory = ""
    private var currentQuery = ""

    fun loadItems(category: String, query: String = "") {
        currentCategory = category
        currentQuery = query
        viewModelScope.launch {
            _uiState.value = InfoUiState.Loading
            val items = if (query.isBlank()) {
                repository.getByCategory(category)
            } else {
                repository.searchInCategory(category, query)
            }
            items.collect { list ->
                _uiState.value = InfoUiState.Success(list)
            }
        }
    }

    fun getItem(id: Int, onResult: (InfoItem?) -> Unit) {
        viewModelScope.launch {
            val item = repository.getItemById(id)
            onResult(item)
        }
    }

    fun addItem(item: InfoItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insert(item)
            loadItems(currentCategory, currentQuery)
            onComplete()
        }
    }
}
```

---

## А.6 — Employee.kt (сущность сотрудника)

```kotlin
package com.example.priqnix.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val position: String,
    val description: String,
    val photoUrl: String? = null
)
```

---

## А.7 — EmployeeDao.kt

```kotlin
package com.example.priqnix.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees ORDER BY name")
    fun getAllEmployees(): Flow<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee)

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Int): Employee?
}
```

---

## А.8 — FavoriteItem.kt (сущность избранного)

```kotlin
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
```

---

## А.9 — FavoriteDao.kt

```kotlin
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
```

---

## А.10 — FavoriteRepository.kt

```kotlin
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
        if (isFav) favoriteDao.removeFavorite(item.infoItemId)
        else favoriteDao.addFavorite(item)
    }
}
```

---

## А.11 — EmployeeRepository.kt

```kotlin
package com.example.priqnix.repository

import com.example.priqnix.data.Employee
import com.example.priqnix.data.EmployeeDao
import kotlinx.coroutines.flow.Flow

class EmployeeRepository(private val employeeDao: EmployeeDao) {
    fun getAllEmployees(): Flow<List<Employee>> = employeeDao.getAllEmployees()
    suspend fun addEmployee(employee: Employee) = employeeDao.insertEmployee(employee)
    suspend fun updateEmployee(employee: Employee) = employeeDao.updateEmployee(employee)
    suspend fun deleteEmployee(employee: Employee) = employeeDao.deleteEmployee(employee)
    suspend fun getEmployeeById(id: Int): Employee? = employeeDao.getEmployeeById(id)
}
```

---

## А.12 — FavoritesViewModel.kt

```kotlin
package com.example.priqnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priqnix.data.FavoriteItem
import com.example.priqnix.data.InfoItem
import com.example.priqnix.repository.FavoriteRepository
import com.example.priqnix.repository.InfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _favoriteItems = MutableStateFlow<List<InfoItem>>(emptyList())
    val favoriteItems: StateFlow<List<InfoItem>> = _favoriteItems.asStateFlow()

    init { loadFavorites() }

    fun loadFavorites() {
        viewModelScope.launch {
            favoriteRepository.getAllFavorites().collect { favs ->
                val items = favs.mapNotNull { infoRepository.getItemById(it.infoItemId) }
                _favoriteItems.value = items
            }
        }
    }

    fun isFavorite(infoItemId: Int) = favoriteRepository.isFavorite(infoItemId)

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun toggleFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            val isFav = favoriteRepository.isFavorite(item.infoItemId).first()
            favoriteRepository.toggleFavorite(item, isFav)
            _snackbarMessage.value = if (isFav) "Удалено из избранного" else "Добавлено в избранное"
        }
    }

    fun clearSnackbar() { _snackbarMessage.value = null }
}
```

---

## А.13 — EmployeesViewModel.kt

```kotlin
package com.example.priqnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priqnix.data.Employee
import com.example.priqnix.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _filteredEmployees = MutableStateFlow<List<Employee>>(emptyList())
    val filteredEmployees: StateFlow<List<Employee>> = _filteredEmployees.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _event = MutableStateFlow<EmployeeEvent?>(null)
    val event: StateFlow<EmployeeEvent?> = _event.asStateFlow()

    init { loadEmployees() }

    fun loadEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllEmployees().collect { list ->
                _employees.value = list
                applyFilter()
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        _filteredEmployees.value = if (query.isEmpty()) _employees.value
        else _employees.value.filter { emp ->
            emp.name.lowercase().contains(query) ||
            emp.position.lowercase().contains(query)
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.addEmployee(employee)
            _event.value = EmployeeEvent.ShowSnackbar("Сотрудник добавлен"); clearEvent()
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
            _event.value = EmployeeEvent.ShowSnackbar("Сотрудник обновлён"); clearEvent()
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
            _event.value = EmployeeEvent.ShowSnackbar("Сотрудник удалён"); clearEvent()
        }
    }

    suspend fun getEmployeeById(id: Int): Employee? = repository.getEmployeeById(id)

    private fun clearEvent() {
        viewModelScope.launch { kotlinx.coroutines.delay(2000); _event.value = null }
    }
}

sealed class EmployeeEvent {
    data class ShowSnackbar(val message: String) : EmployeeEvent()
}
```

---

## А.14 — DatabaseModule.kt (Hilt DI)

```kotlin
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

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InfoDatabase =
        InfoDatabase.getInstance(context)

    @Provides
    fun provideInfoDao(database: InfoDatabase): InfoDao = database.infoDao()

    @Provides
    fun provideEmployeeDao(database: InfoDatabase): EmployeeDao = database.employeeDao()

    @Provides
    fun provideFavoriteDao(database: InfoDatabase): FavoriteDao = database.favoriteDao()

    @Provides @Singleton
    fun provideInfoRepository(infoDao: InfoDao): InfoRepository = InfoRepository(infoDao)

    @Provides @Singleton
    fun provideEmployeeRepository(employeeDao: EmployeeDao): EmployeeRepository =
        EmployeeRepository(employeeDao)

    @Provides @Singleton
    fun provideFavoriteRepository(favoriteDao: FavoriteDao): FavoriteRepository =
        FavoriteRepository(favoriteDao)

    @Provides @Singleton
    fun provideDatabaseInitializer(database: InfoDatabase): DatabaseInitializer =
        DatabaseInitializer(database)
}
```

---

## А.15 — PriqnixApplication.kt

```kotlin
package com.example.priqnix

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PriqnixApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this).maxSizePercent(0.25).build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_cache"))
                    .maxSizeBytes(50 * 1024 * 1024)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
```

---

## А.16 — ThemeManager.kt

```kotlin
package com.example.priqnix.ui

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager(context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        prefs.edit().putBoolean("dark_theme", newValue).apply()
    }
}
```

---

## А.17 — DatabaseInitializer.kt

```kotlin
package com.example.priqnix.data

import com.example.priqnix.utils.PrefillData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class DatabaseInitializer @javax.inject.Inject constructor(private val database: InfoDatabase) {

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            val existing = database.employeeDao().getAllEmployees().firstOrNull()
            if (existing.isNullOrEmpty()) { insertEmployees() }
            listOf("Product", "Service", "Education").forEach { category ->
                if (database.infoDao().getCountByCategory(category) == 0) {
                    database.infoDao().insertAll(PrefillData.getDefaultItemsForCategory(category))
                }
            }
        }
    }

    private suspend fun insertEmployees() {
        val employees = listOf(
            Employee(name = "Андрей Никулкин", position = "Генеральный директор, основатель IQNIX",
                description = "...", photoUrl = null),
            // ... всего 25 сотрудников
        )
        employees.forEach { database.employeeDao().insertEmployee(it) }
    }
}
```

---

## А.18 — PrefillData.kt

```kotlin
package com.example.priqnix.utils

import com.example.priqnix.data.InfoItem

object PrefillData {
    fun getDefaultItemsForCategory(category: String): List<InfoItem> {
        return when (category) {
            "Product" -> listOf(
                InfoItem(title = "Справочно-информационная система «IQNIX Docs»",
                    description = "Мобильное приложение для Android с доступом к документации",
                    category = "Product", details = "Полнофункциональная ..."),
                // ... 12 продуктов
            )
            "Service" -> listOf(
                InfoItem(title = "Разработка ПО на заказ",
                    description = "Индивидуальные решения под ключ",
                    category = "Service", details = "Полный цикл ..."),
                // ... 12 услуг
            )
            "Education" -> listOf(
                InfoItem(title = "Курс «Kotlin для начинающих»",
                    description = "Основы языка программирования Kotlin",
                    category = "Education", details = "40 часов теории и практики..."),
                // ... 8 курсов
            )
            else -> emptyList()
        }
    }
}
```

---

## А.19 — FaqItem.kt

```kotlin
package com.example.priqnix.data

data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String,
    val category: String
)
```

---

## А.20 — FaqData.kt

```kotlin
package com.example.priqnix.utils

import com.example.priqnix.data.FaqItem

object FaqData {
    val items = listOf(
        FaqItem(1, "Какие услуги предоставляет IQNIX?",
            "IQNIX предоставляет полный спектр IT-услуг: разработка ПО, ...", "Услуги"),
        FaqItem(2, "Как заказать разработку ПО?",
            "Оставьте заявку на сайте или напишите на email dev@iqnix.tech...", "Услуги"),
        // ... 12 вопросов
    )
}
```

---

## А.21 — MainActivity.kt (точка входа)

```kotlin
package com.example.priqnix.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.priqnix.data.DatabaseInitializer
import com.example.priqnix.ui.screens.*
import com.example.priqnix.ui.theme.PriqnixTheme
import com.example.priqnix.viewmodel.EmployeesViewModel
import com.example.priqnix.viewmodel.FavoritesViewModel
import com.example.priqnix.viewmodel.InfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var databaseInitializer: DatabaseInitializer

    private val _isDbReady = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !_isDbReady.value }

        lifecycleScope.launch {
            databaseInitializer.initialize()
            _isDbReady.value = true
        }

        val themeManager = ThemeManager(applicationContext)

        setContent {
            val isDarkTheme by themeManager.isDarkTheme.collectAsStateWithLifecycle()
            PriqnixTheme(darkTheme = isDarkTheme) {
                if (_isDbReady.value) {
                    val infoViewModel: InfoViewModel = hiltViewModel()
                    val employeeViewModel: EmployeesViewModel = hiltViewModel()
                    val favoritesViewModel: FavoritesViewModel = hiltViewModel()
                    AppNavigation(infoViewModel, employeeViewModel, favoritesViewModel,
                        onThemeToggle = { themeManager.toggleTheme() })
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(infoViewModel: InfoViewModel, employeeViewModel: EmployeesViewModel,
                  favoritesViewModel: FavoritesViewModel, onThemeToggle: () -> Unit = {}) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("home", "Главная", Icons.Default.Home),
        BottomNavItem("products", "Продукты", Icons.Default.ShoppingCart),
        BottomNavItem("services", "Услуги", Icons.Default.Work),
        BottomNavItem("education", "Образование", Icons.Default.School),
        BottomNavItem("employees", "Сотрудники", Icons.Default.People),
        BottomNavItem("favorites", "Избранное", Icons.Default.Favorite),
        BottomNavItem("about", "О компании", Icons.Default.Info),
        BottomNavItem("faq", "FAQ", Icons.Default.QuestionAnswer),
        BottomNavItem("contacts", "Контакты", Icons.Default.Contacts)
    )

    val favoriteCount by favoritesViewModel.favoriteItems.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            if (item.route == "favorites") {
                                BadgedBox(badge = {
                                    if (favoriteCount.isNotEmpty()) {
                                        Badge { Text(favoriteCount.size.toString()) }
                                    }
                                }) { Icon(item.icon, contentDescription = item.title) }
                            } else {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable("home") { HomeScreen(navController = navController, onThemeToggle = onThemeToggle) }
            composable("about") { AboutScreen() }
            composable("products") {
                CategoryScreen(category = "Product", viewModel = infoViewModel,
                    navController = navController, favoritesViewModel = favoritesViewModel) }
            composable("services") {
                CategoryScreen(category = "Service", viewModel = infoViewModel,
                    navController = navController, favoritesViewModel = favoritesViewModel) }
            composable("education") {
                CategoryScreen(category = "Education", viewModel = infoViewModel,
                    navController = navController, favoritesViewModel = favoritesViewModel) }
            composable("faq") { FaqScreen() }
            composable("contacts") { ContactsScreen() }
            composable("detail/{itemId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
                InfoDetailScreen(itemId = id, viewModel = infoViewModel, navController = navController)
            }
            composable("favorites") { FavoritesScreen(navController = navController) }
            composable("employees") {
                EmployeesScreen(viewModel = employeeViewModel,
                    onEmployeeClick = { id -> navController.navigate("employee_detail/$id") })
            }
            composable("employee_detail/{employeeId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("employeeId")?.toIntOrNull() ?: 0
                EmployeeDetailScreen(employeeId = id, viewModel = employeeViewModel,
                    onBack = { navController.popBackStack() })
            }
        }
    }
}

data class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
```

---

## А.22 — HomeScreen.kt (главный экран)

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController? = null, onThemeToggle: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            TopMenuBar(navController = navController)
            BannerSection(onThemeToggle = onThemeToggle)
            CategoryPills()
            MissionSection()
            StatsSection()
            PartnersSection()
            AnalyticsDevelopmentSection()
            DirectionsSection()
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (scrollState.value > 200) {
            SmallFloatingActionButton(
                onClick = { coroutineScope.launch { scrollState.animateScrollTo(0) } },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Наверх")
            }
        }
    }
}

@Composable
fun TopMenuBar(navController: NavController? = null) {
    val currentRoute = navController?.let {
        it.currentBackStackEntryAsState().value?.destination?.route
    }
    val topItems = listOf(
        "home" to "Главная", "products" to "Продукты", "services" to "Услуги",
        "education" to "Образование", "employees" to "Сотрудники",
        "favorites" to "Избранное", "faq" to "FAQ", "contacts" to "Контакты",
        "about" to "О компании"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        topItems.forEach { (route, title) ->
            Text(
                text = title, fontSize = 13.sp,
                fontWeight = if (route == currentRoute) FontWeight.Bold else FontWeight.Normal,
                color = if (route == currentRoute) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable {
                    navController?.navigate(route) {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun BannerSection(onThemeToggle: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 24.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("IQNIX", fontSize = 54.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Разработка digital-решений для бизнеса и людей", fontSize = 22.sp,
                fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground)
        }
        IconButton(onClick = onThemeToggle, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.Default.DarkMode, contentDescription = "Сменить тему")
        }
    }
}

// --- CategoryPills, MissionSection, StatsSection, PartnersSection,
//     AnalyticsDevelopmentSection, DirectionsSection, ChipDirection ---
// (полный код в исходном файле на 344 строки)
```

---

## А.23 — CategoryListScreen.kt (список категории + InfoCard)

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.priqnix.data.FavoriteItem
import com.example.priqnix.data.InfoItem
import com.example.priqnix.ui.components.ListShimmer
import com.example.priqnix.viewmodel.FavoritesViewModel
import com.example.priqnix.viewmodel.InfoUiState
import com.example.priqnix.viewmodel.InfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    category: String, viewModel: InfoViewModel, navController: NavController,
    onAddClick: () -> Unit, favoritesViewModel: FavoritesViewModel? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery = viewModel.searchQuery
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by favoritesViewModel?.snackbarMessage?.collectAsStateWithLifecycle()
            ?: remember { mutableStateOf(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            favoritesViewModel?.clearSnackbar()
        }
    }

    LaunchedEffect(category) { viewModel.loadItems(category) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(when (category) {
                    "Product" -> "Наши продукты"; "Service" -> "Услуги"
                    "Education" -> "Образование"; else -> category
                })},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onAddClick) { Text("+") } }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    viewModel.searchQuery = query
                    viewModel.loadItems(category, query)
                },
                label = { Text("Поиск") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (val state = uiState) {
                is InfoUiState.Loading -> ListShimmer()
                is InfoUiState.Success -> {
                    if (state.items.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Нет записей. Нажмите + для добавления.")
                        }
                    } else {
                        LazyColumn {
                            items(state.items) { item ->
                                InfoCard(item = item, navController = navController,
                                    favoritesViewModel = favoritesViewModel)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                is InfoUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ошибка: ${state.message}")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(item: InfoItem, navController: NavController,
             favoritesViewModel: FavoritesViewModel? = null) {
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(item.id) {
        favoritesViewModel?.let { vm ->
            vm.isFavorite(item.id).collect { isFavorite = it }
        }
    }

    Card(
        onClick = { navController.navigate("detail/${item.id}") },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                Text(text = item.category, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.description, maxLines = 2)
            }
            if (favoritesViewModel != null) {
                IconButton(onClick = {
                    favoritesViewModel.toggleFavorite(
                        FavoriteItem(item.id, item.title, item.category))
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite
                                      else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Удалить из избранного"
                                             else "Добавить в избранное",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
```

---

## А.24 — AddItemDialog.kt

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddItemDialog(
    @Suppress("UNUSED_PARAMETER") category: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, details: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Добавить запись", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = title, onValueChange = { title = it; showErrors = false },
                    label = { Text("Название *") }, modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && title.isBlank())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description,
                    onValueChange = { description = it; showErrors = false },
                    label = { Text("Краткое описание *") }, modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && description.isBlank())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = details, onValueChange = { details = it },
                    label = { Text("Подробности") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            onConfirm(title, description, details); onDismiss()
                        } else { showErrors = true }
                    }) { Text("Сохранить") }
                }
            }
        }
    }
}
```

---

## А.25 — InfoDetailScreen.kt

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.priqnix.data.InfoItem
import com.example.priqnix.viewmodel.InfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDetailScreen(itemId: Int, viewModel: InfoViewModel, navController: NavController) {
    var item by remember { mutableStateOf<InfoItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(itemId) {
        viewModel.getItem(itemId) { result -> item = result; isLoading = false }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Детальная информация") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            })
    }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) { CircularProgressIndicator(modifier = Modifier.fillMaxSize()) }
            else if (item == null) { Text("Запись не найдена", modifier = Modifier.fillMaxSize().padding(16.dp)) }
            else {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item!!.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Категория: ${item!!.category}", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(item!!.description, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Подробнее:", style = MaterialTheme.typography.titleMedium)
                    Text(item!!.details)
                }
            }
        }
    }
}
```

---

## А.26 — EmployeesScreen.kt

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.priqnix.data.Employee
import com.example.priqnix.ui.components.ListShimmer
import com.example.priqnix.viewmodel.EmployeesViewModel
import com.example.priqnix.viewmodel.EmployeeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(viewModel: EmployeesViewModel, onEmployeeClick: (Int) -> Unit = {}) {
    val filteredEmployees by viewModel.filteredEmployees.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackbarEvent by viewModel.event.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarEvent) {
        if (snackbarEvent is EmployeeEvent.ShowSnackbar)
            snackbarHostState.showSnackbar((snackbarEvent as EmployeeEvent.ShowSnackbar).message)
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        topBar = { TopAppBar(title = { Text("Сотрудники") }) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по имени или должности") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true)
            if (isLoading) ListShimmer()
            else LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredEmployees, key = { it.id }) { employee ->
                    EmployeeCard(employee = employee, onClick = { onEmployeeClick(employee.id) })
                }
            }
        }
    }
}
```

---

## А.27 — EmployeeDetailScreen.kt

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.priqnix.data.Employee
import com.example.priqnix.viewmodel.EmployeesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(employeeId: Int, viewModel: EmployeesViewModel, onBack: () -> Unit) {
    var employee by remember { mutableStateOf<Employee?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(employeeId) {
        employee = viewModel.getEmployeeById(employeeId)
        isLoading = false
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(employee?.name ?: "Сотрудник") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            })
    }) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (employee == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text("Сотрудник не найден") }
        } else {
            val emp = employee!!
            Column(modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                if (emp.photoUrl != null) {
                    Image(painter = rememberAsyncImagePainter(emp.photoUrl),
                        contentDescription = null,
                        modifier = Modifier.size(140.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.height(20.dp))
                }
                Text(text = emp.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer) {
                    Text(text = emp.position,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("О сотруднике", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = emp.description, fontSize = 15.sp, lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
```

---

## А.28 — FavoritesScreen.kt

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.priqnix.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController,
                    viewModel: FavoritesViewModel = hiltViewModel()) {
    val items by viewModel.favoriteItems.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Избранное") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer))
    }) { padding ->
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Favorite, contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Нет избранного", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Нажмите ♡ на карточке товара или услуги", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { item ->
                    InfoCard(item = item, navController)
                }
            }
        }
    }
}
```

---

## А.29 — FaqScreen.kt

```kotlin
package com.example.priqnix.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.priqnix.data.FaqItem
import com.example.priqnix.utils.FaqData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var expandedIds by remember { mutableStateOf(setOf<Int>()) }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) FaqData.items
        else FaqData.items.filter {
            it.question.contains(searchQuery, ignoreCase = true) ||
            it.answer.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("FAQ") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer))
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(value = searchQuery,
                onValueChange = { searchQuery = it; expandedIds = emptySet() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по вопросам...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true)
            Spacer(modifier = Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ничего не найдено", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered, key = { it.id }) { faq ->
                        FaqCard(faq = faq, isExpanded = faq.id in expandedIds,
                            onToggle = {
                                expandedIds = if (faq.id in expandedIds) expandedIds - faq.id
                                              else expandedIds + faq.id
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun FaqCard(faq: FaqItem, isExpanded: Boolean, onToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().clickable { onToggle() }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = faq.question, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text(text = faq.category, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary)
                }
                Icon(imageVector = if (isExpanded) Icons.Default.ExpandLess
                                  else Icons.Default.ExpandMore,
                    contentDescription = null)
            }
            AnimatedVisibility(visible = isExpanded,
                enter = expandVertically(), exit = shrinkVertically()) {
                Text(text = faq.answer,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    fontSize = 14.sp, lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
```

---

## А.30 — ShimmerEffect.kt

```kotlin
package com.example.priqnix.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier, width: Dp = 200.dp,
                  height: Dp = 16.dp,
                  shape: RoundedCornerShape = RoundedCornerShape(4.dp)) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart),
        label = "shimmer")
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero, end = Offset(x = translateAnim, y = translateAnim))
    Box(modifier = modifier.width(width).height(height).clip(shape).background(brush))
}

@Composable
fun ListShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(6) { CardShimmer() }
    }
}

@Composable
fun CardShimmer(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surface)) {
        EmployeeCardShimmer()
    }
}

@Composable
fun EmployeeCardShimmer(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ShimmerEffect(width = 48.dp, height = 48.dp, shape = CircleShape)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerEffect(width = 180.dp, height = 16.dp)
            ShimmerEffect(width = 120.dp, height = 12.dp)
        }
    }
}
```

---

## А.31 — build.gradle (app)

```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
    id 'com.google.dagger.hilt.android'
}

android {
    namespace 'com.example.priqnix'
    compileSdk 34

    defaultConfig {
        applicationId 'com.example.priqnix'
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName '1.0'
    }

    signingConfigs {
        release {
            storeFile file('release.keystore')
            storePassword 'priqnix123'
            keyAlias 'priqnix'
            keyPassword 'priqnix123'
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    buildFeatures { compose true }
    composeOptions { kotlinCompilerExtensionVersion '1.5.10' }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = '17' }
    kapt { correctErrorTypes true }
}

dependencies {
    implementation platform(libs.androidx.compose.bom)
    implementation libs.androidx.core.ktx
    implementation libs.androidx.lifecycle.runtime.ktx
    implementation libs.androidx.lifecycle.viewmodel.compose
    implementation libs.androidx.lifecycle.runtime.compose
    implementation libs.androidx.activity.compose
    implementation libs.androidx.compose.ui
    implementation libs.androidx.compose.ui.graphics
    implementation libs.androidx.compose.ui.tooling.preview
    implementation libs.androidx.compose.material3
    implementation libs.androidx.compose.material.icons
    implementation libs.androidx.navigation.compose
    implementation libs.androidx.room.runtime
    implementation libs.androidx.room.ktx
    kapt libs.androidx.room.compiler
    implementation libs.androidx.core.splashscreen
    implementation libs.hilt.android
    kapt libs.hilt.compiler
    implementation libs.hilt.navigation.compose
    implementation libs.coil.compose
    implementation libs.coroutines.android
    testImplementation libs.junit
    androidTestImplementation libs.androidx.test.junit
    androidTestImplementation libs.androidx.test.espresso.core
    debugImplementation libs.androidx.compose.ui.tooling
}
```

---

## А.32 — libs.versions.toml (version catalog)

```toml
[versions]
agp = "8.2.0"
kotlin = "1.9.22"
composeBom = "2024.04.01"
activityCompose = "1.8.2"
lifecycleRuntimeKtx = "2.7.0"
navigationCompose = "2.7.7"
room = "2.6.1"
hilt = "2.50"
hiltNavigationCompose = "1.1.0"
coil = "2.7.0"
coreSplashscreen = "1.0.1"
coroutines = "1.7.3"
composeCompiler = "1.5.10"
composeMaterialIcons = "1.6.1"
junit = "4.13.2"
androidxTestJunit = "1.1.5"
espressoCore = "3.5.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version = "1.12.0" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version = "2.7.0" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version = "2.8.7" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons = { group = "androidx.compose.material", name = "material-icons-extended", version.ref = "composeMaterialIcons" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version.ref = "coreSplashscreen" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-test-junit = { group = "androidx.test.ext", name = "junit", version.ref = "androidxTestJunit" }
androidx-test-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

---

## А.33 — .github/workflows/android.yml (CI/CD)

```yaml
name: Android CI

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      - name: Build with Gradle
        run: ./gradlew assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```
