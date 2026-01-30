package com.project.nyam.presentation.dashboard.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.nyam.data.model.ChatMessage
import com.project.nyam.data.model.ChatRequest
import com.project.nyam.data.remote.ApiClient
import kotlinx.coroutines.launch

@Composable
fun ChatTab(getToken: suspend () -> String?) {
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf(ChatMessage("Halo! Aku NYAM Bot. Ada yang bisa aku bantu seputar gizimu hari ini?", false)) }
    var isSending by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Area Chat
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(chatMessages) { msg ->
                ChatBubble(msg)
            }
            if (isSending) {
                item {
                    Text("NYAM Bot sedang mengetik...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        // Input Area
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tanya NYAM Bot...") },
                shape = RoundedCornerShape(24.dp),
                enabled = !isSending,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4CAF50))
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val userMsg = inputText
                        chatMessages.add(ChatMessage(userMsg, true))
                        inputText = ""
                        isSending = true

                        scope.launch {
                            try {
                                val token = getToken()
                                val response = ApiClient.instance.sendMessage("Bearer $token", ChatRequest(userMsg))
                                if (response.isSuccessful) {
                                    response.body()?.data?.reply?.let {
                                        chatMessages.add(ChatMessage(it, false))
                                    }
                                }
                            } catch (e: Exception) {
                                chatMessages.add(ChatMessage("Maaf, koneksi terputus. Coba lagi ya!", false))
                            } finally {
                                isSending = false
                                listState.animateScrollToItem(chatMessages.size - 1)
                            }
                        }
                    }
                },
                enabled = !isSending && inputText.isNotBlank(),
                modifier = Modifier.background(Color(0xFF4CAF50), CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Kirim", tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (message.isUser) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            ),
            tonalElevation = 2.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 14.sp,
                color = if (message.isUser) Color(0xFF1B5E20) else Color.Black
            )
        }
    }
}