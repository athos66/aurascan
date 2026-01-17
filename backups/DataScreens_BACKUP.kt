package com.aura.scanlab.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aura.scanlab.BuildConfig
import com.aura.scanlab.data.local.AuraDatabase
import com.aura.scanlab.data.repository.AuraRepositoryImpl
import com.aura.scanlab.domain.model.HistoryItem
import com.aura.scanlab.domain.model.Ingredient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val database = remember { AuraDatabase.getDatabase(context) }
    val repository = remember { AuraRepositoryImpl(database.ingredientDao(), database.historyDao()) }
    var historyItems by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Collect the Flow manually to ensure updates
    LaunchedEffect(Unit) {
        repository.getScanHistory().collectLatest { items ->
            Log.d("HistoryScreen", "Flow emitted ${items.size} items")
            historyItems = items
        }
    }

    HistoryScreenContent(
        historyItems = historyItems,
        onClearHistory = {
            scope.launch {
                Log.d("HistoryScreen", "Clearing history...")
                repository.clearHistory()
                Log.d("HistoryScreen", "Clear completed")
            }
        }
    )
}

@Composable
fun HistoryScreenContent(
    historyItems: List<HistoryItem>,
    onClearHistory: () -> Unit = {}
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Scan History", style = MaterialTheme.typography.headlineMedium)
            
            if (BuildConfig.DEBUG && historyItems.isNotEmpty()) {
                TextButton(onClick = onClearHistory) {
                    Text("CLEAR (${historyItems.size})")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        
        if (historyItems.isEmpty()) {
            Text("No scans yet. Start identifying ingredients!", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(historyItems, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.productName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Found: ${item.matchedIngredients.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    MaterialTheme {
        HistoryScreenContent(emptyList())
    }
}

@Composable
fun EncyclopediaScreen() {
    val context = LocalContext.current
    val database = remember { AuraDatabase.getDatabase(context) }
    val repository = remember { AuraRepositoryImpl(database.ingredientDao(), database.historyDao()) }
    var ingredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }

    LaunchedEffect(Unit) {
        repository.getIngredients().collectLatest { items ->
            ingredients = items
        }
    }
    
    EncyclopediaScreenContent(ingredients)
}

@Composable
fun EncyclopediaScreenContent(ingredients: List<Ingredient>) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredIngredients = ingredients.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Encyclopedia", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search ingredients...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(filteredIngredients, key = { it.id }) { ingredient ->
                ListItem(
                    headlineContent = { Text(ingredient.name) },
                    supportingContent = { Text(ingredient.description) },
                    trailingContent = { Text(ingredient.hazardLevel.name) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EncyclopediaScreenPreview() {
    MaterialTheme {
        EncyclopediaScreenContent(emptyList())
    }
}
