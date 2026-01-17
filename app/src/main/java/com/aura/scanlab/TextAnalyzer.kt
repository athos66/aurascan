package com.aura.scanlab

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextAnalyzer(
    private val onTextRecognized: (List<String>, Long) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var frameCounter = 0

    // Note: @ExperimentalGetImage is a mandatory CameraX opt-in required to access 
    // the underlying Image object from ImageProxy for ML Kit processing.
    // It is used here as a standard library requirement, not as a preview feature.
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        frameCounter++
        // Only process every 5th frame for better stability as requested
        if (frameCounter % 5 != 0) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val startTime = System.currentTimeMillis()

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val latency = System.currentTimeMillis() - startTime
                    val lines = visionText.textBlocks.flatMap { block -> 
                        block.lines.map { it.text }
                    }
                    onTextRecognized(lines, latency)
                }
                .addOnFailureListener { e ->
                    Log.e("TextAnalyzer", "Text recognition failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
