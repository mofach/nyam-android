package com.project.nyam.presentation.dashboard.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.data.model.HistoryData
import com.project.nyam.presentation.dashboard.components.HistoryMealItem
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.project.nyam.data.model.Recipe
import com.project.nyam.data.model.Meal
import com.project.nyam.data.model.MealRequest
import com.project.nyam.data.model.MacroNutrients
import com.project.nyam.presentation.dashboard.components.RecipeDetailSheet
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTab(
    historyData: HistoryData?,
    onCookMeal: (MealRequest) -> Unit,
    onRefresh: ((Boolean) -> Unit) -> Unit
) {
    // Balik urutan list: Makanan terbaru tampil paling atas
    val meals = historyData?.meals?.reversed() ?: emptyList()
    val summary = historyData?.summary

    // State untuk menyimpan meal yang dipilih
    var selectedMeal by remember { mutableStateOf<Meal?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullState,
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh { isRefreshing = it }
        },
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // --- HEADER RINGKASAN HARIAN ---
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "DAILY SUMMARY",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            Surface(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Terkonsumsi", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            "${summary?.totalCalories ?: 0} kkal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    // Info makro mini
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        VerticalMacroInfo("C", "${summary?.totalCarbs ?: 0}g", Color(0xFFFFA726))
                        VerticalMacroInfo("P", "${summary?.totalProtein ?: 0}g", Color(0xFF42A5F5))
                        VerticalMacroInfo("F", "${summary?.totalFat ?: 0}g", Color(0xFFEF5350))
                    }
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )

            // --- DAFTAR RIWAYAT MAKAN ---
            if (meals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.LightGray
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Belum ada riwayat makan hari ini",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp), // Beri ruang untuk Navbar
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "MEAL LOGS",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        )
                    }

                    items(meals) { meal ->
                        Box(modifier = Modifier.clickable { selectedMeal = meal }) {
                            HistoryMealItem(meal = meal)
                        }
                    }
                }
            }
        }
    }
    // Tampilkan Detail Sheet saat item histori diklik
    if (selectedMeal != null) {
        // Kita konversi Meal ke Recipe agar bisa pakai Sheet yang sama
        val mealAsRecipe = Recipe(
            label = selectedMeal!!.foodName,
            image = selectedMeal!!.imageUrl ?: "",
            calories = selectedMeal!!.calories,
            nutrients = MacroNutrients(selectedMeal!!.carbs, selectedMeal!!.protein, selectedMeal!!.fat),
            sourceUrl = selectedMeal!!.sourceUrl ?: "", // Histori mungkin punya sourceUrl null
            time = 0,
            totalWeight = 0.0,
            cuisineType = null,
            mealType = null,
            ingredients = null // Histori biasanya tidak simpan list bahan
        )

        RecipeDetailSheet(
            recipe = mealAsRecipe,
            mealTime = selectedMeal!!.mealTime,
            onCookMeal = { mealReq ->
                onCookMeal(mealReq)
            },
            onDismiss = { selectedMeal = null }
        )
    }
}

@Composable
fun VerticalMacroInfo(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}