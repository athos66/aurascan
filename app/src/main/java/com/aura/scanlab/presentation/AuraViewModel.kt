package com.aura.scanlab.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.scanlab.data.local.AuraDatabase
import com.aura.scanlab.data.repository.AuraRepositoryImpl
import com.aura.scanlab.data.repository.toDomain
import com.aura.scanlab.domain.model.Ingredient
import kotlinx.coroutines.launch
import org.json.JSONArray

class AuraViewModel(context: Context) : ViewModel() {
    private val database = AuraDatabase.getDatabase(context)
    private val repository = AuraRepositoryImpl(database.ingredientDao(), database.historyDao())

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    init {
        viewModelScope.launch {
            val count = database.ingredientDao().getIngredientCount()
            // Fetch one to check if it has translations
            val first = database.ingredientDao().getAllIngredientsOnce().firstOrNull()
            // Check if el translation is missing or names map is actually empty
            val hasTranslations = first?.toDomain()?.localizedNames?.containsKey("el") == true

            // Force update if count is low OR if existing data lacks translations
            if (count < 20 || !hasTranslations) {
                Log.d("AuraViewModel", "Refreshing database (Count: $count, Translations: $hasTranslations)")
                repository.deleteAllIngredients()
                val ingredients = loadIngredientsFromAssets(context)
                repository.initialPopulate(ingredients)
                Log.d("AuraViewModel", "Refresh complete: ${ingredients.size} items")
            } else {
                Log.d("AuraViewModel", "Database stable ($count items, localized)")
            }
        }
    }

    private fun loadIngredientsFromAssets(context: Context): List<Ingredient> {
        return try {
            val json = context.assets.open("ingredients_full.json").bufferedReader().use { it.readText() }
            val array = JSONArray(json)
            val list = mutableListOf<Ingredient>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val categoriesList = mutableListOf<String>()
                if (obj.has("categories")) {
                    val catArray = obj.getJSONArray("categories")
                    for (j in 0 until catArray.length()) {
                        categoriesList.add(catArray.getString(j))
                    }
                }

                val localizedNamesMap = mutableMapOf<String, String>()
                if (obj.has("localizedNames")) {
                    val namesObj = obj.getJSONObject("localizedNames")
                    namesObj.keys().forEach { key ->
                        localizedNamesMap[key] = namesObj.getString(key)
                    }
                }

                val localizedDescriptionsMap = mutableMapOf<String, String>()
                if (obj.has("localizedDescriptions")) {
                    val descObj = obj.getJSONObject("localizedDescriptions")
                    descObj.keys().forEach { key ->
                        localizedDescriptionsMap[key] = descObj.getString(key)
                    }
                }

                list.add(Ingredient(
                    name = obj.getString("name"),
                    hazardLevel = com.aura.scanlab.domain.model.HazardLevel.valueOf(obj.getString("hazardLevel")),
                    description = obj.getString("description"),
                    categories = categoriesList,
                    functionalCategory = if (obj.has("functionalCategory")) obj.getString("functionalCategory") else "",
                    localizedNames = localizedNamesMap,
                    localizedDescriptions = localizedDescriptionsMap
                ))
            }
            list
        } catch (e: Exception) {
            Log.e("AuraViewModel", "Error loading ingredients", e)
            emptyList()
        }
    }
}
