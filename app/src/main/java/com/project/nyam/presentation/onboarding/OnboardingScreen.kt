package com.project.nyam.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class PageData(
    val judul: String,
    val deskripsi: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit // <--- INI KABEL BARUNYA
) {
    val HijauNyam = Color(0xFF4CAF50)
    val PutihBersih = Color(0xFFFFFFFF)

    val pages = listOf(
        PageData("Welcome to NYAM!", "Asisten Gizi Cerdas Anda. Kami bantu atur pola makan sehat.", Icons.Default.Home),
        PageData("Track Nutrition", "Pantau kalori harian dan nutrisi tubuh dengan mudah.", Icons.Default.CheckCircle),
        PageData("Scan Food", "Foto bahan makanan di kulkas, kami carikan resepnya.", Icons.Default.Face)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(PutihBersih).padding(24.dp)) {
        // Tombol Skip
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = {
                onNavigateToLogin() // <--- Kalau diklik, pindah ke Login
            }) {
                Text("Skip", color = HijauNyam, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Slider
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { position ->
            val page = pages[position]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(page.icon, null, modifier = Modifier.size(150.dp), tint = HijauNyam)
                Spacer(modifier = Modifier.height(40.dp))
                Text(page.judul, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = HijauNyam, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Text(page.deskripsi, fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }

        // Indikator & Tombol Next
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row {
                repeat(pages.size) { iteration ->
                    val warna = if (pagerState.currentPage == iteration) HijauNyam else Color.LightGray
                    Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(warna).size(10.dp))
                }
            }
            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            // Kalau halaman terakhir diklik next, pindah ke Login
                            onNavigateToLogin()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HijauNyam),
                shape = CircleShape,
                modifier = Modifier.size(60.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.ArrowForward, "Next")
            }
        }
    }
}