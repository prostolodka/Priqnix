package com.example.priqnix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        TopMenuBar()
        BannerSection()
        CategoryPills()
        MissionSection()
        StatsSection()
        PartnersSection()
        AnalyticsDevelopmentSection()
        DirectionsSection()
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TopMenuBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A24))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val items = listOf(
            "Главная", "О компании", "Наши продукты", "Услуги", "Образование", "Контакты", "Оставить заявку"
        )
        items.forEach { item ->
            Text(
                text = item,
                fontSize = 14.sp,
                fontWeight = if (item == "Главная") FontWeight.Bold else FontWeight.Normal,
                color = if (item == "Главная") MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun BannerSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "IQNIX",
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 4.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Разработка digital-решений для бизнеса и людей",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Composable
fun CategoryPills() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf("Плагин", "Разработка", "Аналитика").forEach { label ->
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MissionSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Мы работаем для вас",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Помогаем компаниям и их клиентам понять друг друга",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            listOf(
                "аккредитованная IT-компания",
                "издатель программных продуктов из реестра отечественного ПО Минцифры РФ",
                "поставщик IT-услуг Федерального уровня"
            ).forEach { point ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(point, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("47", "завершенных проектов")
        StatItem("30 дн", "средний срок сдачи")
        StatItem(">3,5 лет", "работаем в IT сфере")
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}

@Composable
fun PartnersSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text("Партнёры", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val partners = listOf(
                "ПЗСК", "РОСАВИЛИЯ", "ООО ПК \"Робокинетика\"",
                "АО Новосибирскэнергосбыт", "АО Русская кабельная компания"
            )
            items(partners) { partner ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A2A35),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3A45))
                ) {
                    Text(
                        text = partner,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsDevelopmentSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text("Аналитика", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Разработка", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("3D", "1С").forEach { label ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2A2A35),
                    modifier = Modifier.size(80.dp, 80.dp)
                ) {
                    androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                        Text(label, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DirectionsSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text("Направления работы", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Мы постоянно развиваемся и масштабируемся, благодаря чему можем открывать новые",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChipDirection("Разработка ПО")
            ChipDirection("Консалтинг")
            ChipDirection("Образование")
        }
    }
}

@Composable
fun ChipDirection(text: String) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
    }
}