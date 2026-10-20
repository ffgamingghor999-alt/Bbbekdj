package com.example.core.model

enum class AiProviderType(val displayName: String, val requiresKey: Boolean) {
    LOCAL_OFFLINE("MYRA Local Engine (Offline)", false),
    GEMINI_ONLINE("Google Gemini AI (Online)", true),
    OPENROUTER_ONLINE("OpenRouter AI (Online)", true)
}
