package com.project.nyam.presentation.dashboard.tabs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.data.model.*
import com.project.nyam.presentation.dashboard.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    userData: FullUserProfile,
    historyData: HistoryData?,
    recommendations: List<Recipe>,
    onUpdateTdee: (Double) -> Unit,
    onCookMeal: (MealRequest) -> Unit,
    onRefresh: ((Boolean) -> Unit) -> Unit
) {
    val HijauNyam = Color(0xFF4CAF50)
    val summary = historyData?.summary ?: NutritionSummary(0, 0, 0, 0)
    val target = userData.nutritionalNeeds

    var expandedTdee by remember { mutableStateOf(false) }
    var showMacroInfo by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    var showOverLimitDialog by remember { mutableStateOf(false) }
    var overLimitMessage by remember { mutableStateOf("") }

    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

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

            // --- SECTION 1: GIZI ---
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

            // --- SECTION 2: HEALTH METRICS ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // 1. HEADER: BASIC INFO
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetricHeaderItem(
                            icon = if (userData.physicalData.gender == 0) Icons.Default.Male else Icons.Default.Female,
                            label = "GENDER",
                            value = if (userData.physicalData.gender == 0) "Laki-laki" else "Perempuan",
                            tint = Color(0xFF42A5F5)
                        )
                        MetricHeaderItem(
                            icon = Icons.Default.Cake,
                            label = "AGE",
                            value = "${userData.physicalData.age} Tahun",
                            tint = Color(0xFFEC407A)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                    // 2. BODY COMPOSITION
                    Text("BODY COMPOSITION", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold, color = Color.Gray))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.weight(1.5f),
                            color = Color(0xFF3F51B5).copy(alpha = 0.05f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("BMI", color = Color(0xFF3F51B5), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${userData.healthStats.bmi}", color = Color(0xFF3F51B5), fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                                Text(userData.healthStats.bmiStatus, color = Color(0xFF3F51B5).copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            CompactMetricTile("Height", "${userData.physicalData.height} cm", Icons.Default.Height)
                            Spacer(modifier = Modifier.height(8.dp))
                            CompactMetricTile("Weight", "${userData.physicalData.weight} kg", Icons.Default.FitnessCenter)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. METABOLISM ANALYSIS
                    Text("METABOLISM ANALYSIS", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold, color = Color.Gray))
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7FA), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("BMR Value", fontSize = 12.sp, color = Color.Gray)
                                    Text("${userData.healthStats.bmr} kcal/day", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                BmrScoreBadge(userData.healthStats.bmrScore, userData.healthStats.bmrLabel)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                val colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4), Color(0xFF4CAF50), Color(0xFFFBC02D), Color(0xFFF57C00), Color(0xFFD32F2F))
                                for (i in 0..5) {
                                    val isCurrent = userData.healthStats.bmrScore == i
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(7.dp).background(colors[i], CircleShape))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("S$i", fontSize = 9.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal, color = if (isCurrent) Color.Black else Color.Gray)
                                    }
                                }
                            }
                            Text("*S0: Ext. Weak, S1: Weak, S2: Normal, S3: Overweight, S4: Obesity, S5: Ext. Obesity", fontSize = 8.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. LIFESTYLE & PREFERENCES (Full Width & Chips)
                    Text(
                        "LIFESTYLE & PREFERENCES",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // --- ACTIVITY LEVEL (DENGAN DROPDOWN) ---
                        Text("ACTIVITY LEVEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray.copy(alpha = 0.6f))
                        Box {
                            Surface(
                                onClick = { expandedTdee = true },
                                modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFF9800).copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.1f))
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DirectionsRun, null, modifier = Modifier.size(20.dp), tint = Color(0xFFFF9800))
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(activityLabel, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFFF9800))
                                        Text("Ketuk untuk mengubah intensitas harian", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ExpandMore, null, modifier = Modifier.size(20.dp), tint = Color(0xFFFF9800))
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

                        Spacer(modifier = Modifier.height(20.dp))

                        // --- ALLERGIES (CHIPS STYLE) ---" sampai akhir Column di DashboardScreen.kt dengan ini:

                        val allergies = userData.preferences.allergies
                        Text("ALLERGIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(8.dp))

                        when {
                            // KASUS 0 ATAU 1: Full Sebaris
                            allergies.isEmpty() || allergies.size == 1 -> {
                                AllergyChip(
                                    text = if (allergies.isEmpty()) "Tidak ada alergi terdeteksi (Aman!)" else allergies[0],
                                    isNone = allergies.isEmpty(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // KASUS GENAP (2, 4, 6...): Bagi Dua Rata
                            allergies.size % 2 == 0 -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    allergies.chunked(2).forEach { pair ->
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            pair.forEach { item -> AllergyChip(text = item, modifier = Modifier.weight(1f)) }
                                        }
                                    }
                                }
                            }

                            // KASUS GANJIL (3, 5...): Baris pertama Full, sisanya Bagi Dua
                            else -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AllergyChip(text = allergies[0], modifier = Modifier.fillMaxWidth())
                                    allergies.drop(1).chunked(2).forEach { pair ->
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            pair.forEach { item -> AllergyChip(text = item, modifier = Modifier.weight(1f)) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- SECTION 3: SMART RECOMMENDATIONS ---
            Text(
                "SMART RECOMMENDATIONS",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )
            Text("Berdasarkan sisa kuota gizi harimu", fontSize = 12.sp, color = Color.Gray)

            Spacer(Modifier.height(16.dp))

            // SEKARANG kita pakai data 'recommendations' asli dari API
            if (recommendations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.height(8.dp))
                        Text("Mencari resep terbaik untukmu...", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp) // Spacing sudah diatur di RecommendationItem
                ) {
                    items(recommendations) { recipe ->
                        RecommendationItem(
                            recipe = recipe,
                            onClick = { selectedRecipe = recipe } // Set resep yang dipilih saat diklik
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (selectedRecipe != null) {
        RecipeDetailSheet(
            recipe = selectedRecipe!!,
            onCookMeal = { req ->
                onCookMeal(req)
                selectedRecipe = null // Tutup sheet setelah klik masak
            },
            onDismiss = { selectedRecipe = null } // Tutup sheet
        )
    }

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