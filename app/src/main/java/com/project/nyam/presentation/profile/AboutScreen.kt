package com.project.nyam.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val HijauNyam = Color(0xFF4CAF50)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tentang NYAM", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- LOGO & SLOGAN ---
            Image(
                painter = painterResource(id = R.drawable.nyam_logo),
                contentDescription = "NYAM Logo",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "NYAM",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HijauNyam
            )
            Text(
                text = "Not Your Average Menu",
                fontSize = 14.sp,
                color = Color.Gray,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(32.dp))

            // --- DESKRIPSI APLIKASI ---
            AboutSectionCard(
                title = "Apa itu NYAM?",
                icon = Icons.Default.Info,
                color = HijauNyam,
                description = "NYAM adalah aplikasi inovatif yang dirancang untuk membantu Anda menemukan menu diet sehat menggunakan bahan makanan yang sudah ada di rumah. Dengan teknologi Image Recognition, NYAM menganalisis bahan makanan Anda dan memberikan rekomendasi resep yang dipersonalisasi sesuai dengan kebutuhan gizi tubuh."
            )

            Spacer(Modifier.height(16.dp))

            // --- DEVELOPER TEAM (C242-PS136) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, null, tint = HijauNyam, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "The Team C242-PS136",
                            fontWeight = FontWeight.Bold,
                            color = HijauNyam,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    // List Pengembang Berdasarkan Role
                    DeveloperItem("Cloud Computing", "Aqil Muhammad Fachrezi & Christopher")
                    DeveloperItem("Mobile Development", "Yohanes Christianto & Kaleb Gibran")
                    DeveloperItem("Machine Learning", "Fanny Rorencia, Janet Deby & Theophilus")
                }
            }

            Spacer(Modifier.height(40.dp))

            // --- FOOTER VERSION ---
            Text(
                text = "Version 1.0.0 (Capstone Project)",
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Text(
                text = "© 2026 NYAM Project Team",
                fontSize = 12.sp,
                color = Color.LightGray
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun AboutSectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun DeveloperItem(role: String, names: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(role, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(names, fontSize = 14.sp, color = Color.DarkGray)
    }
}