package com.example.priqnix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("О компании", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ООО «Айкьюникс» — аккредитованная IT-компания, резидент технопарка. Мы специализируемся на разработке компьютерного ПО, информационных систем, мобильных приложений и облачных решений. Входим в реестр отечественного ПО Минцифры РФ.",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Миссия: создавать инновационные продукты, которые помогают бизнесу расти и адаптироваться к цифровому миру.")
    }
}