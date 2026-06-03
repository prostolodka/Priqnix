package com.example.priqnix.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

@Composable
fun ContactsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Контакты", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("📍 Адрес: г. Омск, ул. Нефтезаводская, 42")
        Text("📞 Телефон: +7 (3812) 123-456")
        Text("✉️ Email: info@priqnix.ru")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Карта проезда:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    val html = """
                        <!DOCTYPE html>
                        <html>
                        <head><meta name="viewport" content="width=device-width, initial-scale=1.0"></head>
                        <body style="margin:0;padding:0;">
                            <iframe src="https://yandex.ru/map-widget/v1/?um=constructor%3A1b2c3d4e5f6g7h8i9j0k&source=constructor" 
                                    width="100%" height="100%" frameborder="0"></iframe>
                        </body>
                        </html>
                    """.trimIndent()
                    loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}