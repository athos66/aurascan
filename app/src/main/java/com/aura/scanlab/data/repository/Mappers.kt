package com.aura.scanlab.data.repository

import com.aura.scanlab.data.local.HistoryEntity
import com.aura.scanlab.data.local.IngredientEntity
import com.aura.scanlab.domain.model.HazardLevel
import com.aura.scanlab.domain.model.HistoryItem
import com.aura.scanlab.domain.model.Ingredient
import org.json.JSONArray
import org.json.JSONObject

fun IngredientEntity.toDomain(): Ingredient {
    // Parse localized names
    val namesMap = mutableMapOf<String, String>()
    try {
        val namesJson = JSONObject(localizedNamesJson)
        namesJson.keys().forEach { key ->
            namesMap[key] = namesJson.getString(key)
        }
    } catch (_: Exception) {}

    // Parse localized descriptions
    val descriptionsMap = mutableMapOf<String, String>()
    try {
        val descriptionsJson = JSONObject(localizedDescriptionsJson)
        descriptionsJson.keys().forEach { key ->
            descriptionsMap[key] = descriptionsJson.getString(key)
        }
    } catch (_: Exception) {}

    return Ingredient(
        id = id,
        name = name,
        hazardLevel = HazardLevel.valueOf(hazardLevel),
        description = description,
        categories = if (categories.isNotEmpty()) categories.split(",").map { it.trim() } else emptyList(),
        functionalCategory = functionalCategory,
        localizedNames = namesMap,
        localizedDescriptions = descriptionsMap
    )
}

fun Ingredient.toEntity(): IngredientEntity {
    val namesJson = JSONObject(localizedNames).toString()
    val descriptionsJson = JSONObject(localizedDescriptions).toString()
    
    return IngredientEntity(
        id = id,
        name = name,
        hazardLevel = hazardLevel.name,
        description = description,
        categories = categories.joinToString(","),
        functionalCategory = functionalCategory,
        localizedNamesJson = namesJson,
        localizedDescriptionsJson = descriptionsJson
    )
}

fun HistoryEntity.toDomain(): HistoryItem {
    val jsonArray = JSONArray(matchedIngredientsJson)
    val ingredients = mutableListOf<String>()
    for (i in 0 until jsonArray.length()) {
        ingredients.add(jsonArray.getString(i))
    }
    return HistoryItem(
        id = id,
        productName = productName,
        timestamp = timestamp,
        isClean = isClean,
        matchedIngredients = ingredients,
        category = category
    )
}

fun HistoryItem.toEntity() = HistoryEntity(
    id = id,
    productName = productName,
    timestamp = timestamp,
    isClean = isClean,
    matchedIngredientsJson = JSONArray(matchedIngredients).toString(),
    category = category
)

