package com.example.core.model

data class ChatMessage(
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val languageCode: String = "en",
    val suggestedAction: String? = null,
    val isActionExecuted: Boolean = false
)
