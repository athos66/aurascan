package com.aura.scanlab.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aura.scanlab.presentation.theme.SuccessGreen

@Composable
fun ScanningOverlay(
    isMatchFound: Boolean = false,
    showInstruction: Boolean = false
) {
    val borderColor by animateColorAsState(
        targetValue = if (isMatchFound) SuccessGreen else Color.White,
        label = "borderColor"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Semi-transparent background with a clear hole for the scan area
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val scanWidth = canvasWidth * 0.8f
            val scanHeight = canvasHeight * 0.3f
            
            val left = (canvasWidth - scanWidth) / 2
            val top = (canvasHeight - scanHeight) / 2

            // Draw the dimmed background
            drawRect(
                color = Color.Black.copy(alpha = 0.7f),
                size = size
            )

            // Punch a hole (clear) for the scanning area
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(scanWidth, scanHeight),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                blendMode = BlendMode.Clear
            )
        }

        // The scan frame (border)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.3f)
                .align(Alignment.Center)
                .border(4.dp, borderColor, RoundedCornerShape(16.dp))
        )

        // Instruction text - show when no ingredients found yet
        if (showInstruction) {
            Text(
                text = "Point camera at ingredient list",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (0.15f * 1000).dp + 40.dp) // Below the scan area
                    .padding(horizontal = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanningOverlayPreview() {
    MaterialTheme {
        ScanningOverlay(isMatchFound = false, showInstruction = true)
    }
}

@Preview(showBackground = true)
@Composable
fun ScanningOverlayMatchPreview() {
    MaterialTheme {
        ScanningOverlay(isMatchFound = true, showInstruction = false)
    }
}
