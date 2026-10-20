package com.example.core.model

import java.util.Locale

enum class Language(val code: String, val displayName: String, val locale: Locale) {
    ENGLISH("en", "English", Locale.US),
    BENGALI("bn", "বাংলা (Bengali)", Locale("bn", "BD"));

    companion object {
        fun fromCode(code: String): Language {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
