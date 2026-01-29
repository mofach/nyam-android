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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.data.model.NutritionSummary
import com.project.nyam.data.model.NutritionalNeeds
import androidx.compose.foundation.BorderStroke

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

        // REVISI: Tetap tampilkan angka gram meskipun berlebih
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

        // REVISI 2: Badge Warning di pojok kanan atas Box
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
        Text(body, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Justify, lineHeight = 20.sp)
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

@Composable
fun BmrScoreView(score: Int) {
    val colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4), Color(0xFF4CAF50), Color(0xFFFBC02D), Color(0xFFF57C00), Color(0xFFD32F2F))
    val currentColor = colors.getOrElse(score) { Color.Gray }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Speed, null, tint = currentColor, modifier = Modifier.size(40.dp))
        Text("BMR Score", fontSize = 10.sp, color = Color.Gray)
        Text("$score", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = currentColor)
    }
}

@Composable
fun BmiLegendView() {
    val labels = listOf("Extremely Weak", "Weak", "Normal", "Overweight", "Obesity", "Extreme Obesity")
    val colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4), Color(0xFF4CAF50), Color(0xFFFBC02D), Color(0xFFF57C00), Color(0xFFD32F2F))
    Column(Modifier.padding(top = 12.dp)) {
        labels.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                row.forEach { label ->
                    val idx = labels.indexOf(label)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                        Box(Modifier.size(12.dp).clip(CircleShape).background(colors[idx]))
                        Spacer(Modifier.width(8.dp))
                        Text(label, fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}