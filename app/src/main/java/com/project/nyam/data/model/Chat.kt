package com.project.nyam.data.model

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val status: String,
    val data: ChatReply
)

data class ChatReply(
    val reply: String
)

// Model untuk tampilan UI Chat
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)