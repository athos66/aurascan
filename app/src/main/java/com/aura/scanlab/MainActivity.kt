package com.aura.scanlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.aura.scanlab.presentation.AuraScanApp
import com.aura.scanlab.presentation.theme.AuraScanTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * The main entry point for the AuraScan Lab app.
 * This activity sets up the CameraX engine and Jetpack Compose UI.
 */
class MainActivity : ComponentActivity() {
    // Executor for camera analysis tasks to keep the UI thread responsive
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            AuraScanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Launch the main AuraScan App structure
                    AuraScanApp(cameraExecutor)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the executor to avoid memory leaks
        cameraExecutor.shutdown()
    }
}
