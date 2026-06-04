package com.example.priqnix.utils

import com.example.priqnix.data.InfoItem

object PrefillData {
    fun getDefaultItemsForCategory(category: String): List<InfoItem> {
        return when (category) {
            "Product" -> listOf(
                InfoItem(title = "Справочно-информационная система «IQNIX Docs»", description = "Мобильное приложение для Android с доступом к документации", category = "Product", details = "Полнофункциональная мобильная платформа для ведения документации организаций. Включает модули: документооборот, база знаний, интеграция с 1С. Резидент реестра отечественного ПО Минцифры РФ."),
                InfoItem(title = "Облачная платформа IQNIX Cloud", description = "Хранение и обработка данных в защищённом контуре", category = "Product", details = "Масштабируемое облачное решение на базе OpenStack. Виртуальные серверы, S3-хранилище, managed Kubernetes. Соответствует 152-ФЗ."),
                InfoItem(title = "Система аналитики IQNIX Insight", description = "BI-платформа для бизнес-аналитики", category = "Product", details = "Сбор и визуализация данных из любых источников. Дашборды, отчёты, машинное обучение. Интеграция с CRM, ERP, 1С."),
                InfoItem(title = "Платформа управления задачами IQNIX Track", description = "Корпоративный трекер задач и проектов", category = "Product", details = "Agile/Scrum доски, диаграммы Ганта, тайм-трекинг, отчёты по эффективности. Мобильное приложение для iOS и Android."),
                InfoItem(title = "Чат-бот платформа IQNIX Bot", description = "Конструктор чат-ботов для бизнеса", category = "Product", details = "Визуальный конструктор ботов для Telegram, VK, WhatsApp. NLP на базе LLM, интеграция с 1С и CRM. Поддержка GPT."),
                InfoItem(title = "IQNIX HR", description = "Система управления персоналом", category = "Product", details = "HRM-система: подбор, адаптация, обучение, оценка сотрудников. Электронный кадровый документооборот. Интеграция с 1С ЗУП."),
                InfoItem(title = "IQNIX CRM", description = "Система управления взаимоотношениями с клиентами", category = "Product", details = "Воронка продаж, скоринг лидов, автозвонки, email-рассылки. Интеграция с телефонией и мессенджерами."),
                InfoItem(title = "IQNIX Billing", description = "Платформа для управления подписками и платежами", category = "Product", details = "Выставление счетов, автоплатежи, интеграция с эквайрингом, мультивалютность. Отчёты по выручке и задолженностям."),
                InfoItem(title = "IQNIX Safe", description = "Корпоративный DLP-монитор", category = "Product", details = "Защита от утечек данных. Контроль съёмных носителей, перехват трафика, анализ поведения сотрудников. Сертифицировано ФСТЭК."),
                InfoItem(title = "IQNIX Meet", description = "Корпоративная платформа видеоконференций", category = "Product", details = "HD-видео, демонстрация экрана, запись встреч, транскрибация. On-premise развёртывание. Шифрование end-to-end."),
                InfoItem(title = "Маркетплейс IQNIX Store", description = "Платформа для цифровых товаров и услуг", category = "Product", details = "Готовый маркетплейс для продажи курсов, шаблонов, лицензий. Встроенный платёжный шлюз и система лояльности."),
                InfoItem(title = "IQNIX IoT Hub", description = "Платформа интернета вещей", category = "Product", details = "Сбор и обработка телеметрии с устройств. MQTT-брокер, визуализация, сценарии автоматизации. Поддержка LoRaWAN и NB-IoT.")
            )
            "Service" -> listOf(
                InfoItem(title = "Разработка ПО на заказ", description = "Индивидуальные решения под ключ", category = "Service", details = "Полный цикл разработки от анализа требований до сопровождения. Стек: Kotlin, Java, Python, C#, Go. Аутстаффинг и выделенные команды."),
                InfoItem(title = "IT-консалтинг", description = "Аудит и оптимизация ИТ-инфраструктуры", category = "Service", details = "Аудит архитектуры, оптимизация затрат на ИТ, миграция в облако, импортозамещение. Более 50 успешных проектов."),
                InfoItem(title = "Разработка мобильных приложений", description = "Нативные и кроссплатформенные приложения", category = "Service", details = "Android (Kotlin, Jetpack Compose), iOS (SwiftUI), Flutter, React Native. Полный цикл: дизайн, разработка, публикация в магазинах."),
                InfoItem(title = "Внедрение 1С", description = "Автоматизация учёта и управления", category = "Service", details = "Внедрение, доработка и сопровождение 1С:Бухгалтерия, 1С:УНФ, 1С:ERP, 1С:ЗУП. Переход с иностранных систем."),
                InfoItem(title = "Интеграция систем", description = "Связываем любые системы через API", category = "Service", details = "Разработка API-шлюзов, ESB-шина, интеграция CRM+ERP+1С+сайт. RabbitMQ, Kafka, REST, GraphQL, gRPC."),
                InfoItem(title = "Кибербезопасность", description = "Защита информации и аудит безопасности", category = "Service", details = "Пентест, анализ уязвимостей, внедрение СКУД, DLP-систем, SIEM. Лицензия ФСТЭК. Соответствие 152-ФЗ и GDPR."),
                InfoItem(title = "DevOps и CI/CD", description = "Автоматизация разработки и развёртывания", category = "Service", details = "Docker, Kubernetes, GitLab CI/CD, Jenkins, Terraform. Построение инфраструктуры в Yandex.Cloud, AWS, Azure. Мониторинг (Prometheus, Grafana)."),
                InfoItem(title = "Техническая поддержка", description = "Аутсорсинг ИТ-поддержки 24/7", category = "Service", details = "Service Desk, мониторинг инфраструктуры, поддержка пользователей. SLA от 1 часа. Удалённая и выездная поддержка."),
                InfoItem(title = "UI/UX-дизайн", description = "Проектирование пользовательского опыта", category = "Service", details = "Исследования, прототипирование, дизайн-системы. Figma, Sketch, Adobe XD. Юзабилити-тестирование. Сайд-проекты и корпоративные системы."),
                InfoItem(title = "Разработка Telegram-ботов", description = "Автоматизация коммуникаций в Telegram", category = "Service", details = "Боты для продаж, поддержки, уведомлений. Интеграция с CRM, платежами, AI. Aiogram, python-telegram-bot."),
                InfoItem(title = "Анализ данных и ML", description = "Data Science и машинное обучение", category = "Service", details = "Прогнозные модели, кластеризация, NLP, компьютерное зрение. Python, TensorFlow, PyTorch. Развёртывание моделей в продакшн."),
                InfoItem(title = "UI/UX-аудит", description = "Экспертиза цифровых продуктов", category = "Service", details = "Эвристическая оценка, тепловые карты, A/B-тестирование. Отчёт с рекомендациями по улучшению конверсии и пользовательского опыта.")
            )
            "Education" -> listOf(
                InfoItem(title = "Курс «Kotlin для начинающих»", description = "Основы языка программирования Kotlin", category = "Education", details = "40 часов теории и практики. Темы: синтаксис, ООП, корутины, Flow. Финальный проект — консольное приложение."),
                InfoItem(title = "Android-разработка с нуля", description = "Создание мобильных приложений под Android", category = "Education", details = "Интенсив 72 часа. Jetpack Compose, Room, Navigation, MVVM. Проект в портфолио — полноценное приложение."),
                InfoItem(title = "Курс «Python для анализа данных»", description = "Pandas, NumPy, Matplotlib", category = "Education", details = "48 часов практики. Обработка данных, визуализация, статистика. Финальный проект — анализ реального датасета."),
                InfoItem(title = "Fullstack-разработка на Django", description = "Backend и frontend на Django + DRF", category = "Education", details = "64 часа. Модели, REST API, аутентификация, тестирование, деплой. Проект — интернет-магазин."),
                InfoItem(title = "Курс по машинному обучению", description = "Scikit-learn, нейросети, NLP", category = "Education", details = "56 часов. Линейные модели, деревья решений, нейронные сети. Финальный хакатон с реальной бизнес-задачей."),
                InfoItem(title = "DevOps-инженер: с нуля до PRO", description = "Docker, Kubernetes, CI/CD", category = "Education", details = "80 часов. Docker, K8s, Terraform, Ansible, GitLab CI. Лабораторные работы на реальных серверах."),
                InfoItem(title = "Курс по кибербезопасности", description = "Основы защиты информации", category = "Education", details = "40 часов. Сетевая безопасность, криптография, пентест, соответствие 152-ФЗ. Практика на виртуальных стендах."),
                InfoItem(title = "1С: Программирование и конфигурирование", description = "Разработка на платформе 1С", category = "Education", details = "72 часа. Язык 1С, запросы, отчёты, обмен данными. Подготовка к экзамену 1С:Специалист.")
            )
            else -> emptyList()
        }
    }
}