package com.aura.scanlab.domain.repository

import com.aura.scanlab.domain.model.HistoryItem
import com.aura.scanlab.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow

interface AuraRepository {
    fun getIngredients(): Flow<List<Ingredient>>
    fun getScanHistory(): Flow<List<HistoryItem>>
    suspend fun saveScan(item: HistoryItem)
    suspend fun searchIngredients(query: String): Flow<List<Ingredient>>
    suspend fun clearHistory()
    suspend fun deleteAllIngredients()
    suspend fun initialPopulate(ingredients: List<Ingredient>)
}
