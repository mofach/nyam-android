package com.project.nyam.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.nyam.data.model.*
import com.project.nyam.presentation.dashboard.components.RecipeDetailSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultPage(
    results: List<Recipe>,
    recognitionInfo: RecognitionInfo? = null,
    onBack: () -> Unit,
    onCookMeal: (MealRequest) -> Unit // Pastikan parameter ini ada
) {
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Results", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (results.isEmpty()) {
            // REVISI 1: Tampilan Kosong yang Lebih Bagus
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Oops! No recipes found.",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    "Try searching with different keywords.",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // INFO PREDIKSI AI
                recognitionInfo?.let {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF2E7D32))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "AI Prediction:",
                                        fontSize = 11.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = "${it.predicted_class.uppercase()} (${(it.predicted_prob * 100).toInt()}%)",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                            }
                        }
                    }
                }

                items(results) { recipe ->
                    SearchRecipeCard(
                        recipe = recipe,
                        onClick = { selectedRecipe = recipe }
                    )
                }
            }
        }

            // DETAIL SHEET (Disesuaikan dengan DashboardComponents kamu)
            selectedRecipe?.let { recipe ->
                RecipeDetailSheet(
                    recipe = recipe,
                    onDismiss = { selectedRecipe = null },
                    onCookMeal = { mealReq ->
                        onCookMeal(mealReq)
                        selectedRecipe = null
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun SearchRecipeCard(recipe: Recipe, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(24.dp), // Pakai rounded besar biar senada dengan RecommendationItem
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                // Foto Header
                AsyncImage(
                    model = recipe.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp), // Lebih tinggi sedikit biar cakep di scroll vertikal
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    // Cuisine tag kecil di atas judul
                    Text(
                        text = recipe.cuisineType?.firstOrNull()?.uppercase() ?: "RECIPE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4CAF50),
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    // Judul Resep
                    Text(
                        text = recipe.label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(12.dp))

                    // Info Bar (Icon + Text)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Kalori
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            " ${recipe.calories.toInt()} kcal",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(Modifier.width(16.dp))

                        // Waktu
                        Icon(
                            Icons.Default.Timer,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(" ${recipe.time} min", fontSize = 12.sp, color = Color.Gray)

                        Spacer(Modifier.width(16.dp))

                        // Berat
                        Icon(
                            Icons.Default.Scale,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            " ${recipe.totalWeight.toInt()}g",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }