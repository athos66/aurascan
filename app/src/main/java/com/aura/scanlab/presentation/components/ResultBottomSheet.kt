package com.aura.scanlab.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.aura.scanlab.domain.model.HazardLevel
import com.aura.scanlab.domain.model.Ingredient
import com.aura.scanlab.presentation.theme.AlertRed
import com.aura.scanlab.presentation.theme.SuccessGreen

@Composable
fun ResultDetailSheet(
    ingredient: Ingredient,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier.fillMaxSize().zIndex(100f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Semi-transparent scrim
            Surface(
                onClick = onDismiss,
                color = Color.Black.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxSize()
            ) {}

            // The Sheet Content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp)
                ) {
                    val hazardColor = when (ingredient.hazardLevel) {
                        HazardLevel.HIGH -> AlertRed
                        HazardLevel.MEDIUM -> Color(0xFFFF9800)
                        HazardLevel.LOW -> SuccessGreen
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(12.dp).background(hazardColor, RoundedCornerShape(2.dp))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = ingredient.hazardLevel.name,
                                color = hazardColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = ingredient.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continue Scanning")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultDetailSheetPreview() {
    MaterialTheme {
        ResultDetailSheet(
            ingredient = Ingredient(
                id = 1,
                name = "Sodium Laureth Sulfate",
                description = "A common surfactant found in many cleaning and personal care products. Can be irritating to eyes and skin.",
                hazardLevel = HazardLevel.MEDIUM
            ),
            visible = true,
            onDismiss = {}
        )
    }
}
