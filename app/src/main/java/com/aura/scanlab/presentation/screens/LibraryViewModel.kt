package com.aura.scanlab.presentation.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.scanlab.data.local.AuraDatabase
import com.aura.scanlab.data.repository.AuraRepositoryImpl
import com.aura.scanlab.domain.model.Ingredient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LibraryViewModel(context: Context) : ViewModel() {
    private val database = AuraDatabase.getDatabase(context)
    private val repository = AuraRepositoryImpl(database.ingredientDao(), database.historyDao())

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("All")

    val ingredients: StateFlow<Map<String, List<Ingredient>>> = combine(
        repository.getIngredients(),
        snapshotFlow { searchQuery },
        snapshotFlow { selectedCategory }
    ) { list, query, category ->
        val filtered = list.filter { ingredient ->
            val matchesSearch = if (query.isBlank()) true else {
                ingredient.name.contains(query, ignoreCase = true) || 
                ingredient.description.contains(query, ignoreCase = true)
            }
            
            val matchesCategory = if (category == "All") true else {
                ingredient.functionalCategory.equals(category, ignoreCase = true)
            }
            
            matchesSearch && matchesCategory
        }

        filtered.sortedBy { it.name }
            .groupBy { it.name.take(1).uppercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun onCategoryChange(category: String) {
        selectedCategory = category
    }

    fun getFunctionalCategories(allIngredients: List<Ingredient>): List<String> {
        return listOf("All") + allIngredients.map { it.functionalCategory }
            .filter { it.isNotBlank() }
            .distinct()
            .sortedBy { it }
    }
}
