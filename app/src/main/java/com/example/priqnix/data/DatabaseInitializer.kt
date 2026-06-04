package com.example.priqnix.data

import com.example.priqnix.utils.PrefillData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class DatabaseInitializer @javax.inject.Inject constructor(private val database: InfoDatabase) {

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            val existing = database.employeeDao().getAllEmployees().firstOrNull()
            if (existing.isNullOrEmpty()) {
                insertEmployees()
            }
            val categories = listOf("Product", "Service", "Education")
            categories.forEach { category ->
                if (database.infoDao().getCountByCategory(category) == 0) {
                    database.infoDao().insertAll(PrefillData.getDefaultItemsForCategory(category))
                }
            }
        }
    }

    private suspend fun insertEmployees() {
        val employees = listOf(
            Employee(name = "Андрей Никулкин", position = "Генеральный директор, основатель IQNIX", description = "Эксперт клуба хакатонщиков ФКН НИУ ВШЭ\nEx-Technology Solution Professional Microsoft Russia\nВыпускник Института сервисных технологий ФГБОУ ВО РГУТИС (СПО 2019)\nВыпускник АНО ВО ИДК (ВО 2024)\nОбучающийся по программе магистратуры Университет \"Синергия\"\nПрофессорско-преподавательский состав IT-кафедр\n\nEmail: ceo@iqnix.tech\nМоб.: +7 (929) 959-74-77\nТел.: +7 (495) 157-77-74 (доб. 777)", photoUrl = null),
            Employee(name = "Кристина Никулкина", position = "Финансовое планирование и управление бухгалтерией", description = "Российский экономический университет имени Г.В. Плеханова\n\nEmail: nikulkina@iqnix.tech", photoUrl = null),
            Employee(name = "Даниил Мироненко", position = "Ассистент Генерального директора", description = "Ex-сотрудник Федеральной службы государственной регистрации, кадастру и картографии\nEx-секретарь государственной гражданской службы РФ 2 класса\n\nEmail: ask@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Ксения Ахальцева", position = "Советник по аккредитации, лицензированию и госрегистрации", description = "Генеральный директор, учредитель ООО \"КК\" (Консалтинг Контакт)\n\nEmail: ksenia@konsalting-k.ru\nТел.: +7 (989) 118-00-90", photoUrl = null),
            Employee(name = "Владимир Клочко", position = "Руководитель методического отдела", description = "Email: edu@iqnix.tech\nТел.: +7 (929) 959-74-77", photoUrl = null),
            Employee(name = "Максим Ефремов", position = "Руководитель проектов в сфере образования", description = "Куратор базовых кафедр компании, образовательных программ ДПО\n\nEmail: edu@iqnix.tech\nТел.: +7 (495) 157-77-74 (доб. 501)", photoUrl = null),
            Employee(name = "Максим Чумаков", position = "Python разработчик, Backend Django / Unfold", description = "Email: dev@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Эдуард Романченко", position = "Внештатный разработчик", description = "Русификация open-source сервисов\n\nEmail: dev@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Алексей Спиридонов", position = "Backend разработчик (Django, Python)", description = "Email: dev@iqnix.tech", photoUrl = null),
            Employee(name = "Андрей Шереметьев", position = "Оператор аутсорсинговых проектов, техподдержка", description = "Email: help@iqnix.tech / dev@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Кирилл Кофейников", position = "Специалист по ИИ (LLM)", description = "Разработка LLM моделей\n\nEmail: dev@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Дмитрий Гончаренко", position = "Разработчик ПО, Томский филиал", description = "Email: dev@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Даниил Кручинский", position = "Разработчик ПО, Томский филиал", description = "Email: dev@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Дамир Мухомедзянов", position = "Разработчик ПО, Томский филиал", description = "Email: dev@iqnix.tech\nТел.: +7 (495) 157-77-74", photoUrl = null),
            Employee(name = "Елена Воробьёва", position = "HR-директор", description = "Управление персоналом, подбор и адаптация сотрудников\n\nEmail: hr@iqnix.tech\nТел.: +7 (495) 157-77-74 (доб. 102)", photoUrl = null),
            Employee(name = "Сергей Ковалёв", position = "Руководитель отдела продаж", description = "B2B-продажи, работа с ключевыми клиентами\n\nEmail: sales@iqnix.tech\nТел.: +7 (495) 157-77-74 (доб. 201)", photoUrl = null),
            Employee(name = "Анна Смирнова", position = "UI/UX-дизайнер", description = "Дизайн интерфейсов, прототипирование, дизайн-системы\n\nEmail: design@iqnix.tech", photoUrl = null),
            Employee(name = "Иван Петров", position = "Frontend-разработчик (React, TypeScript)", description = "SPA, PWA, микрофронтенды\n\nEmail: dev@iqnix.tech", photoUrl = null),
            Employee(name = "Ольга Новикова", position = "Тестировщик ПО (QA Engineer)", description = "Автоматизированное и ручное тестирование\n\nEmail: qa@iqnix.tech", photoUrl = null),
            Employee(name = "Николай Соколов", position = "Системный администратор", description = "Администрирование Linux-серверов, сетевая инфраструктура\n\nEmail: sysadmin@iqnix.tech\nТел.: +7 (495) 157-77-74 (доб. 301)", photoUrl = null),
            Employee(name = "Татьяна Морозова", position = "Маркетолог", description = "Digital-маркетинг, SMM, контент-стратегия\n\nEmail: marketing@iqnix.tech", photoUrl = null),
            Employee(name = "Денис Волков", position = "Data Scientist", description = "Машинное обучение, NLP, компьютерное зрение\n\nEmail: ml@iqnix.tech", photoUrl = null),
            Employee(name = "Мария Кузнецова", position = "Аналитик", description = "Бизнес-анализ, системный анализ, написание ТЗ\n\nEmail: analytics@iqnix.tech", photoUrl = null),
            Employee(name = "Артём Белов", position = "iOS-разработчик (Swift, SwiftUI)", description = "Разработка мобильных приложений под iOS\n\nEmail: dev@iqnix.tech", photoUrl = null),
            Employee(name = "Виктория Зайцева", position = "Юрисконсульт", description = "Договорное право, интеллектуальная собственность\n\nEmail: legal@iqnix.tech", photoUrl = null)
        )
        employees.forEach { database.employeeDao().insertEmployee(it) }
    }
}