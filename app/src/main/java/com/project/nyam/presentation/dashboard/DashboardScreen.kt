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
                                    .padding(start = 4.dp) // Spacing yang lebih proporsional
                            ) {
                                val displayName = userData.name.split(" ").firstOrNull() ?: userData.name

                                // Greeting dengan warna yang lebih deep
                                Text(
                                    text = "$greeting, $displayName!",
                                    fontSize = 20.sp, // Ukuran sedikit dinaikkan agar dominan
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2D3436), // Dark charcoal agar tidak kaku
                                    letterSpacing = (-0.5).sp // Sedikit rapat agar modern
                                )

                                // Sub-title dengan aksen hijau tipis
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WavingHand, // Memberi kesan ramah
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = Color(0xFF4CAF50)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Sudah makan apa hari ini?",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        },
                        actions = {
                            // Container Profile dengan Border halus
                            Surface(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable { onNavigateToProfile() },
                                color = Color(0xFFF1F8E9), // Background soft green jika foto loading
                                border = BorderStroke(2.dp, Color(0xFF4CAF50).copy(alpha = 0.2f)) // Ring estetik
                            ) {
                                if (!userData.photoUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = userData.photoUrl,
                                        contentDescription = "Profile",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color(0xFF4CAF50)
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        ),
                        // Menggunakan modifier bawaan tanpa shadow berat agar terlihat clean (flat design)
                        windowInsets = WindowInsets.statusBars
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