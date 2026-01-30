package com.project.nyam.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.data.model.FullUserProfile
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    userData: FullUserProfile?,
    onBack: () -> Unit,
    onSave: (name: String, birthdate: String, gender: Int, height: Int, weight: Int, activityLevel: Double, allergies: List<String>) -> Unit
) {
    // State Loading
    var isSaving by remember { mutableStateOf(false) }

    // State Form
    var name by remember { mutableStateOf(userData?.name ?: "") }
    var birthdate by remember { mutableStateOf(userData?.birthdate ?: "2000-01-01") }
    var height by remember { mutableStateOf(userData?.physicalData?.height?.toString() ?: "") }
    var weight by remember { mutableStateOf(userData?.physicalData?.weight?.toString() ?: "") }
    var gender by remember { mutableStateOf(userData?.physicalData?.gender ?: 0) }
    var selectedAllergies by remember { mutableStateOf(userData?.preferences?.allergies ?: emptyList()) }

    // DatePicker State
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val allergyOptions = listOf(
        "gluten-free", "dairy-free", "egg-free", "soy-free", "wheat-free",
        "fish-free", "shellfish-free", "tree-nut-free", "peanut-free"
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        birthdate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDate().toString()
                    }
                    showDatePicker = false
                }) { Text("OK", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // Gunakan Box sebagai wrapper utama untuk menumpuk Loading Overlay
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profil", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack, enabled = !isSaving) {
                            Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                        }
                    },
                    actions = {
                        // Hilangkan button simpan saat loading agar tidak double click
                        if (!isSaving) {
                            TextButton(onClick = {
                                isSaving = true
                                onSave(name, birthdate, gender, height.toIntOrNull() ?: 0, weight.toIntOrNull() ?: 0, userData?.physicalData?.activityLevel ?: 1.2, selectedAllergies)
                            }) {
                                Text("Simpan", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            }
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
                    .padding(24.dp)
            ) {
                Text("Nama Lengkap", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                OutlinedTextField(
                    value = name,
                    onValueChange = { if(!isSaving) name = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4CAF50))
                )

                Spacer(Modifier.height(20.dp))

                Text("Tanggal Lahir", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                OutlinedTextField(
                    value = birthdate,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { if(!isSaving) showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Icon(Icons.Default.DateRange, null, tint = Color(0xFF4CAF50)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4CAF50))
                )

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Tinggi (cm)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        OutlinedTextField(
                            value = height,
                            onValueChange = { if (!isSaving && it.all { c -> c.isDigit() }) height = it },
                            modifier = Modifier.padding(top = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4CAF50))
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Berat (kg)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { if (!isSaving && it.all { c -> c.isDigit() }) weight = it },
                            modifier = Modifier.padding(top = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4CAF50))
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("Jenis Kelamin", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Laki-laki" to 0, "Perempuan" to 1).forEach { (label, value) ->
                        FilterChip(
                            selected = gender == value,
                            onClick = { if(!isSaving) gender = value },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFE8F5E9), selectedLabelColor = Color(0xFF2E7D32))
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("Preferensi Alergi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    allergyOptions.forEach { allergy ->
                        val isSelected = selectedAllergies.contains(allergy)
                        FilterChip(
                            selected = isSelected,
                            onClick = { if (!isSaving) selectedAllergies = if (isSelected) selectedAllergies - allergy else selectedAllergies + allergy },
                            label = { Text(allergy, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFE8F5E9), selectedLabelColor = Color(0xFF2E7D32))
                        )
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }

        // --- FULL SCREEN LOADING OVERLAY (BLUR/DIM EFFECT) ---
        if (isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Efek Dim
                    .clickable(enabled = false) {}, // Block interaksi user ke layer bawah
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                        Spacer(Modifier.height(16.dp))
                        Text("Menyimpan perubahan...", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}