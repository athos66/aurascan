package com.aura.scanlab.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val hazardLevel: String, // Stored as String for Room simplicity
    val description: String,
    val categories: String = "", // Comma-separated or JSON
    val functionalCategory: String = ""
)

@Entity(tableName = "scan_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val timestamp: Long,
    val isClean: Boolean,
    val matchedIngredientsJson: String, // Serialized list
    val category: String = "FOOD"
)
