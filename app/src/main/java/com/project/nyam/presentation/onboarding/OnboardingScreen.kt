package com.project.nyam.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.R
import kotlinx.coroutines.launch

// Modifikasi sedikit datanya agar bisa menampung Logo atau Icon
data class PageData(
    val judul: String,
    val deskripsi: String,
    val icon: ImageVector? = null,
    val isLogo: Boolean = false
)

// ... import lainnya tetap sama ...

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val HijauNyam = Color(0xFF4CAF50)
    val PutihBersih = Color(0xFFFFFFFF)

    val pages = listOf(
        PageData("Welcome to NYAM!", "Asisten Gizi Cerdas Anda. Kami bantu atur pola makan sehat.", isLogo = true),
        PageData("Track Nutrition", "Pantau kalori harian dan nutrisi tubuh dengan mudah.", icon = Icons.Default.CheckCircle),
        PageData("Scan Food", "Foto bahan makanan di kulkas, kami carikan resepnya.", icon = Icons.Default.RestaurantMenu)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(PutihBersih).padding(24.dp)) {

        // --- HEADER: SKIP BUTTON ---
        Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterEnd) {
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(onClick = { onNavigateToLogin() }) {
                    Text("Skip", color = HijauNyam, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // --- CONTENT: SLIDER ---
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { position ->
            val page = pages[position]
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (page.isLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.nyam_logo),
                        contentDescription = "Logo NYAM",
                        modifier = Modifier.size(180.dp),
                        contentScale = ContentScale.Fit
                    )

                    // --- BRANDING TEXT (Hanya di halaman pertama) ---
                    Spacer(modifier = Modifier.height(55.dp))
                    Text(
                        text = "NYAM",
                        fontSize = 52.sp,
                        color = HijauNyam,
                        letterSpacing = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Not Your Average Menu",
                        fontSize = 12.sp,
                        color = HijauNyam,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(page.icon!!, null, modifier = Modifier.size(140.dp), tint = HijauNyam)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = page.judul,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.deskripsi,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                // --- FOOTER CREDIT (Muncul di semua halaman onboarding secara halus) ---
                if (pagerState.currentPage == 0) { // Jika hanya ingin di hal 1, biarkan if ini
                    Spacer(modifier = Modifier.height(60.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Built by the NYAM Team C242-PS136",
                            fontSize = 9.sp,
                            color = Color.LightGray,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Refined by Aqil Muhammad Fachrezi",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // --- INDICATOR & NEXT BUTTON ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                repeat(pages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) HijauNyam else Color.LightGray)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onNavigateToLogin()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HijauNyam),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(56.dp)
                    .width(if (pagerState.currentPage == pages.size - 1) 160.dp else 64.dp)
            ) {
                if (pagerState.currentPage == pages.size - 1) {
                    Text("Get Started", fontWeight = FontWeight.Bold, color = Color.White)
                } else {
                    Icon(Icons.Default.ArrowForward, "Next", tint = Color.White)
                }
            }
        }
    }
}