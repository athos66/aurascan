package com.aura.scanlab.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.aura.scanlab.R
import com.aura.scanlab.domain.model.HistoryItem
import com.aura.scanlab.domain.model.Ingredient
import com.aura.scanlab.presentation.theme.AlertRed
import com.aura.scanlab.presentation.theme.SuccessGreen
import android.text.format.DateFormat
import com.aura.scanlab.data.local.PreferenceManager
import java.util.Date

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val viewModel = remember { HistoryViewModel(context) }
    val historyGroups by viewModel.historyItems.collectAsState()

    HistoryScreenContent(
        searchQuery = viewModel.searchQuery,
        onSearchChange = { viewModel.onSearchQueryChange(it) },
        selectedFilter = viewModel.selectedFilter,
        onFilterChange = { viewModel.setFilter(it) },
        historyGroups = historyGroups,
        onClearHistory = { viewModel.clearHistory() }
    )
}

@Composable
fun HistoryScreenContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: HistoryFilter,
    onFilterChange: (HistoryFilter) -> Unit,
    historyGroups: Map<String, List<HistoryItem>>,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.history_title),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            

        }

        // Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1A1A1A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF4DBB5F), // Vibrant green from screenshot
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text(stringResource(R.string.search_history_placeholder), color = Color.Gray, fontSize = 16.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF00E676),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HistoryFilter.entries.toTypedArray()) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF00E676) else Color.Transparent)
                        .then(
                            if (!isSelected) Modifier.background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ).padding(1.dp).background(Color.Black, RoundedCornerShape(12.dp))
                             .then(Modifier.background(Color.Transparent).padding(1.dp)) // border effect
                            else Modifier
                        )
                        .clickable { onFilterChange(filter) }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    // Manual border for unselected
                    if (!isSelected) {
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            color = Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626))
                        ) {}
                    }
                    Text(
                        text = when(filter) {
                            HistoryFilter.ALL -> stringResource(R.string.filter_all)
                            HistoryFilter.FOOD -> stringResource(R.string.category_food)
                            HistoryFilter.COSMETICS -> stringResource(R.string.category_cosmetics)
                        }.uppercase(),
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Grouped List
        if (historyGroups.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.history_empty), color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                historyGroups.forEach { (date, items) ->
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = date,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                             Text(
                                 text = stringResource(R.string.items_count, items.size),
                                 color = Color.Gray,
                                 fontSize = 12.sp,
                                 fontWeight = FontWeight.Bold
                             )
                        }
                    }
                    items(items, key = { it.id }) { item ->
                        HistoryCard(item)
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    val dateText = DateFormat.getTimeFormat(LocalContext.current).format(Date(item.timestamp))
    
    val iconColor = if (item.isClean) Color(0xFF1E3A1E) else Color(0xFF3A1E1E)
    val iconTint = if (item.isClean) Color(0xFF4DBB5F) else Color(0xFFFF5252)
    val icon = when {
        !item.isClean -> Icons.Default.Warning
        item.productName.contains("Yogurt", ignoreCase = true) -> Icons.Default.Eco
        item.productName.contains("Bar", ignoreCase = true) -> Icons.Default.Restaurant
        else -> Icons.Default.Eco
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF141414)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.productName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        dateText,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                
                // Matched Ingredients (if flagged)
                if (!item.isClean && item.matchedIngredients.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.found_ingredients, item.matchedIngredients.take(3).joinToString(", "), if(item.matchedIngredients.size > 3) "..." else ""),
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = (if (item.isClean) Color(0xFF143014) else Color(0xFF301414))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (item.isClean) Color(0xFF00E676) else Color(0xFFFF5252))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (item.isClean) stringResource(R.string.status_clean) else stringResource(R.string.status_flagged),
                            color = if (item.isClean) Color(0xFF00E676) else Color(0xFFFF5252),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Chevron
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EncyclopediaScreen() {
    val context = LocalContext.current
    val viewModel = remember { LibraryViewModel(context) }
    val ingredientsByLetter by viewModel.ingredients.collectAsState()
    
    // We need all ingredients to extract unique categories for the filter bar
    // Since LibraryViewModel.ingredients is a Map, we merge them for the filter logic
    val allIngredients = ingredientsByLetter.values.flatten()
    val categories = viewModel.getFunctionalCategories(allIngredients)

    EncyclopediaScreenContent(
        searchQuery = viewModel.searchQuery,
        onSearchChange = { viewModel.onSearchQueryChange(it) },
        selectedCategory = viewModel.selectedCategory,
        onCategoryChange = { viewModel.onCategoryChange(it) },
        categories = categories,
        ingredientsByLetter = ingredientsByLetter
    )
}

