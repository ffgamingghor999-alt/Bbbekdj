package com.example.ai

import com.example.core.model.AiProviderType
import com.example.core.model.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OnlineAiEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        prompt: String,
        provider: AiProviderType,
        apiKey: String,
        language: Language,
        contextNotes: List<String> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("No API key provided for ${provider.displayName}")
            )
        }

        try {
            val systemContext = buildString {
                append("You are MYRA AI, a native Android personal assistant. ")
                append("Provide concise, polite, helpful responses formatted cleanly. ")
                if (language == Language.BENGALI) {
                    append("Reply in Bengali (বাংলা) unless requested otherwise. ")
                } else {
                    append("Reply in English. ")
                }
                if (contextNotes.isNotEmpty()) {
                    append("\nUser saved memory context: ")
                    append(contextNotes.take(3).joinToString("; "))
                }
            }

            when (provider) {
                AiProviderType.GEMINI_ONLINE -> callGemini(prompt, systemContext, apiKey)
                AiProviderType.OPENROUTER_ONLINE -> callOpenRouter(prompt, systemContext, apiKey)
                AiProviderType.LOCAL_OFFLINE -> Result.failure(IllegalStateException("Local engine selected"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun callGemini(prompt: String, systemContext: String, apiKey: String): Result<String> {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()

        val jsonPayload = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "$systemContext\n\nUser Question: $prompt")
                        })
                    })
                })
            }
            put("contents", contentsArray)
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                return Result.failure(
                    RuntimeException("Gemini API Error (HTTP ${response.code}): $errorBody")
                )
            }

            val body = response.body?.string() ?: return Result.failure(RuntimeException("Empty response"))
            val json = JSONObject(body)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val reply = parts.getJSONObject(0).optString("text")
                    return Result.success(reply.trim())
                }
            }
            return Result.failure(RuntimeException("Could not parse response text from Gemini API"))
        }
    }

    private fun callOpenRouter(prompt: String, systemContext: String, apiKey: String): Result<String> {
        val url = "https://openrouter.ai/api/v1/chat/completions"
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()

        val jsonPayload = JSONObject().apply {
            put("model", "meta-llama/llama-3-8b-instruct:free")
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemContext)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }
            put("messages", messages)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("HTTP-Referer", "https://myra.ai.assistant")
            .addHeader("X-Title", "MYRA AI Assistant")
            .post(jsonPayload.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                return Result.failure(
                    RuntimeException("OpenRouter API Error (HTTP ${response.code}): $errorBody")
                )
            }

            val body = response.body?.string() ?: return Result.failure(RuntimeException("Empty response"))
            val json = JSONObject(body)
            val choices = json.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val message = choices.getJSONObject(0).optJSONObject("message")
                val content = message?.optString("content")
                if (!content.isNullOrBlank()) {
                    return Result.success(content.trim())
                }
            }
            return Result.failure(RuntimeException("Could not parse response text from OpenRouter"))
        }
    }
}
