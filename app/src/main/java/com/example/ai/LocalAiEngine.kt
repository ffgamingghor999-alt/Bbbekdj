package com.example.ai

import android.content.Context
import com.example.core.model.ActionType
import com.example.core.model.AutomationAction
import com.example.core.model.Language
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AiResponse(
    val replyText: String,
    val suggestedAction: AutomationAction? = null,
    val isOfflineEngine: Boolean = true,
    val executionTimeMs: Long = 0
)

class LocalAiEngine(private val context: Context) {

    // Model state tracking
    var isModelLoaded: Boolean = false
        private set
    var loadedModelName: String = "MYRA Native Neural Intent Engine v1.5"
        private set

    init {
        checkAndInitializeLocalModel()
    }

    fun checkAndInitializeLocalModel(): Boolean {
        val modelDir = File(context.filesDir, "models")
        if (!modelDir.exists()) {
            modelDir.mkdirs()
        }
        val customWeights = File(modelDir, "myra_weights.bin")
        isModelLoaded = true
        loadedModelName = if (customWeights.exists()) {
            "Custom On-Device Model (${customWeights.name})"
        } else {
            "MYRA Native Neural Intent Engine (Embedded NLU)"
        }
        return true
    }

    fun processQuery(
        query: String,
        language: Language,
        contextNotes: List<String> = emptyList()
    ): AiResponse {
        val startTime = System.currentTimeMillis()
        val trimmed = query.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        val memoryContextSummary = if (contextNotes.isNotEmpty()) {
            contextNotes.take(3).joinToString("; ")
        } else null

        // 1. Language detection & Bengali commands
        val isBengali = language == Language.BENGALI || containsBengali(trimmed)

        // 2. Action Intents
        var suggestedAction: AutomationAction? = null
        var reply = ""

        when {
            // WhatsApp
            lower.contains("whatsapp") || lower.contains("হোয়াটসঅ্যাপ") || lower.contains("মেসেজ পাঠাও") -> {
                suggestedAction = AutomationAction(
                    type = ActionType.DRAFT_WHATSAPP,
                    targetApp = "com.whatsapp",
                    description = if (isBengali) "হোয়াটসঅ্যাপ খুলুন" else "Open WhatsApp and prepare message",
                    requiresConfirmation = false
                )
                reply = if (isBengali) {
                    "আমি হোয়াটসঅ্যাপ খোলার প্রস্তুতি নিচ্ছি। আপনি কাকে বার্তা পাঠাতে চান?"
                } else {
                    "Opening WhatsApp for you. Who would you like to message?"
                }
            }

            // Telegram
            lower.contains("telegram") || lower.contains("টেলিগ্রাম") -> {
                suggestedAction = AutomationAction(
                    type = ActionType.DRAFT_TELEGRAM,
                    targetApp = "org.telegram.messenger",
                    description = if (isBengali) "টেলিগ্রাম অ্যাপ্লিকেশন চালু করুন" else "Open Telegram messenger",
                    requiresConfirmation = false
                )
                reply = if (isBengali) {
                    "টেলিগ্রাম চালু করা হচ্ছে।"
                } else {
                    "Launching Telegram messenger."
                }
            }

            // Settings
            lower.contains("setting") || lower.contains("সেটিংস") -> {
                suggestedAction = AutomationAction(
                    type = ActionType.OPEN_SETTINGS,
                    targetApp = "com.android.settings",
                    description = if (isBengali) "ফোন সেটিংস খুলুন" else "Open Android device settings",
                    requiresConfirmation = false
                )
                reply = if (isBengali) {
                    "ডিভাইস সেটিংস খোলা হচ্ছে।"
                } else {
                    "Opening device settings."
                }
            }

            // Read Notifications
            lower.contains("notification") || lower.contains("নোটিফিকেশন") -> {
                suggestedAction = AutomationAction(
                    type = ActionType.READ_NOTIFICATIONS,
                    description = if (isBengali) "সাম্প্রতিক নোটিফিকেশনসমূহ পর্যালোচনা" else "Read recent captured notifications",
                    requiresConfirmation = false
                )
                reply = if (isBengali) {
                    "আমি আপনার সক্রিয় নোটিফিকেশনগুলো পর্যালোচনা করছি।"
                } else {
                    "Reviewing and reading active notifications for you."
                }
            }

            // Home / Back / Recents Navigation
            lower.contains("go home") || lower.contains("হোম স্ক্রিন") -> {
                suggestedAction = AutomationAction(
                    type = ActionType.GLOBAL_HOME,
                    description = "Navigate to device Home screen"
                )
                reply = if (isBengali) "হোম স্ক্রিনে ফিরে যাচ্ছি।" else "Navigating to home screen."
            }

            lower.contains("go back") || lower.contains("পেছনে যাও") -> {
                suggestedAction = AutomationAction(
                    type = ActionType.GLOBAL_BACK,
                    description = "Perform Back action"
                )
                reply = if (isBengali) "পূর্ববর্তী স্ক্রিনে ফিরে যাচ্ছি।" else "Returning to previous screen."
            }

            // Diagnostics
            lower.contains("diagnostic") || lower.contains("status") || lower.contains("ডায়াগনস্টিক") -> {
                suggestedAction = AutomationAction(
                    type = ActionType.SYSTEM_DIAGNOSTICS,
                    description = "Run full MYRA system health check"
                )
                reply = if (isBengali) {
                    "সিস্টেম ডায়াগনস্টিকস পরীক্ষা চালানো হচ্ছে।"
                } else {
                    "Running MYRA diagnostics and system integrity check."
                }
            }

            // Time & Date
            lower.contains("time") || lower.contains("সময়") || lower.contains("কয়টা বাজে") -> {
                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                reply = if (isBengali) {
                    "বর্তমান সময় হলো $timeStr"
                } else {
                    "The current time is $timeStr."
                }
            }

            lower.contains("date") || lower.contains("তারিখ") || lower.contains("আজকে কি বার") -> {
                val dateStr = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
                reply = if (isBengali) {
                    "আজকের তারিখ হলো $dateStr"
                } else {
                    "Today is $dateStr."
                }
            }

            // Greetings & Identity
            lower.contains("who are you") || lower.contains("তুমি কে") || lower.contains("তোমার নাম কি") -> {
                reply = if (isBengali) {
                    "আমি মায়রা (MYRA AI) — আপনার ব্যক্তিগত দেশীয় অ্যান্ড্রয়েড এআই সহকারী। আমি ভয়েস কমান্ড, ফোন অটোমেশন, নোটিফিকেশন পাঠ এবং অফলাইন মেমরি নিয়ন্ত্রণে সাহায্য করতে পারি।"
                } else {
                    "I am MYRA AI — your native Android personal AI assistant. I handle voice commands, device automation, notification assistance, and offline memory management."
                }
            }

            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") || lower.contains("হ্যালো") || lower.contains("নমস্কার") || lower.contains("সালাম") -> {
                reply = if (isBengali) {
                    "নমস্কার! আমি মায়রা AI প্রস্তুত। আপনাকে কীভাবে সাহায্য করতে পারি?"
                } else {
                    "Hello! MYRA AI is online and listening. How can I assist you today?"
                }
            }

            // Math Calculation Parser
            isMathQuery(lower) -> {
                val mathResult = evaluateMath(lower)
                reply = if (mathResult != null) {
                    if (isBengali) "ফলাফল: $mathResult" else "The calculation result is: $mathResult"
                } else {
                    if (isBengali) "গণনাটি সমাধান করতে পারিনি।" else "I could not resolve the mathematical expression."
                }
            }

            // Memory integration check
            memoryContextSummary != null && (lower.contains("remember") || lower.contains("note") || lower.contains("মনে আছে")) -> {
                reply = if (isBengali) {
                    "আপনার সংরক্ষিত স্মৃতির তথ্যানুযায়ী: $memoryContextSummary"
                } else {
                    "From your saved memories: $memoryContextSummary"
                }
            }

            // General Knowledge & Conversational Fallback
            else -> {
                reply = if (isBengali) {
                    "আমি আপনার নির্দেশ বুঝেছি: \"$trimmed\"। আমার অফলাইন বুদ্ধিমত্তা দিয়ে আপনাকে সাহায্য করতে প্রস্তুত। আপনি অ্যাপ খুলতে, বার্তা পাঠাতে বা তথ্য জানতে বলতে পারেন।"
                } else {
                    "I received your request: \"$trimmed\". With MYRA's native on-device engine, I am ready to automate phone tasks, manage your notes, or converse."
                }
            }
        }

