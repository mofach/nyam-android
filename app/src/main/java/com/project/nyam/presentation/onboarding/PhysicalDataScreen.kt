package com.project.nyam.presentation.onboarding

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.data.model.PhysicalDataRequest
import com.project.nyam.data.remote.ApiClient
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PhysicalDataScreen(uid: String, onComplete: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val HijauNyam = Color(0xFF4CAF50)
    val BackgroundSoft = Color(0xFFF8F9FA)

    // --- STATE FORM ---
    var name by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("1998-05-20") }
    var gender by remember { mutableStateOf(0) } // 0: Male, 1: Female

    val activityOptions = listOf(
        "Sedentary (Kantoran)" to 1.2,
        "Lightly Active (1-3x/minggu)" to 1.375,
        "Moderately Active (3-5x/minggu)" to 1.55,
        "Very Active (6-7x/minggu)" to 1.725,
        "Extra Active (Fisik Berat)" to 1.9
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedActivityText by remember { mutableStateOf(activityOptions[0].first) }
    var selectedActivityValue by remember { mutableStateOf(activityOptions[0].second) }

    val allergyOptions = listOf(
        "gluten-free", "dairy-free", "egg-free", "soy-free",
        "wheat-free", "fish-free", "shellfish-free", "tree-nut-free", "peanut-free"
    )
    val selectedAllergies = remember { mutableStateListOf<String>() }

    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formattedMonth = (month + 1).toString().padStart(2, '0')
                val formattedDay = day.toString().padStart(2, '0')
                birthdate = "$year-$formattedMonth-$formattedDay"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoft)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Lengkapi Profil",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2D3436)
        )
        Text(
            text = "Bantu kami menghitung kebutuhan gizimu",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CARD UTAMA DATA DIRI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // 1. NAMA
                Text("Informasi Dasar", fontWeight = FontWeight.Bold, color = HijauNyam)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = HijauNyam) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. TANGGAL LAHIR
                OutlinedTextField(
                    value = birthdate, onValueChange = {},
                    label = { Text("Tanggal Lahir") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = HijauNyam)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. GENDER
                Text("Jenis Kelamin", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = gender == 0, onClick = { gender = 0 }, colors = RadioButtonDefaults.colors(selectedColor = HijauNyam))
                    Text("Laki-laki")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = gender == 1, onClick = { gender = 1 }, colors = RadioButtonDefaults.colors(selectedColor = HijauNyam))
                    Text("Perempuan")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CARD DATA FISIK
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Metrik Tubuh", fontWeight = FontWeight.Bold, color = HijauNyam)
                Spacer(modifier = Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { if(it.all { c -> c.isDigit() }) height = it },
                        label = { Text("Tinggi (cm)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { if(it.all { c -> c.isDigit() }) weight = it },
                        label = { Text("Berat (kg)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Tambahkan ini juga
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. ACTIVITY LEVEL
                Text("Tingkat Aktivitas", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedActivityText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        activityOptions.forEach { (label, value) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedActivityText = label
                                    selectedActivityValue = value
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CARD ALERGI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Alergi Makanan", fontWeight = FontWeight.Bold, color = HijauNyam)
                Text("Pilih jika ada (bisa lebih dari satu)", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allergyOptions.forEach { allergy ->
                        FilterChip(
                            selected = selectedAllergies.contains(allergy),
                            onClick = {
                                if (selectedAllergies.contains(allergy)) selectedAllergies.remove(allergy)
                                else selectedAllergies.add(allergy)
                            },
                            label = { Text(allergy) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HijauNyam.copy(alpha = 0.1f),
                                selectedLabelColor = HijauNyam
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // TOMBOL SIMPAN
        Button(
            onClick = {
                if (name.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                    Toast.makeText(context, "Lengkapi data dulu ya!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                scope.launch {
                    val request = PhysicalDataRequest(
                        name = name,
                        birthdate = birthdate,
                        gender = gender,
                        height = height.toInt(),
                        weight = weight.toInt(),
                        activityLevel = selectedActivityValue,
                        allergies = selectedAllergies.toList()
                    )
                    val response = ApiClient.instance.updateProfile(uid, request)
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Profil Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                        onComplete()
                    } else {
                        Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HijauNyam),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Simpan Profil", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}