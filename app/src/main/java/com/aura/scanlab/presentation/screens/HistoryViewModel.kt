package com.aura.scanlab.presentation.screens

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.scanlab.data.local.AuraDatabase
import com.aura.scanlab.data.local.PreferenceManager
import com.aura.scanlab.data.repository.AuraRepositoryImpl
import com.aura.scanlab.domain.model.HistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.text.format.DateUtils
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HistoryFilter {
    ALL, FOOD, COSMETICS
}

class HistoryViewModel(private val context: Context) : ViewModel() {
    private val database = AuraDatabase.getDatabase(context)
    private val repository = AuraRepositoryImpl(database.ingredientDao(), database.historyDao())

    var searchQuery by mutableStateOf("")
    var selectedFilter by mutableStateOf(HistoryFilter.ALL)

    private val allIngredients = repository.getIngredients().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val historyItems: StateFlow<Map<String, List<HistoryItem>>> = combine(
        repository.getScanHistory(),
        snapshotFlow { searchQuery },
        snapshotFlow { selectedFilter },
        allIngredients
    ) { items, query, filter, ingredients ->
        val lookup = ingredients.associateBy { it.name }
        val currentLang = PreferenceManager(context).getLanguage()

        items.map { item ->
            // Localize ingredients for the UI
            val localizedIngredients = item.matchedIngredients.map { englishName ->
                lookup[englishName]?.localizedNames?.get(currentLang) ?: englishName
            }
            
            // Map the item to its localized version for display and search
            val localizedProductName = when (item.productName) {
                "Food Scan" -> context.getString(com.aura.scanlab.R.string.default_food_scan_name)
                "Cosmetic Scan" -> context.getString(com.aura.scanlab.R.string.default_cosmetic_scan_name)
                else -> item.productName
            }

            // We store the original matchedIngredients (English) to allow searching against both
            Triple(item, localizedProductName, localizedIngredients)
        }.filter { (originalItem, localizedName, localizedIngs) ->
            // Category filter
            val matchesFilter = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.FOOD -> originalItem.category == "FOOD"
                HistoryFilter.COSMETICS -> originalItem.category == "COSMETICS"
            }
            if (!matchesFilter) return@filter false

            // Search filter works on localized name, English name, localized ingredients, and English ingredients
            val matchesSearch = if (query.isBlank()) true else {
                localizedName.contains(query, ignoreCase = true) ||
                        originalItem.productName.contains(query, ignoreCase = true) ||
                        localizedIngs.any { it.contains(query, ignoreCase = true) } ||
                        originalItem.matchedIngredients.any { it.contains(query, ignoreCase = true) }
            }
            
            matchesSearch
        }.map { (originalItem, localizedName, localizedIngs) ->
            originalItem.copy(
                productName = localizedName,
                matchedIngredients = localizedIngs
            )
        }.groupBy { item ->
            getRelativeDate(item.timestamp)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    private fun getRelativeDate(timestamp: Long): String {
        return when {
            DateUtils.isToday(timestamp) -> context.getString(com.aura.scanlab.R.string.group_today)
            isYesterday(timestamp) -> context.getString(com.aura.scanlab.R.string.group_yesterday)
            else -> {
                val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                DateUtils.formatDateTime(context, timestamp, flags)
            }
        }
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val date = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        return date.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
               date.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
    }

    fun setFilter(filter: HistoryFilter) {
        selectedFilter = filter
        Log.d("HistoryViewModel", "Filter set to: $filter")
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
