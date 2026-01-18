package com.aura.scanlab.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.aura.scanlab.TextAnalyzer
import com.aura.scanlab.presentation.components.ScanResultPopup
import com.aura.scanlab.presentation.components.ScanningOverlay
import com.aura.scanlab.presentation.theme.DarkGrey
import com.aura.scanlab.presentation.theme.SuccessGreen
import java.util.concurrent.ExecutorService
import androidx.compose.ui.tooling.preview.Preview as ComposePreview

@Composable
fun HomeScreen(
    cameraExecutor: ExecutorService
) {
    val context = LocalContext.current
    val viewModel = remember { HomeViewModel(context) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasCameraPermission = it }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        HomeScreenContent(
            cameraExecutor = cameraExecutor,
            viewModel = viewModel
        )
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(androidx.compose.ui.res.stringResource(com.aura.scanlab.R.string.camera_required))
        }
    }
}

@Composable
fun HomeScreenContent(
    cameraExecutor: ExecutorService,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Flashlight state
    var isFlashOn by remember { mutableStateOf(false) }
    var camera: Camera? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        val previewView = remember { PreviewView(context) }

        LaunchedEffect(Unit) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, TextAnalyzer { lines, lat ->
                        viewModel.onTextDetected(lines, lat)
                    })
                }

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("HomeScreen", "Binding failed", e)
            }
        }

        // Camera Preview
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // UI Layer
        ScanningOverlay(
            isMatchFound = viewModel.matchedIngredients.isNotEmpty(),
            showInstruction = false // Changed to false as we now have "Analyzing Ingredients" capsule
        )
        
        // --- TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding() // Handle status bar
                .padding(top = 0.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Capsule
            TopStatusCapsule(text = androidx.compose.ui.res.stringResource(com.aura.scanlab.R.string.analyzing_ingredients))
            
            // Flashlight Button
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FlashlightButton(
                    isFlashOn = isFlashOn,
                    onClick = {
                        isFlashOn = !isFlashOn
                        camera?.cameraControl?.enableTorch(isFlashOn)
                    }
                )
            }
        }

        // --- BOTTOM AREA ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp), // Space for Nav Bar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ingredient Chips (Moved up)
            if (viewModel.currentSessionIngredients.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp), // Increased spacing
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.currentSessionIngredients.toList().sortedBy { it.name }, key = { it.id }) { ingredient ->
                        IngredientChip(ingredient)
                    }
                }
            }

            // Mode Switcher
            ModeSwitcher(
                selectedMode = viewModel.selectedMode,
                onModeSelected = { viewModel.setMode(it) }
            )
        }

        // "Scan Complete" indicator
        AnimatedVisibility(
            visible = viewModel.showScanComplete,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(com.aura.scanlab.R.string.complete),
                        tint = Color.White
                    )
                    Text(androidx.compose.ui.res.stringResource(com.aura.scanlab.R.string.scan_complete), color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Result Popup
        AnimatedVisibility(
            visible = viewModel.showResultPopup,
            enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }),
            exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ScanResultPopup(
                ingredients = viewModel.currentSessionIngredients.toList().sortedByDescending { it.hazardLevel }, // High risk first
                onSave = { viewModel.saveAndDismiss() },
                onDismiss = { viewModel.dismissPopup() }
            )
        }
    }
}

// --- NEW COMPONENTS ---

@Composable
fun TopStatusCapsule(text: String) {
    Surface(
        shape = RoundedCornerShape(50), // Fully rounded capsule
        color = Color.Black.copy(alpha = 0.6f),
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Pulsing dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4285F4)) // Blue dot
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



@Composable
fun FlashlightButton(isFlashOn: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.6f),
            contentColor = Color.White
        ),
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription = stringResource(com.aura.scanlab.R.string.flashlight),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ModeSwitcher(
    selectedMode: Int,
    onModeSelected: (Int) -> Unit
) {
    // Mode switcher container
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.8f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable { onModeSelected(0) }
                    .background(if (selectedMode == 0) Color(0xFF4285F4) else Color.Transparent), // Blue when selected
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(com.aura.scanlab.R.string.mode_food),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            // Cosmetics Mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable { onModeSelected(1) }
                    .background(if (selectedMode == 1) Color(0xFFE91E63) else Color.Transparent), // Pink/Fuchsia
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(com.aura.scanlab.R.string.mode_cosmetics),
                    color = Color.White.copy(alpha = 0.7f), // Dimmed when not selected
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun IngredientChip(ingredient: com.aura.scanlab.domain.model.Ingredient) {
    val isHighHazard = ingredient.hazardLevel == com.aura.scanlab.domain.model.HazardLevel.HIGH
    val currentLang = java.util.Locale.getDefault().language
    val displayName = ingredient.localizedNames[currentLang] ?: ingredient.name
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighHazard) 
                com.aura.scanlab.presentation.theme.AlertRed.copy(alpha = 0.9f) 
            else 
                Color.White.copy(alpha = 0.9f)
        )
    ) {
        Text(
            text = displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isHighHazard) Color.White else DarkGrey
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            // Mock UI preview
            Column {
                Spacer(Modifier.height(40.dp))
                Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween) {
                    TopStatusCapsule("Analyzing Ingredients...")
                    FlashlightButton(false) {}
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
