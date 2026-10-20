package com.example.core.model

enum class ActionType {
    OPEN_APP,
    OPEN_SETTINGS,
    DRAFT_WHATSAPP,
    DRAFT_TELEGRAM,
    READ_NOTIFICATIONS,
    GLOBAL_HOME,
    GLOBAL_BACK,
    GLOBAL_RECENTS,
    SCROLL_DOWN,
    SCROLL_UP,
    SYSTEM_DIAGNOSTICS,
    UNKNOWN
}

data class AutomationAction(
    val type: ActionType,
    val targetApp: String? = null,
    val payload: String? = null,
    val description: String,
    val requiresConfirmation: Boolean = false
)
