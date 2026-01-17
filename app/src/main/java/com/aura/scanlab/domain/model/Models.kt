package com.aura.scanlab.domain.model

data class Ingredient(
    val id: Int = 0,
    val name: String,
    val hazardLevel: HazardLevel,
    val description: String,
    val categories: List<String> = emptyList(),
    val functionalCategory: String = "",
    val localizedNames: Map<String, String> = emptyMap(),
    val localizedDescriptions: Map<String, String> = emptyMap()
)

enum class HazardLevel {
    HIGH, MEDIUM, LOW
}

data class HistoryItem(
    val id: Int = 0,
    val productName: String,
    val timestamp: Long,
    val isClean: Boolean,
    val matchedIngredients: List<String> = emptyList(),
    val category: String = "FOOD"
)