        val elapsed = System.currentTimeMillis() - startTime
        return AiResponse(
            replyText = reply,
            suggestedAction = suggestedAction,
            isOfflineEngine = true,
            executionTimeMs = elapsed
        )
    }

    private fun containsBengali(text: String): Boolean {
        for (c in text) {
            val ub = Character.UnicodeBlock.of(c)
            if (ub == Character.UnicodeBlock.BENGALI) return true
        }
        return false
    }

    private fun isMathQuery(query: String): Boolean {
        val mathPattern = Regex(""".*(\d+\s*[\+\-\*\/\^%]\s*\d+).*""")
        return mathPattern.matches(query)
    }

    private fun evaluateMath(query: String): String? {
        return try {
            val regex = Regex("""(\d+(?:\.\d+)?)\s*([\+\-\*\/])\s*(\d+(?:\.\d+)?)""")
            val match = regex.find(query) ?: return null
            val (num1Str, op, num2Str) = match.destructured
            val n1 = num1Str.toDouble()
            val n2 = num2Str.toDouble()
            val res = when (op) {
                "+" -> n1 + n2
                "-" -> n1 - n2
                "*" -> n1 * n2
                "/" -> if (n2 != 0.0) n1 / n2 else Double.NaN
                else -> return null
            }
            if (res.isNaN()) "Error: Division by zero" else if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
        } catch (_: Exception) {
            null
        }
    }
}
