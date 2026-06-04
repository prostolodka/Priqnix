# CHANGELOG — IQNIX Priqnix

Полная история изменений проекта за 4–5 июня 2026.

---

## [2026-06-05] — Час 3

### UI/UX
- **Карточки партнёров** — `surfaceVariant` → `secondaryContainer` с `tonalElevation` (видно в обеих темах)
- **Плитки 3D/1С** на главной — `surfaceVariant` → `tertiaryContainer` (контраст в светлой и тёмной темах)
- **Кнопка «Наверх»** — `SmallFloatingActionButton` с иконкой `KeyboardArrowUp` появляется при скролле > 200px, плавно скроллит к верху
- **Badge-счётчик избранного** — на иконке «Избранное» в нижней навигации отображается количество сохранённых элементов
- **Snackbar избранного** — при нажатии ♡ всплывает «Добавлено в избранное» / «Удалено из избранного»

### Исправления
- **AboutScreen** — удалена кнопка смены темы, роут добавлен в нижнюю навигацию (9 пунктов в меню)
- **TopMenuBar (главная)** — добавлен пункт "О компании", совпадает с нижним меню

---

## [2026-06-05] — Час 2

### Новые фичи
- **FAQ** — экран `FaqScreen.kt` с раскрывающимися карточками, поиском, категориями (12 вопросов)
  - Данные: `utils/FaqData.kt` — статический список
  - Анимация: `AnimatedVisibility` для expand/collapse
- **Избранное (Favorites)** — полноценная система:
  - Room-таблица `favorites` (entity: `FavoriteItem.kt`, dao: `FavoriteDao.kt`)
  - `FavoriteRepository.kt` — CRUD операции
  - `FavoritesViewModel.kt` (+ `FavoritesScreen.kt`) — список избранного
  - Кнопка ♡ / ♥ на каждой `InfoCard` с `Flow<Boolean>` отслеживанием состояния
  - Поддержка Hilt (`@HiltViewModel`)
- **CI/CD** — `.github/workflows/android.yml`:
  - сборка на push/PR в main
  - загрузка debug APK как artifact

### Исправления (по замечаниям)
1. **Room миграция** — `fallbackToDestructiveMigration()` заменён на `MIGRATION_1_2` + `MIGRATION_2_3`:
   - `MIGRATION_1_2`: создание таблицы `employees` (схема v1 → v2)
   - `MIGRATION_2_3`: создание таблицы `favorites` (v2 → v3)
   - Импорт: `androidx.room.migration.Migration`, `androidx.sqlite.db.SupportSQLiteDatabase`
2. **`.gitignore`** — добавлен `app/release.keystore`, файл удалён из git-трекинга
3. **AboutScreen** — убрана кнопка темы
4. **HomeScreen цвета** — все `Color.White` заменены на `MaterialTheme.colorScheme.onBackground`/`onSurface`
5. **Поиск** — `InfoViewModel` использует `SavedStateHandle` для `searchQuery` (переживает поворот экрана)
6. **AddItemDialog** — валидация с красной подсветкой пустых полей + звёздочка на обязательных полях
7. **Загрузка БД** — `splashScreen.setKeepOnScreenCondition` теперь ждёт `_isDbReady`, показан `CircularProgressIndicator` пока БД инициализируется
8. **Навигация** — TopMenuBar на главной кликабельный, совпадает с нижним меню (добавлен "О компании")

### Инфраструктура
- **Version Catalog** — `gradle/libs.versions.toml` полностью переписан
- **app/build.gradle** — все зависимости переписаны на `alias(libs.*)`
- **root build.gradle** — переписан на плагины из каталога
- **compose-bom** обновлён: `2024.02.00` → `2024.04.01` (Material3 1.2.x)
- **Coil disk cache** — `PriqnixApplication.kt`: `ImageLoaderFactory` с 50MB кэшем и crossfade

---

## [2026-06-05] — Час 1

### Сборка
- `assembleDebug` — **BUILD SUCCESSFUL** (после обновления BOM и Version Catalog)

---

## [2026-06-04] — Час 3

### Hilt Dependency Injection
- **Плагин**: `com.google.dagger.hilt.android` (v2.50)
- **Новый файл**: `di/DatabaseModule.kt` — `@Module @InstallIn(SingletonComponent::class)`, предоставляет:
  - `InfoDatabase` (синглтон, Room)
  - `InfoDao`, `EmployeeDao`, `FavoriteDao`
  - `InfoRepository`, `EmployeeRepository`, `FavoriteRepository`
  - `DatabaseInitializer`
- **Новый файл**: `PriqnixApplication.kt` — `@HiltAndroidApp`
- **Обновлено**: `MainActivity.kt` — `@AndroidEntryPoint`, поля `@Inject`
- **Обновлено**: `InfoViewModel`, `EmployeesViewModel` — `@HiltViewModel`, `@Inject constructor`
- **Удалены**: `InfoViewModelFactory.kt`, `EmployeesViewModelFactory.kt`
- **Обновлён**: `DatabaseInitializer` — теперь использует `@Inject constructor` для получения DAO

### Переключатель темы
- **PriqnixTheme.kt** — параметр `darkTheme: Boolean` вместо `= true`
- **MainActivity.kt** — `remember { mutableStateOf(true) }`
- **AboutScreen.kt** — кнопка `DarkMode` в тулбаре

### Релизная подпись
- **Создан**: `app/release.keystore` (keyAlias: priqnix)
- **app/build.gradle** — добавлен `signingConfigs.release` + `buildTypes.release`

### Splash Screen (Android 12+)
- **Зависимость**: `core-splashscreen:1.0.1`
- **themes.xml** — добавлен `Theme.Priqnix.Splash`
- **AndroidManifest.xml** — `android:theme="@style/Theme.Priqnix.Splash"`
- **MainActivity.kt** — `installSplashScreen()` + `setKeepOnScreenCondition`

### Сборка
- `assembleDebug` — **BUILD SUCCESSFUL** (43s)
- `assembleRelease` — **BUILD SUCCESSFUL** (2m 18s)

---

## [2026-06-04] — Час 2

### Детальная карточка сотрудника
- **Новый файл**: `ui/screens/EmployeeDetailScreen.kt`
  - Аватар (круглый), ФИО, должность в чипе, описание
  - Навигация: `employee_detail/{employeeId}` → `popBackStack()`

### Поиск по сотрудникам
- **EmployeesViewModel.kt** — `updateSearchQuery()` + `filteredEmployees` StateFlow
- **EmployeesScreen.kt** — строка поиска с фильтрацией по имени и должности

### Snackbar вместо Log
- **EmployeesScreen.kt** — события теперь через `SnackbarHost` (добавлен/удалён/обновлён)

### Сборка
- `assembleDebug` — **BUILD SUCCESSFUL** (20s, 0 warnings)

---

## [2026-06-04] — Час 1 (базовые исправления)

### 1. PrefillData.kt не использовался
- **Файл**: `data/DatabaseInitializer.kt`
- **Было**: заполнял только сотрудников
- **Стало**: вызывает `PrefillData.getDefaultItemsForCategory()` для Product, Service, Education

### 2. Дублирование кода в MainActivity.kt
- **Было**: 3 одинаковых блока в NavHost (products/services/education)
- **Стало**: вынесена общая функция `CategoryScreen(category, ...)`

### 3. Отсутствие android:theme в манифесте
- **Файл**: `AndroidManifest.xml`
- **Решение**: добавлен `android:theme="@style/Theme.Priqnix"`

### 4. JAVA_HOME в gradle.properties
- **Файл**: `gradle.properties`
- **Было**: `C:\\Program Files\\java\\...`
- **Стало**: `C:\\Program Files\\Java\\...`

### 5. InfoDao — suspend вместо Flow
- **Файл**: `data/InfoDao.kt`
- **Было**: `suspend fun getByCategory(): List<InfoItem>` — одноразовая загрузка
- **Стало**: `fun getByCategory(): Flow<List<InfoItem>>` — реактивный, Room сам обновляет

### 6. InfoRepository — лишние suspend-обёртки
- **Файл**: `repository/InfoRepository.kt`
- **Было**: `suspend fun getByCategory(): Flow<List<InfoItem>>` с `flow { emit(repository.getByCategory()) }`
- **Стало**: обычные функции, просто делегируют DAO

### 7. ArrowBack deprecated
- **Файл**: `ui/screens/InfoDetailScreen.kt`
- **Решение**: `Icons.Default.ArrowBack` → `Icons.AutoMirrored.Filled.ArrowBack`

### 8. Неиспользуемый параметр category
- **Файл**: `ui/screens/AddItemDialog.kt`
- **Решение**: `@Suppress("UNUSED_PARAMETER")`

### Наполнение данными
- **Сотрудники**: 14 → 25 (добавлены HR, Sales, UI/UX, Frontend, QA, SysAdmin, Marketing, Data Science, iOS, Legal)
- **Продукты**: 2 → 12 (Docs, Cloud, Insight, Track, Bot, HR, CRM, Billing, Safe, Meet, Store, IoT Hub)
- **Услуги**: 2 → 12 (Dev, Consulting, Mobile, 1С, Integration, Security, DevOps, Support, Design, Bots, Data Science, Audit)
- **Образование**: 2 → 8 (Kotlin, Android, Python, Django, ML, DevOps, Security, 1С)
- **Источник**: `utils/PrefillData.kt`

### Финальная сборка
- ✅ `assembleDebug` — BUILD SUCCESSFUL
- ✅ `testDebugUnitTest` — BUILD SUCCESSFUL
- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Repo: https://github.com/prostolodka/Priqnix
