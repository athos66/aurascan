package com.aura.scanlab.data.repository

import com.aura.scanlab.data.local.HistoryDao
import com.aura.scanlab.data.local.IngredientDao
import com.aura.scanlab.domain.model.HistoryItem
import com.aura.scanlab.domain.model.Ingredient
import com.aura.scanlab.domain.repository.AuraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuraRepositoryImpl(
    private val ingredientDao: IngredientDao,
    private val historyDao: HistoryDao
) : AuraRepository {

    override fun getIngredients(): Flow<List<Ingredient>> {
        return ingredientDao.getAllIngredients().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getScanHistory(): Flow<List<HistoryItem>> {
        return historyDao.getHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveScan(item: HistoryItem) {
        historyDao.insertHistory(item.toEntity())
    }

    override suspend fun searchIngredients(query: String): Flow<List<Ingredient>> {
        return ingredientDao.searchIngredients(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun clearHistory() {
        historyDao.clearHistory()
    }
    
    suspend fun initialPopulate(ingredients: List<Ingredient>) {
        ingredientDao.insertAll(ingredients.map { it.toEntity() })
    }

    // Helper to check if database is empty
    suspend fun isDatabaseEmpty(): Boolean {
        return ingredientDao.getIngredientCount() == 0
    }
}