@Composable
fun EncyclopediaScreenContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    categories: List<String>,
    ingredientsByLetter: Map<String, List<Ingredient>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.library_title),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column {
            // SEARCH BAR
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1A1A)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF4DBB5F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text(stringResource(R.string.search_ingredients), color = Color.Gray, fontSize = 16.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF00E676),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FILTERS
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    val displayName = localizeFunctionalCategory(category)
                    
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onCategoryChange(category) },
                        color = if (isSelected) Color(0xFF00E676) else Color(0xFF1A1A1A),
                        shape = RoundedCornerShape(20.dp),
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)) else null
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displayName,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (category != "All") {
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // GROUPED LIST
            if (ingredientsByLetter.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (searchQuery.isEmpty()) stringResource(R.string.loading) else stringResource(R.string.no_scans_found), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    ingredientsByLetter.toSortedMap().forEach { (letter, items) ->
                        item {
                            Column {
                                Text(
                                    text = letter,
                                    color = Color(0xFF00E676),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                HorizontalDivider(color = Color(0xFF1A1A1A), thickness = 1.dp)
                            }
                        }
                        items(items, key = { it.id }) { ingredient ->
                            val currentLang = PreferenceManager(LocalContext.current).getLanguage()
                            EncyclopediaCard(ingredient, currentLang)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EncyclopediaCard(ingredient: Ingredient, currentLang: String) {
    val hazardInfo = when (ingredient.hazardLevel) {
        com.aura.scanlab.domain.model.HazardLevel.HIGH -> Triple(stringResource(R.string.high_risk), AlertRed, Color(0xFF301414))
        com.aura.scanlab.domain.model.HazardLevel.MEDIUM -> Triple(stringResource(R.string.medium_risk), com.aura.scanlab.presentation.theme.WarningOrange, Color(0xFF302414))
        com.aura.scanlab.domain.model.HazardLevel.LOW -> Triple(stringResource(R.string.low_risk), SuccessGreen, Color(0xFF143014))
    }

    val displayName = ingredient.localizedNames[currentLang] ?: ingredient.name
    val displayDesc = ingredient.localizedDescriptions[currentLang] ?: ingredient.description

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF141414)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // BADGES
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Hazard Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = hazardInfo.third
                    ) {
                        Text(
                            text = hazardInfo.first,
                            color = hazardInfo.second,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                    
                    // Category Badge
                    if (ingredient.functionalCategory.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF262626)
                        ) {
                            Text(
                                text = localizeFunctionalCategory(ingredient.functionalCategory).uppercase(),
                                color = Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = displayName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = displayDesc,
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.learn_more),
                    color = Color(0xFF00E676),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { /* Detail */ }
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // PLACEHOLDER IMAGE
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                // In a real app, use an Image with a painter
                // Here we use a subtle icon or just a gray box per mockup style
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = Color(0xFF262626),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
private fun localizeFunctionalCategory(category: String): String {
    return when (category.uppercase()) {
        "ALL" -> stringResource(R.string.category_all)
        "FOOD" -> stringResource(R.string.category_food)
        "COSMETICS" -> stringResource(R.string.category_cosmetics)
        "SWEETENER" -> stringResource(R.string.category_sweetener)
        "ACIDULANT" -> stringResource(R.string.category_acidulant)
        "COLORANT" -> stringResource(R.string.category_colorant)
        "PRESERVATIVE" -> stringResource(R.string.category_preservative)
        "FLOUR IMPROVER" -> stringResource(R.string.category_flour_improver)
        "FRAGRANCE MODIFIER" -> stringResource(R.string.category_fragrance_modifier)
        "ANTIBACTERIAL" -> stringResource(R.string.category_antibacterial)
        "SURFACTANT" -> stringResource(R.string.category_surfactant)
        else -> category.lowercase().replaceFirstChar { it.uppercase() }
    }
}
