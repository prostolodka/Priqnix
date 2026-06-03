package com.example.priqnix.utils

import com.example.priqnix.data.InfoItem

object PrefillData {
    fun getDefaultItemsForCategory(category: String): List<InfoItem> {
        return when (category) {
            "Product" -> listOf(
                InfoItem(title = "Справочно-информационная система", description = "Мобильное приложение для Android", category = "Product", details = "Полное описание продукта..."),
                InfoItem(title = "Облачная платформа PRIQNIX Cloud", description = "Хранение и обработка данных", category = "Product", details = "Масштабируемое решение для бизнеса")
            )
            "Service" -> listOf(
                InfoItem(title = "Разработка ПО на заказ", description = "Индивидуальные решения", category = "Service", details = "Полный цикл разработки"),
                InfoItem(title = "IT-консалтинг", description = "Аудит и оптимизация", category = "Service", details = "Помощь в цифровой трансформации")
            )
            "Education" -> listOf(
                InfoItem(title = "Курс Kotlin для начинающих", description = "Основы языка", category = "Education", details = "40 часов теории и практики"),
                InfoItem(title = "Android-разработка с нуля", description = "Создание приложений", category = "Education", details = "Интенсив с проектом в портфолио")
            )
            else -> emptyList()
        }
    }
}