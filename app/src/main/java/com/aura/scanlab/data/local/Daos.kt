package com.aura.scanlab.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :query || '%'")
    fun searchIngredients(query: String): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientEntity>)

    @Query("SELECT COUNT(*) FROM ingredients")
    suspend fun getIngredientCount(): Int
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<HistoryEntity>>

    @Insert
    suspend fun insertHistory(item: HistoryEntity)

    @Query("DELETE FROM scan_history")
    suspend fun clearHistory()
}
