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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val headerText = if (isClean) "SAFE SCAN: NO HIGH RISKS" else "HIGH ALERT: $highRiskCount FLAGS FOUND"
    val headerIcon = if (isClean) Icons.Default.Info else Icons.Default.Warning

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1210)), // Very dark dark-green/black
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
                    .background(Color(0xFF1A1D1A)) // Slightly lighter dark background
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
                        text = "Scan data provided by global health databases. Consult a professional for dietary advice.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Footer Button
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to My History", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
        HazardLevel.HIGH -> "HIGH RISK"
        HazardLevel.MEDIUM -> "MEDIUM RISK"
        HazardLevel.LOW -> "LOW RISK"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ingredient.name,
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
                text = ingredient.description,
                color = Color.LightGray,
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
