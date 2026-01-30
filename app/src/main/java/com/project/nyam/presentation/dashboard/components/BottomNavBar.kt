package com.project.nyam.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bar Utama
        BottomAppBar(
            containerColor = Color.White,
            contentColor = Color.Gray,
            tonalElevation = 8.dp,
            modifier = Modifier.height(70.dp),
            actions = {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceAround) {
                    NavBarItem(Icons.Default.Home, "Home", "home_content", currentRoute, onNavigate)
                    NavBarItem(Icons.Default.History, "History", "history", currentRoute, onNavigate)
                }

                // Ruang untuk FAB di tengah
                Spacer(Modifier.width(80.dp))

                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceAround) {
                    NavBarItem(Icons.Default.Newspaper, "News", "news", currentRoute, onNavigate)
                    NavBarItem(Icons.Default.Chat, "Chatbot", "chat", currentRoute, onNavigate)
                }
            }
        )

        // FAB Search (Menjorok ke Atas)
        FloatingActionButton(
            onClick = { onNavigate("search") },
            containerColor = if (currentRoute == "search") Color(0xFF2E7D32) else Color(0xFF4CAF50),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(64.dp)
                .offset(y = (-25).dp), // Mengatur seberapa jauh menjorok ke atas
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun NavBarItem(icon: ImageVector, label: String, route: String, currentRoute: String, onNavigate: (String) -> Unit) {
    val isSelected = currentRoute == route
    Column(
        modifier = Modifier
            .clickable { onNavigate(route) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = if (isSelected) Color(0xFF4CAF50) else Color.Gray)
        Text(label, fontSize = 10.sp, color = if (isSelected) Color(0xFF4CAF50) else Color.Gray)
    }
}