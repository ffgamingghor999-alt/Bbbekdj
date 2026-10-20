package com.example.ai

import com.example.core.model.AiProviderType
import com.example.core.model.Language
import com.example.data.local.repository.MyraRepository
import kotlinx.coroutines.flow.firstOrNull

class AiBrain(
    private val localAiEngine: LocalAiEngine,
    private val onlineAiEngine: OnlineAiEngine,
    private val repository: MyraRepository
) {
    suspend fun processUserPrompt(
        prompt: String,
        provider: AiProviderType,
        apiKey: String,
        language: Language
    ): AiResponse {
        val recentNotes = repository.allNotes.firstOrNull()?.take(5)?.map { "${it.title}: ${it.content}" } ?: emptyList()

        // If local offline engine is selected or no online API key is configured, execute offline engine
        if (provider == AiProviderType.LOCAL_OFFLINE || apiKey.isBlank()) {
            return localAiEngine.processQuery(prompt, language, recentNotes)
        }

        // Attempt online provider
        val onlineResult = onlineAiEngine.generateResponse(
            prompt = prompt,
            provider = provider,
            apiKey = apiKey,
            language = language,
            contextNotes = recentNotes
        )

        return if (onlineResult.isSuccess) {
            AiResponse(
                replyText = onlineResult.getOrThrow(),
                suggestedAction = null,
                isOfflineEngine = false,
                executionTimeMs = 0
            )
        } else {
            // Graceful fallback to local engine on network failure or rate limit
            val fallback = localAiEngine.processQuery(prompt, language, recentNotes)
            AiResponse(
                replyText = "${fallback.replyText}\n\n[Note: Online AI request failed (${onlineResult.exceptionOrNull()?.message ?: "Network error"}). Fell back to native offline engine.]",
                suggestedAction = fallback.suggestedAction,
                isOfflineEngine = true,
                executionTimeMs = fallback.executionTimeMs
            )
        }
    }

    fun isLocalModelLoaded(): Boolean = localAiEngine.isModelLoaded
    fun getLocalModelName(): String = localAiEngine.loadedModelName
}
