package com.aura.scanlab.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.aura.scanlab.R
import com.aura.scanlab.domain.model.HazardLevel
import com.aura.scanlab.domain.model.Ingredient
import com.aura.scanlab.presentation.theme.AlertRed
import com.aura.scanlab.presentation.theme.SuccessGreen
import com.aura.scanlab.presentation.theme.WarningOrange

@Composable
fun ScanResultPopup(
    ingredients: List<Ingredient>,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highRiskCount = ingredients.count { it.hazardLevel == HazardLevel.HIGH }
    val isClean = highRiskCount == 0

    val headerColor = if (isClean) SuccessGreen else AlertRed
    val headerText = if (isClean) stringResource(R.string.safe_scan) else stringResource(R.string.high_alert, highRiskCount)
    val headerIcon = if (isClean) Icons.Default.Info else Icons.Default.Warning

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Unified theme surface
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f) // Take up 75% of screen height
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Drag Handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.DarkGray)
                )
            }

            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = headerIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = headerText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            // List Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 20.dp, bottom = 20.dp)
            ) {
                items(ingredients) { ingredient ->
                    IngredientRow(ingredient)
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp),
                        color = Color.DarkGray.copy(alpha = 0.3f)
                    )
                }
            }

            // Footer Info Box
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)) // Themed variant
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.scan_disclaimer),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Footer Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = SuccessGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.save_to_history),
                        color = SuccessGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        stringResource(R.string.discard_scan),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun IngredientRow(ingredient: Ingredient) {
    val hazardColor = when (ingredient.hazardLevel) {
        HazardLevel.HIGH -> AlertRed
        HazardLevel.MEDIUM -> WarningOrange
        HazardLevel.LOW -> SuccessGreen
    }
    
    val riskText = when (ingredient.hazardLevel) {
        HazardLevel.HIGH -> stringResource(R.string.high_risk)
        HazardLevel.MEDIUM -> stringResource(R.string.medium_risk)
        HazardLevel.LOW -> stringResource(R.string.low_risk)
    }

    val currentLang = java.util.Locale.getDefault().language
    val displayName = ingredient.localizedNames[currentLang] ?: ingredient.name
    val displayDesc = ingredient.localizedDescriptions[currentLang] ?: ingredient.description

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                
                // Risk Tag
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = hazardColor.copy(alpha = 0.2f),
                ) {
                    Text(
                        text = riskText,
                        color = hazardColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayDesc,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Hazard Dot
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(hazardColor)
        )
    }
}
