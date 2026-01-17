package com.aura.scanlab.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.scanlab.data.local.AuraDatabase
import com.aura.scanlab.data.repository.AuraRepositoryImpl
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
            // If count is low (e.g., still using the old 8-item list), force update
            if (count < 15) {
                Log.d("AuraViewModel", "Populating database from JSON (Current count: $count)")
                val ingredients = loadIngredientsFromAssets(context)
                repository.initialPopulate(ingredients)
            } else {
                Log.d("AuraViewModel", "Database already populated ($count items)")
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

                list.add(Ingredient(
                    name = obj.getString("name"),
                    hazardLevel = com.aura.scanlab.domain.model.HazardLevel.valueOf(obj.getString("hazardLevel")),
                    description = obj.getString("description"),
                    categories = categoriesList,
                    functionalCategory = if (obj.has("functionalCategory")) obj.getString("functionalCategory") else ""
                ))
            }
            list
        } catch (e: Exception) {
            Log.e("AuraViewModel", "Error loading ingredients", e)
            emptyList()
        }
    }
}
