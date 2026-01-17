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
import com.aura.scanlab.data.repository.AuraRepositoryImpl
import com.aura.scanlab.domain.model.HistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class HistoryFilter {
    ALL, FOOD, COSMETICS
}

class HistoryViewModel(context: Context) : ViewModel() {
    private val database = AuraDatabase.getDatabase(context)
    private val repository = AuraRepositoryImpl(database.ingredientDao(), database.historyDao())

    var searchQuery by mutableStateOf("")
    var selectedFilter by mutableStateOf(HistoryFilter.ALL)

    val historyItems: StateFlow<Map<String, List<HistoryItem>>> = combine(
        repository.getScanHistory(),
        snapshotFlow { searchQuery },
        snapshotFlow { selectedFilter }
    ) { items, query, filter ->
        items.filter { item ->
            // Search filter
            val matchesSearch = item.productName.contains(query, ignoreCase = true) ||
                    item.matchedIngredients.any { it.contains(query, ignoreCase = true) }
            
            // Category filter
            val matchesFilter = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.FOOD -> item.category == "FOOD"
                HistoryFilter.COSMETICS -> item.category == "COSMETICS"
            }
            
            matchesSearch && matchesFilter
        }.groupBy { item ->
            getRelativeDate(item.timestamp)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    private fun getRelativeDate(timestamp: Long): String {
        val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()
        
        return when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
        }
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
