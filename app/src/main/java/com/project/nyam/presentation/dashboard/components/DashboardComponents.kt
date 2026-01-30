package com.project.nyam.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.data.model.NutritionSummary
import com.project.nyam.data.model.NutritionalNeeds
import com.project.nyam.data.model.MealRequest
import com .project.nyam.data.model.Meal
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.foundation.layout.Arrangement
import com.project.nyam.data.model.Recipe
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.RestaurantMenu
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

@Composable
fun NutritionRing(label: String, current: Int, target: Int, color: Color, onClick: () -> Unit) {
    val progress = if (target > 0) current.toFloat() / target else 0f
    val isOver = progress > 1f

    val displayIcon = when(label) {
        "Karbo" -> Icons.Default.RiceBowl
        "Protein" -> Icons.Default.EggAlt
        "Lemak" -> Icons.Default.Opacity
        else -> Icons.Default.Circle
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(85.dp)) {
                CircularProgressIndicator(progress = 1f, color = color.copy(alpha = 0.15f), strokeWidth = 8.dp, modifier = Modifier.fillMaxSize())
                CircularProgressIndicator(progress = progress.coerceAtMost(1f), color = color, strokeWidth = 8.dp, strokeCap = StrokeCap.Round, modifier = Modifier.fillMaxSize())
                Icon(displayIcon, null, tint = color, modifier = Modifier.size(32.dp))
            }

            if (isOver) {
                Surface(
                    modifier = Modifier.size(24.dp).offset(x = 4.dp, y = (-4).dp),
                    shape = CircleShape,
                    color = Color(0xFFE53935),
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Icon(Icons.Default.PriorityHigh, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)

        Text(
            text = "$current / ${target}g",
            fontSize = 11.sp,
            color = if(isOver) Color.Red else Color.Gray,
            fontWeight = if(isOver) FontWeight.Bold else FontWeight.Normal
        )

        val kkal = if (label == "Lemak") current * 9 else current * 4
        Text("${kkal} kkal", fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CalorieMasterBox(summary: NutritionSummary, target: NutritionalNeeds, onClick: () -> Unit) {
    val HijauNyam = Color(0xFF4CAF50)
    val progress = if(target.calories > 0) summary.totalCalories.toFloat() / target.calories else 0f

    Box(contentAlignment = Alignment.TopEnd) {
        Card(
            modifier = Modifier.fillMaxWidth().height(150.dp).clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = HijauNyam)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("${summary.totalCalories} / ${target.calories} kkal", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
                if (progress > 1f) Text("Melebihi Target!", color = Color.White.copy(0.9f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                SegmentedProgressBar(summary, target, Color(0xFFFFA726), Color(0xFF42A5F5), Color(0xFFEF5350), isMaster = true)
            }
        }

        if (progress > 1f) {
            Surface(
                modifier = Modifier.size(32.dp).offset(x = 8.dp, y = (-8).dp),
                shape = CircleShape,
                color = Color(0xFFB71C1C),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.padding(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroInfoSheet(label: String, onDismiss: () -> Unit) {
    val color = when(label) {
        "Karbo" -> Color(0xFFFFA726)
        "Protein" -> Color(0xFF42A5F5)
        "Total Kalori" -> Color(0xFF4CAF50)
        else -> Color(0xFFEF5350)
    }

    val content = when(label) {
        "Karbo" -> MacroData.karbo
        "Protein" -> MacroData.protein
        "Total Kalori" -> MacroData.kalori
        else -> MacroData.lemak
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
            }
            Surface(Modifier.size(80.dp), shape = CircleShape, color = color.copy(alpha = 0.1f)) {
                val icon = when(label) {
                    "Karbo" -> Icons.Default.RiceBowl
                    "Protein" -> Icons.Default.EggAlt
                    "Total Kalori" -> Icons.Default.LocalFireDepartment
                    else -> Icons.Default.Opacity
                }
                Icon(icon, null, tint = color, modifier = Modifier.padding(20.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(label, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(Modifier.height(24.dp))
            InfoSection("Apa itu $label?", content.deskripsi)
            InfoSection("Kontribusi dalam Tubuh", content.kontribusi)
            InfoSection("Contoh Sumber Makanan", content.contoh)
            InfoSection("Insight NYAM", content.insight)
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun InfoSection(title: String, body: String) {
    Column(Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        Spacer(Modifier.height(8.dp))
        Text(body, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
    }
}

object MacroData {
    data class MacroInfo(val deskripsi: String, val kontribusi: String, val contoh: String, val insight: String)
    val karbo = MacroInfo("Karbohidrat adalah sumber energi utama.", "Diubah menjadi glukosa untuk bahan bakar sel.", "Nasi merah, ubi, gandum.", "Pilih karbo kompleks.")
    val protein = MacroInfo("Membangun jaringan tubuh.", "Membantu hormon dan imun.", "Dada ayam, telur, tempe.", "Jaga otot Anda.")
    val lemak = MacroInfo("Cadangan energi tubuh.", "Membantu serap vitamin A,D,E,K.", "Alpukat, zaitun.", "Fokus lemak tak jenuh.")
    val kalori = MacroInfo("Satuan energi dari makanan.", "Bahan bakar fungsi dasar tubuh.", "Karbo (4), Protein (4), Lemak (9).", "Keseimbangan adalah kunci.")
}

@Composable
fun SegmentedProgressBar(summary: NutritionSummary, target: NutritionalNeeds, cColor: Color, pColor: Color, fColor: Color, isMaster: Boolean = false) {
    val totalTarget = target.calories.toFloat()
    val cW = (summary.totalCarbs * 4) / totalTarget
    val pW = (summary.totalProtein * 4) / totalTarget
    val fW = (summary.totalFat * 9) / totalTarget
    val bgColor = if (isMaster) Color.White.copy(alpha = 0.3f) else Color(0xFFEEEEEE)

    Row(Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(7.dp)).background(bgColor)) {
        if (cW > 0) Box(Modifier.fillMaxHeight().weight(cW.coerceAtLeast(0.01f)).background(cColor))
        if (pW > 0) Box(Modifier.fillMaxHeight().weight(pW.coerceAtLeast(0.01f)).background(pColor))
        if (fW > 0) Box(Modifier.fillMaxHeight().weight(fW.coerceAtLeast(0.01f)).background(fColor))
        val rem = 1f - (cW + pW + fW)
        if (rem > 0) Box(Modifier.fillMaxHeight().weight(rem.coerceAtLeast(0.01f)).background(bgColor))
    }
}

// --- NEW REVISED COMPONENTS FOR HEALTH METRICS ---

@Composable
fun MetricHeaderItem(icon: ImageVector, label: String, value: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = tint)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CompactMetricTile(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BmrScoreBadge(score: Int, label: String) {
    val colors = listOf(
        Color(0xFF2196F3), // 0: Extremely Weak
        Color(0xFF00BCD4), // 1: Weak
        Color(0xFF4CAF50), // 2: Normal
        Color(0xFFFBC02D), // 3: Overweight
        Color(0xFFF57C00), // 4: Obesity
        Color(0xFFD32F2F)  // 5: Extreme Obesity
    )
    val color = colors.getOrElse(score) { Color.Gray }

    Surface(color = color, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = "Score $score: $label",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Tambahkan ini di DashboardComponents.kt
@Composable
fun AllergyChip(text: String, modifier: Modifier = Modifier, isNone: Boolean = false) {
    Surface(
        modifier = modifier,
        color = if (isNone) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isNone) Color(0xFF4CAF50).copy(0.2f) else Color.Red.copy(0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isNone) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isNone) Color(0xFF4CAF50) else Color.Red
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isNone) Color(0xFF4CAF50) else Color.Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SmallMacroBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
    }
}

@Composable
fun RecommendationItem(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp).padding(end = 16.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = recipe.image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(130.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                // Cuisine tag kecil
                Text(
                    text = recipe.cuisineType?.firstOrNull()?.uppercase() ?: "RECIPE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Text(recipe.label, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 15.sp)

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Timer, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Text(" ${recipe.time} min", fontSize = 11.sp, color = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Scale, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Text(" ${recipe.totalWeight.toInt()}g", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailSheet(
    recipe: Recipe,
    onCookMeal: (MealRequest) -> Unit,
    onDismiss: () -> Unit,
    mealTime: String? = null
) {
    val context = LocalContext.current

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                // 1. CUISINE TYPE: Muncul kalau ada (Data Rekomendasi)
                recipe.cuisineType?.firstOrNull()?.let { cuisine ->
                    Surface(
                        modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            cuisine.uppercase(),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(text = recipe.label, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

            // 2. INFO BAR: Adaptif (Time & Weight muncul kalau > 0)
            Row(
                Modifier.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallMacroBadge("${recipe.calories} kkal", Color(0xFF4CAF50))

                if (recipe.time > 0) SmallMacroBadge("${recipe.time} Min", Color.Gray)
                if (recipe.totalWeight > 0) SmallMacroBadge("${recipe.totalWeight.toInt()}g", Color.Blue.copy(0.6f))

                if (!mealTime.isNullOrBlank()) {
                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(text = formatMealTime(mealTime), color = Color.Gray, fontSize = 14.sp)
                }
            }

            // 3. MEAL TYPE: Muncul kalau ada (Data Rekomendasi)
            recipe.mealType?.let { types ->
                Text(
                    text = "Perfect for ${types.joinToString()}",
                    fontSize = 12.sp, color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(Modifier.height(16.dp))
            }

            // 4. INGREDIENTS: Muncul kalau ada (Data Rekomendasi)
            if (!recipe.ingredients.isNullOrEmpty()) {
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(Modifier.height(16.dp))
                Text(text = "Ingredients", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(Modifier.height(8.dp))
                recipe.ingredients.forEach { item ->
                    Row(Modifier.padding(vertical = 4.dp)) {
                        Text(text = "•", modifier = Modifier.padding(end = 8.dp), color = Color(0xFF4CAF50))
                        Text(text = item, fontSize = 14.sp, color = Color.Gray)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // 5. NUTRITION: Pasti muncul (Dua-duanya ada)
            Text(text = "Nutritional Info", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                NutrientDetail("Carbs", "${recipe.nutrients.carbs}g", Color(0xFFFFA726))
                NutrientDetail("Protein", "${recipe.nutrients.protein}g", Color(0xFF42A5F5))
                NutrientDetail("Fat", "${recipe.nutrients.fat}g", Color(0xFFEF5350))
            }

            Spacer(Modifier.height(32.dp))

            // BUTTONS
            if (!recipe.sourceUrl.isNullOrBlank()) {
                Button(
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(recipe.sourceUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.OpenInNew, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Lihat Instruksi Memasak", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = {
                    val mealReq = MealRequest(
                        foodName = recipe.label,
                        calories = recipe.calories,
                        carbs = recipe.nutrients.carbs,
                        protein = recipe.nutrients.protein,
                        fat = recipe.nutrients.fat,
                        imageUrl = recipe.image,
                        sourceUrl = recipe.sourceUrl // TERKIRIM KE BE
                    )
                    onCookMeal(mealReq)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.RestaurantMenu, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Masak & Makan Sekarang", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun NutrientDetail(label: String, value: String, color: Color) {
    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold, color = color)
    }
}

// Tambahkan di DashboardComponents.kt
@Composable
fun HistoryMealItem(meal: Meal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.imageUrl ?: Icons.Default.Fastfood,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.foodName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Kalori
                    Text(
                        text = "${meal.calories} kkal",
                        color = Color(0xFF4CAF50),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    // Meal Time (Jam Makan)
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = formatMealTime(meal.mealTime) ?: "-",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("C: ${meal.carbs}g", fontSize = 10.sp, color = Color(0xFFFFA726))
                Text("P: ${meal.protein}g", fontSize = 10.sp, color = Color(0xFF42A5F5))
                Text("F: ${meal.fat}g", fontSize = 10.sp, color = Color(0xFFEF5350))
            }
        }
    }
}

fun formatMealTime(rawTime: String?): String {
    if (rawTime.isNullOrBlank()) return "-"
    return try {
        // Parse format "2026-01-30T01:56:25.363Z"
        val utcTime = ZonedDateTime.parse(rawTime)
        // Konversi ke zona waktu sistem HP (WIB jika di Indonesia)
        val localTime = utcTime.withZoneSameInstant(ZoneId.systemDefault())
        // Format jadi HH:mm saja
        localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        // Fallback jika parse gagal, ambil potongan jam di tengah
        if (rawTime.contains("T")) {
            rawTime.substringAfter("T").take(5)
        } else "-"
    }
}