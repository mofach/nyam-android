package com.project.nyam.presentation.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.nyam.data.model.*
import com.project.nyam.presentation.dashboard.components.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userData: FullUserProfile,
    historyData: HistoryData?,
    onNavigateToProfile: () -> Unit,
    onUpdateTdee: (Double) -> Unit,
    onRefresh: ((Boolean) -> Unit) -> Unit
) {
    var currentTab by remember { mutableStateOf("home_content") }
    val HijauNyam = Color(0xFF4CAF50)

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
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA))) {
            if (currentTab == "home_content") {
                HomeTabContent(userData, historyData, onUpdateTdee, onRefresh)
            } else {
                PlaceholderTab(currentTab)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabContent(
    userData: FullUserProfile,
    historyData: HistoryData?,
    onUpdateTdee: (Double) -> Unit,
    onRefresh: ((Boolean) -> Unit) -> Unit
) {
    val HijauNyam = Color(0xFF4CAF50)
    val summary = historyData?.summary ?: NutritionSummary(0, 0, 0, 0)
    val target = userData.nutritionalNeeds

    // STATES
    var expandedTdee by remember { mutableStateOf(false) } // Variabel ini sekarang aman
    var showMacroInfo by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    // DIALOG LOGIC
    var showOverLimitDialog by remember { mutableStateOf(false) }
    var overLimitMessage by remember { mutableStateOf("") }

    LaunchedEffect(summary) {
        val overLimits = mutableListOf<String>()
        if (summary.totalCalories > target.calories) overLimits.add("Kalori")
        if (summary.totalCarbs > target.carbs) overLimits.add("Karbohidrat")
        if (summary.totalProtein > target.protein) overLimits.add("Protein")
        if (summary.totalFat > target.fat) overLimits.add("Lemak")

        if (overLimits.isNotEmpty()) {
            overLimitMessage = "Asupan ${overLimits.joinToString(", ")} Anda telah melebihi target harian."
            showOverLimitDialog = true
        }
    }

    val activityLabel = when (userData.physicalData.activityLevel) {
        1.2 -> "Sedentary"
        1.375 -> "Lightly Active"
        1.55 -> "Moderately Active"
        1.725 -> "Very Active"
        1.9 -> "Extra Active"
        else -> "Custom"
    }

    PullToRefreshBox(
        state = pullState,
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh { isRefreshing = it }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {

            // --- BOX 1: GIZI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Progres Nutrisi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = HijauNyam)
                    Spacer(Modifier.height(16.dp))

                    CalorieMasterBox(summary = summary, target = target) {
                        showMacroInfo = "Total Kalori"
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        NutritionRing("Karbo", summary.totalCarbs, target.carbs, Color(0xFFFFA726)) { showMacroInfo = "Karbo" }
                        NutritionRing("Protein", summary.totalProtein, target.protein, Color(0xFF42A5F5)) { showMacroInfo = "Protein" }
                        NutritionRing("Lemak", summary.totalFat, target.fat, Color(0xFFEF5350)) { showMacroInfo = "Lemak" }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // --- BOX 2: HEALTH METRICS ---
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Tinggi", color = Color.Gray, fontSize = 13.sp)
                            Text("${userData.physicalData.height} cm", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                        BmrScoreView(userData.healthStats.bmrScore)
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Berat", color = Color.Gray, fontSize = 13.sp)
                            Text("${userData.physicalData.weight} kg", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text("Tingkat Aktivitas", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                    // REVISI: Dropdown TDEE
                    Box {
                        Surface(
                            onClick = { expandedTdee = true },
                            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = HijauNyam.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, HijauNyam.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(activityLabel, color = HijauNyam, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                    Text("Target: ${userData.healthStats.tdee} kkal/hari", color = Color.Gray, fontSize = 12.sp)
                                }
                                Icon(Icons.Default.ExpandMore, contentDescription = null, tint = HijauNyam)
                            }
                        }

                        DropdownMenu(
                            expanded = expandedTdee,
                            onDismissRequest = { expandedTdee = false }
                        ) {
                            val options = listOf(
                                "Sedentary (Kantoran)" to 1.2,
                                "Lightly Active (1-3 hari/mgg)" to 1.375,
                                "Moderately Active (3-5 hari/mgg)" to 1.55,
                                "Very Active (6-7 hari/mgg)" to 1.725,
                                "Extra Active (Atletis)" to 1.9
                            )
                            options.forEach { (label, value) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        onUpdateTdee(value)
                                        expandedTdee = false
                                    }
                                )
                            }
                        }
                    }

                    Text("BMI: ${userData.healthStats.bmi} (${userData.healthStats.bmiStatus})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    if (userData.preferences.allergies.isNotEmpty()) {
                        Surface(modifier = Modifier.padding(top = 12.dp), color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Alergi: ${userData.preferences.allergies.joinToString()}", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    BmiLegendView()
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // --- POPUP OVERLIMIT ---
    if (showOverLimitDialog) {
        AlertDialog(
            onDismissRequest = { showOverLimitDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFD32F2F)) },
            title = { Text("Asupan Melebihi Batas!", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(overLimitMessage, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text("Tetap perhatikan pola makan Anda untuk sisa hari ini.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(onClick = { showOverLimitDialog = false }) {
                    Text("Saya Mengerti", color = HijauNyam, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }

    if (showMacroInfo != null) {
        MacroInfoSheet(label = showMacroInfo!!, onDismiss = { showMacroInfo = null })
    }
}

@Composable
fun PlaceholderTab(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Halaman $title - Sedang dalam Pengembangan", color = Color.Gray)
    }
}