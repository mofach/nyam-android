package com.project.nyam.presentation.dashboard

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.nyam.data.model.*
import com.project.nyam.data.remote.ApiClient
import com.project.nyam.presentation.dashboard.components.*
import com.project.nyam.presentation.dashboard.tabs.*
import com.project.nyam.util.FileUtil
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest // Penting untuk Manifest.permission.CAMERA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userData: FullUserProfile,
    historyData: HistoryData?,
    recommendations: List<Recipe>,
    newsList: List<NewsItem>,
    onGetToken: suspend () -> String?,
    onNavigateToProfile: () -> Unit,
    onUpdateTdee: (Double) -> Unit,
    onCookMeal: (MealRequest) -> Unit,
    onRefresh: ((Boolean) -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf("home_content") }
    var searchResults by remember { mutableStateOf<List<Recipe>?>(null) }
    var recognitionResult by remember { mutableStateOf<RecognitionInfo?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var isCameraOpen by remember { mutableStateOf(false) }

    // Launcher untuk Izin Kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraOpen = true
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Selamat Pagi"
        in 12..14 -> "Selamat Siang"
        in 15..18 -> "Selamat Sore"
        else -> "Selamat Malam"
    }

    // Fungsi Reusable untuk Upload Gambar (Dipakai Galeri & Kamera)
    val handleImageUpload: (android.net.Uri) -> Unit = { uri ->
        isSearching = true
        scope.launch {
            try {
                val token = onGetToken()
                val multipart = FileUtil.uriToMultipart(context, uri, "file")
                if (multipart != null) {
                    val res = ApiClient.instance.predictFood("Bearer $token", multipart)
                    if (res.isSuccessful) {
                        searchResults = res.body()?.data?.recommendations?.recipes
                        recognitionResult = res.body()?.data?.recognition
                    } else {
                        Toast.makeText(context, "Makanan tidak dikenali", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            } finally {
                isSearching = false
            }
        }
    }

    // --- LOGIKA OVERLAY ---
    if (isCameraOpen) {
        // Layar Kamera In-App
        CameraScreen(
            onImageCaptured = { uri ->
                isCameraOpen = false
                handleImageUpload(uri)
            },
            onClose = { isCameraOpen = false }
        )
    } else if (searchResults != null) {
        // Halaman Hasil Pencarian
        SearchResultPage(
            results = searchResults!!,
            recognitionInfo = recognitionResult,
            onBack = {
                searchResults = null
                recognitionResult = null
            },
            onCookMeal = onCookMeal
        )
    } else {
        // --- TAMPILAN DASHBOARD UTAMA ---
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    BottomNavBar(
                        currentRoute = currentTab,
                        onNavigate = { currentTab = it }
                    )
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Column(
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                                    .fillMaxWidth()
                            ) {
                                val displayName = userData.name.split(" ").firstOrNull() ?: userData.name
                                Text(
                                    text = "$greeting, $displayName",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Sudah makan apa hari ini?",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier.padding(end = 12.dp).size(44.dp)
                            ) {
                                if (!userData.photoUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = userData.photoUrl,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(38.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(38.dp),
                                        tint = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                        modifier = Modifier.shadow(2.dp)
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(Color(0xFFF8F9FA))
                ) {
                    when (currentTab) {
                        "home_content" -> HomeTab(
                            userData = userData,
                            historyData = historyData,
                            recommendations = recommendations,
                            onUpdateTdee = onUpdateTdee,
                            onRefresh = onRefresh,
                            onCookMeal = onCookMeal
                        )
                        "history" -> HistoryTab(
                            historyData = historyData,
                            onRefresh = onRefresh,
                            onCookMeal = onCookMeal
                        )
                        "news" -> NewsTab(
                            newsList = newsList,
                            onRefresh = onRefresh
                        )
                        "chat" -> ChatTab(
                            getToken = onGetToken
                        )
                        "search" -> SearchTab(
                            onSearchText = { query ->
                                isSearching = true
                                scope.launch {
                                    try {
                                        val token = onGetToken()
                                        val res = ApiClient.instance.searchByText("Bearer $token", query)
                                        if (res.isSuccessful) {
                                            searchResults = res.body()?.data?.recipes
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Search failed", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isSearching = false
                                    }
                                }
                            },
                            onSearchImage = { uri -> handleImageUpload(uri) },
                            onLaunchCamera = {
                                // Cek izin dulu baru buka kamera
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }// FIX ERROR
                        )
                    }
                }
            }

            // Global Loading Overlay
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(Modifier.height(16.dp))
                        Text("Analyzing Food...", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}