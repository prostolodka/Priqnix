# Changelog — 2026-06-04

## Исправленные ошибки

### 1. PrefillData.kt не использовался
- **Файл**: `app/src/main/java/com/example/priqnix/data/DatabaseInitializer.kt`
- **Проблема**: DatabaseInitializer заполнял только сотрудников, но не заполнял товары, услуги и курсы. Объект `PrefillData` был полностью мёртвым кодом.
- **Решение**: DatabaseInitializer теперь вызывает `PrefillData.getDefaultItemsForCategory()` для категорий Product, Service, Education при первом запуске.

### 2. Дублирование кода в MainActivity.kt
- **Файл**: `app/src/main/java/com/example/priqnix/ui/MainActivity.kt`
- **Проблема**: Три одинаковых блока для composable("products"), composable("services"), composable("education") с полным дублированием AddItemDialog.
- **Решение**: Вынесена общая функция `CategoryScreen()`, которая принимает категорию как параметр.

### 3. Отсутствие android:theme в манифесте
- **Файл**: `app/src/main/AndroidManifest.xml`
- **Проблема**: У activity не было `android:theme`, что могло вызвать crash на старых версиях Android.
- **Решение**: Добавлен `android:theme="@style/Theme.Priqnix"`.

### 4. Неправильный путь JAVA_HOME в gradle.properties
- **Файл**: `gradle.properties`
- **Проблема**: `org.gradle.java.home` содержал `java` (строчные) вместо `Java` (с прописной).
- **Решение**: `C:\\Program Files\\java\\...` -> `C:\\Program Files\\Java\\...`

### 5. InfoDao возвращал suspend-функции вместо Flow
- **Файл**: `app/src/main/java/com/example/priqnix/data/InfoDao.kt`
- **Проблема**: `getByCategory()` и `searchInCategory()` были `suspend fun` возвращающие `List`, из-за чего репозиторий оборачивал их в `flow { emit() }` — одноразовая эмиссия без реактивности.
- **Решение**: Методы теперь возвращают `Flow<List<InfoItem>>` (Room сам следит за изменениями таблицы).

### 6. InfoRepository имел suspend-обёртки для Flow
- **Файл**: `app/src/main/java/com/example/priqnix/repository/InfoRepository.kt`
- **Проблема**: `getByCategory()` и `searchInCategory()` были `suspend` и создавали `flow { emit() }`.
- **Решение**: Методы не-suspend, просто делегируют DAO.

---

## Улучшения и новые данные

### Сотрудники (было 14, стало 25)
- **Файл**: `DatabaseInitializer.kt`
- Добавлены: Елена Воробьёва (HR-директор), Сергей Ковалёв (Руководитель отдела продаж), Анна Смирнова (UI/UX-дизайнер), Иван Петров (Frontend), Ольга Новикова (QA), Николай Соколов (Системный администратор), Татьяна Морозова (Маркетолог), Денис Волков (Data Scientist), Мария Кузнецова (Аналитик), Артём Белов (iOS-разработчик), Виктория Зайцева (Юрисконсульт).

### Продукты (было 2, стало 12)
- **Файл**: `PrefillData.kt`
- IQNIX Docs, IQNIX Cloud, IQNIX Insight, IQNIX Track, IQNIX Bot, IQNIX HR, IQNIX CRM, IQNIX Billing, IQNIX Safe, IQNIX Meet, IQNIX Store, IQNIX IoT Hub.

### Услуги (было 2, стало 12)
- **Файл**: `PrefillData.kt`
- Разработка ПО, IT-консалтинг, Мобильная разработка, Внедрение 1С, Интеграция систем, Кибербезопасность, DevOps, Техподдержка, UI/UX-дизайн, Telegram-боты, Data Science, UI/UX-аудит.

### Образование (было 2, стало 8)
- **Файл**: `PrefillData.kt`
- Kotlin для начинающих, Android-разработка, Python для анализа данных, Fullstack на Django, Машинное обучение, DevOps-инженер, Кибербезопасность, 1С программирование.

### 7. Deprecated ArrowBack (warning)
- **Файл**: `app/src/main/java/com/example/priqnix/ui/screens/InfoDetailScreen.kt`
- **Проблема**: `Icons.Default.ArrowBack` deprecated в Material 3.
- **Решение**: Заменён на `Icons.AutoMirrored.Filled.ArrowBack`.

### 8. Неиспользуемый параметр category в AddItemDialog (warning)
- **Файл**: `app/src/main/java/com/example/priqnix/ui/screens/AddItemDialog.kt`
- **Решение**: Добавлен `@Suppress("UNUSED_PARAMETER")`.

---

## Финальная сборка

- `gradlew assembleDebug` — **BUILD SUCCESSFUL в 5s, 0 warnings**
- `gradlew clean assembleDebug` — **BUILD SUCCESSFUL в 21s** (полная пересборка)
- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Размер APK: (см. файл)

- **Команда**: `gradlew assembleDebug`
- **Результат**: BUILD SUCCESSFUL (2s)
- **APK**: `app/build/outputs/apk/debug/app-debug.apk`
