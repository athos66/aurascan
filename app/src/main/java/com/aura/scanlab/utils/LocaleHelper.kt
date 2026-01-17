package com.aura.scanlab.utils

import android.content.Context
import android.content.res.Configuration
import com.aura.scanlab.data.local.PreferenceManager
import java.util.Locale

object LocaleHelper {
    
    /**
     * Updates the app's locale based on saved preference
     */
    fun setLocale(context: Context): Context {
        val preferenceManager = PreferenceManager(context)
        val language = preferenceManager.getLanguage()
        return updateResources(context, language)
    }

    /**
     * Updates the app's locale to the specified language
     */
    fun setLocale(context: Context, language: String): Context {
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    /**
     * Gets localized text from a map with fallback to default
     */
    fun getLocalizedText(
        localizedMap: Map<String, String>,
        defaultText: String,
        languageCode: String
    ): String {
        return localizedMap[languageCode] ?: defaultText
    }
}
