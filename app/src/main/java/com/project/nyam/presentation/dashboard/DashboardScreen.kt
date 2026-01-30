package com.project.nyam.presentation.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.nyam.data.model.*
import com.project.nyam.presentation.dashboard.components.*
import java.util.Calendar
import com.project.nyam.presentation.dashboard.tabs.HomeTab
import com.project.nyam.presentation.dashboard.tabs.HistoryTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userData: FullUserProfile,
    historyData: HistoryData?,
    recommendations: List<Recipe>,
    onNavigateToProfile: () -> Unit,
    onUpdateTdee: (Double) -> Unit,
    onCookMeal: (MealRequest) -> Unit,
    onRefresh: ((Boolean) -> Unit) -> Unit
) {
    var currentTab by remember { mutableStateOf("home_content") }

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Selamat Pagi"
        in 12..14 -> "Selamat Siang"
        in 15..18 -> "Selamat Sore"
        else -> "Selamat Malam"
    }

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = currentTab, onNavigate = { currentTab = it }) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("$greeting, ${userData.name}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Sudah makan apa hari ini?", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        AsyncImage(
                            model = userData.photoUrl ?: Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(36.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // Logika Navigasi Tab (Traffic Controller)
            when (currentTab) {
                "home_content" -> {
                    HomeTab(
                        userData = userData,
                        historyData = historyData,
                        recommendations = recommendations,
                        onUpdateTdee = onUpdateTdee,
                        onRefresh = onRefresh,
                        onCookMeal = onCookMeal
                    )
                }
                "history" -> {
                    HistoryTab(
                        historyData = historyData,
                        onRefresh = onRefresh,
                        onCookMeal = onCookMeal
                    )
                }
                // Tab news, scan, dan chat bisa menyusul
                else -> {
                    PlaceholderTab(currentTab)
                }
            }
        }
    }
}

@Composable
fun PlaceholderTab(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Halaman $title - Sedang dalam Pengembangan", color = Color.Gray)
    }
}