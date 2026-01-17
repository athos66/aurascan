package com.aura.scanlab

import android.content.Context
import android.util.Log
import org.json.JSONObject
import kotlin.math.min

object IngredientMatcher {
    /**
     * Loads the list of chemical ingredients from a local JSON asset.
     * TODO: Implement getting the list from a Remote Config JSON value and default to local JSON on error.
     */
    fun getIngredients(context: Context): List<String> {
        return try {
            val jsonString = context.assets.open("ingredients.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("ingredients")
            List(jsonArray.length()) { i -> jsonArray.getString(i) }
        } catch (e: Exception) {
            Log.e("IngredientMatcher", "Error loading ingredients from assets", e)
            // Fallback to a hardcoded minimal list if JSON loading fails completely
            listOf("Titanium Dioxide", "Red 40", "Parabens", "Aspartame", "BHT")
        }
    }

    fun fuzzyMatch(input: String, target: String): Boolean {
        val inputLower = input.lowercase()
        val targetLower = target.lowercase()
        
        // Exact match check first
        if (inputLower == targetLower) return true

        // For very short strings (e.g., BHA, BHT), we require an exact match or 
        // a very specific word boundary match to avoid false positives from noise.
        if (targetLower.length < 4) {
            // Check if the input contains the target as a standalone word
            val pattern = Regex("\\b${Regex.escape(targetLower)}\\b")
            return pattern.find(inputLower) != null
        }
        
        // Dynamic Levenshtein threshold based on string length
        // - Length 4-7: 1 error allowed
        // - Length > 7: 2 errors allowed
        val threshold = when {
            targetLower.length >= 8 -> 2
            targetLower.length >= 4 -> 1
            else -> 0
        }
        
        // Check if target is contained directly (common Case)
        if (inputLower.contains(targetLower)) return true
        
        // Fallback to Levenshtein distance for fuzzy matching
        return levenshteinDistance(inputLower, targetLower) <= threshold
    }

    fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }
}
