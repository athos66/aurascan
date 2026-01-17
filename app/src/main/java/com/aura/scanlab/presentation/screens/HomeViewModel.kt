package com.aura.scanlab.presentation.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.scanlab.data.local.AuraDatabase
import com.aura.scanlab.data.repository.AuraRepositoryImpl
import com.aura.scanlab.domain.model.HistoryItem
import com.aura.scanlab.domain.model.Ingredient
import com.aura.scanlab.domain.usecase.AnalyzeImageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(private val context: Context) : ViewModel() {
    private val database = AuraDatabase.getDatabase(context)
    private val repository = AuraRepositoryImpl(database.ingredientDao(), database.historyDao())
    private val analyzeUseCase = AnalyzeImageUseCase()

    // UI State
    var currentSessionIngredients by mutableStateOf<Set<Ingredient>>(emptySet())
        private set
    var matchedIngredients by mutableStateOf<List<Ingredient>>(emptyList())
        private set
    var recognizedText by mutableStateOf("")
    var latency by mutableStateOf(0L)
    var dbCount by mutableStateOf(0)
    var showScanComplete by mutableStateOf(false)
        private set
    
    // Internal state
    private var isScanningPaused = false
    var showResultPopup by mutableStateOf(false)
        private set
        
    // Mode State (0 = Food, 1 = Cosmetics)
    var selectedMode by mutableStateOf(0)
        private set

    // Gap detection
    private var gapCheckJob: Job? = null
    private var isInGap = true 
    
    companion object {
        private const val GAP_THRESHOLD_STRONG_MS = 1200L 
        private const val GAP_THRESHOLD_WEAK_MS = 500L    
    }

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Cache ingredients to avoid DB hit every frame
    private var cachedIngredients: List<Ingredient> = emptyList()

    init {
        viewModelScope.launch {
            // Observe ingredients continuously
            repository.getIngredients().collect { list ->
                cachedIngredients = list
                dbCount = list.size
                Log.d("HomeViewModel", "Ingredients cached: ${list.size}")
            }
        }
    }

    fun setMode(modeIndex: Int) {
        if (selectedMode != modeIndex) {
            selectedMode = modeIndex
            currentSessionIngredients = emptySet()
            matchedIngredients = emptyList()
            isInGap = true
            Log.d("HomeViewModel", "Switched mode to ${if(modeIndex == 0) "FOOD" else "COSMETICS"}")
        }
    }

    fun dismissPopup() {
        showResultPopup = false
        // Dismissing without saving = Discard current results
        currentSessionIngredients = emptySet()
        matchedIngredients = emptyList()
        isInGap = true 
    }
    
    fun saveAndDismiss() {
        viewModelScope.launch {
            saveToHistory()
            dismissPopup()
        }
    }

    private suspend fun saveToHistory() {
        if (currentSessionIngredients.isEmpty()) return
        Log.i("HomeViewModel", "Saving ${currentSessionIngredients.size} ingredients to History")
        repository.saveScan(HistoryItem(
            productName = if (selectedMode == 0) "Food Scan" else "Cosmetic Scan", // Ideally we'd have a product name detection, but for now this is the type
            timestamp = System.currentTimeMillis(),
            isClean = !hasHarmfulIngredient(),
            matchedIngredients = currentSessionIngredients.map { it.name }.sorted(),
            category = if (selectedMode == 0) "FOOD" else "COSMETICS"
        ))
    }

    private suspend fun handleSafeScanComplete() {
        if (currentSessionIngredients.isEmpty()) return
        Log.i("HomeViewModel", "Safe Scan Complete - Showing feedback")
        
        // Pause and show feedback
        isScanningPaused = true
        showScanComplete = true
        
        // Hold for 2 seconds
        delay(2000)
        
        // Reset
        showScanComplete = false
        isScanningPaused = false
        currentSessionIngredients = emptySet()
        matchedIngredients = emptyList()
        isInGap = true
    }

    fun hasHarmfulIngredient(): Boolean {
        return currentSessionIngredients.any { 
            it.hazardLevel == com.aura.scanlab.domain.model.HazardLevel.HIGH ||
            it.hazardLevel == com.aura.scanlab.domain.model.HazardLevel.MEDIUM
        }
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (e: Exception) {
            Log.w("HomeViewModel", "Haptic feedback failed", e)
        }
    }

    fun onTextDetected(lines: List<String>, lat: Long) {

        // 1. PAUSE scanning logic if popup is showing or scanning is paused
        if (showResultPopup || isScanningPaused) return

        latency = lat
        recognizedText = lines.joinToString(" ")

        if (lines.isEmpty()) {
            return
        }

        gapCheckJob?.cancel()

        if (isInGap) {
            Log.i("HomeViewModel", "=== NEW SCAN SESSION STARTED ===")
            currentSessionIngredients = emptySet()
            matchedIngredients = emptyList()
            isInGap = false
            showScanComplete = false 
        }

        viewModelScope.launch {
            // Use cached list instead of querying DB
            val allIngredients = cachedIngredients
            if (allIngredients.isEmpty()) {
                startGapCheck(GAP_THRESHOLD_WEAK_MS) 
                return@launch
            }

            // Filter ingredients based on selected mode
            val targetCategory = if (selectedMode == 0) "FOOD" else "COSMETICS"
            
            val filteredIngredients = allIngredients.filter { ingredient ->
                ingredient.categories.isEmpty() || ingredient.categories.contains(targetCategory)
            }

            val matches = analyzeUseCase.matchIngredients(lines, filteredIngredients)
            matchedIngredients = matches

            if (matches.isNotEmpty()) {
                val newIngredients = matches.toSet() - currentSessionIngredients
                if (newIngredients.isNotEmpty()) {
                    Log.i("HomeViewModel", "NEW ingredients: ${newIngredients.map { it.name }}")
                    currentSessionIngredients = currentSessionIngredients + newIngredients
                    triggerHaptic()
                }
                
                startGapCheck(GAP_THRESHOLD_STRONG_MS)
            } else {
                startGapCheck(GAP_THRESHOLD_WEAK_MS)
            }
        }
    }

    private fun startGapCheck(timeoutMs: Long) {
        gapCheckJob?.cancel()
        gapCheckJob = viewModelScope.launch {
            delay(timeoutMs)
            
            Log.i("HomeViewModel", "=== GAP DETECTED (${timeoutMs}ms) ===")
            
            if (currentSessionIngredients.isNotEmpty()) {
                if (hasHarmfulIngredient()) {
                    // Method A: Harmful -> Show Popup (Blocking, Requires Manual Save)
                    showResultPopup = true
                } else {
                    // Method B: Safe -> Show Completion message without blocking
                    handleSafeScanComplete()
                }
            } else {
                // Empty session, just reset
                isInGap = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gapCheckJob?.cancel()
    }
}
