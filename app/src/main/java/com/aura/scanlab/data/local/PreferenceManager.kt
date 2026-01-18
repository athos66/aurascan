package com.aura.scanlab.data.local

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "aura_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_THEME = "app_theme"
        
        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        
        // Supported languages
        val SUPPORTED_LANGUAGES = listOf(
            LanguageOption("en", "English"),
            LanguageOption("el", "Ελληνικά")
        )
    }

    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    fun setOnboardingComplete(complete: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, complete).apply()
    }

    fun getLanguage(): String {
        val saved = prefs.getString(KEY_LANGUAGE, null)
        if (saved != null) return saved
        
        // Default to device language if supported, otherwise English
        val deviceLang = Locale.getDefault().language
        return if (SUPPORTED_LANGUAGES.any { it.code == deviceLang }) deviceLang else "en"
    }

    fun setLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getTheme(): String {
        return prefs.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    fun setTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
    }
}

data class LanguageOption(val code: String, val displayName: String)
