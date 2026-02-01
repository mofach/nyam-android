package com.project.nyam.presentation.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.project.nyam.data.model.FullUserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: FullUserProfile?,
    onBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToAbout: () -> Unit, // Parameter navigasi baru untuk About App
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE8F5E9))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER SECTION (CURVED & GRADIENT) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFE8F5E9), Color.White)
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = CircleShape, border = BorderStroke(4.dp, Color.White), shadowElevation = 12.dp) {
                        if (!userData?.photoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = userData?.photoUrl,
                                contentDescription = null,
                                modifier = Modifier.size(110.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(110.dp).background(Color.LightGray),
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = userData?.name ?: "User",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = userData?.email ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // --- TARGET NUTRISI HARIAN (MODERN CARD) ---
                Text(
                    "Target Nutrisi Harian",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFF1F8E9), Color.White)
                                )
                            )
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NutrisiItem("Energi", "${userData?.nutritionalNeeds?.calories ?: 0}", "kkal")
                        NutrisiDivider()
                        NutrisiItem("Karbo", "${userData?.nutritionalNeeds?.carbs ?: 0}", "g")
                        NutrisiDivider()
                        NutrisiItem("Protein", "${userData?.nutritionalNeeds?.protein ?: 0}", "g")
                        NutrisiDivider()
                        NutrisiItem("Lemak", "${userData?.nutritionalNeeds?.fat ?: 0}", "g")
                    }
                }

                Spacer(Modifier.height(32.dp))

                // --- DATA FISIK & PERSONAL (LIST STYLE) ---
                Text(
                    "Informasi Fisik & Akun",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailRow(Icons.Default.Cake, "Tgl Lahir", "${userData?.birthdate ?: "-"} (${userData?.physicalData?.age ?: 0} Thn)")
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
                        DetailRow(Icons.Default.Wc, "Gender", if (userData?.physicalData?.gender == 0) "Laki-laki" else "Perempuan")
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
                        DetailRow(Icons.Default.Straighten, "Tinggi Badan", "${userData?.physicalData?.height ?: 0} cm")
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
                        DetailRow(Icons.Default.MonitorWeight, "Berat Badan", "${userData?.physicalData?.weight ?: 0} kg")
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
                        DetailRow(Icons.Default.Warning, "Alergi", userData?.preferences?.allergies?.joinToString()?.takeIf { it.isNotEmpty() } ?: "Tidak ada")
                    }
                }

                Spacer(Modifier.height(40.dp))

                // --- ACTION BUTTONS ---
                Button(
                    onClick = onNavigateToEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Ubah Profil & Data Fisik", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(Modifier.height(12.dp))

                // Tombol Tentang Aplikasi
                OutlinedButton(
                    onClick = onNavigateToAbout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Tentang Aplikasi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE53935))
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun NutrisiItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
        Text(unit, fontSize = 10.sp, color = Color(0xFF2E7D32))
    }
}

@Composable
fun NutrisiDivider() {
    Box(
        modifier = Modifier
            .height(30.dp)
            .width(1.dp)
            .background(Color(0xFFE0E0E0))
    )
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF4CAF50))
        Spacer(Modifier.width(12.dp))
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF37474F))
    }
}