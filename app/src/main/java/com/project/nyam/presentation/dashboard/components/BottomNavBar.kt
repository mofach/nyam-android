package com.project.nyam.presentation.dashboard.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.nyam.data.model.BottomNavItem

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val HijauNyam = Color(0xFF4CAF50)
    val items = listOf(
        BottomNavItem("Home", "home_content", Icons.Default.Home),
        BottomNavItem("History", "history", Icons.Default.History),
        BottomNavItem("Scan", "scan", Icons.Default.QrCodeScanner),
        BottomNavItem("News", "news", Icons.Default.Newspaper),
        BottomNavItem("Chat", "chat", Icons.Default.Chat)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = HijauNyam,
                    selectedTextColor = HijauNyam,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = HijauNyam.copy(alpha = 0.1f)
                )
            )
        }
    }
}